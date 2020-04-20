package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentComentariosGerarPreco#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentComentariosGerarPreco extends Fragment {

    private RatingBar comentarioRating;
    private EditText inputComentario;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private AlertDialog.Builder alertDialog;
    private ArrayList<Map<String, Object>> comentariosList;
    private AdapterComentario adapterComentario;
    private DocumentSnapshot document;
    private int numeroComentarios;
    private float soma;
    int indice;
    private boolean isUser;
    private CollectionReference comentariosRef = FirebaseFirestore.getInstance().collection("comentarios");
    private CollectionReference marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");
    private CollectionReference pessoaRef =  FirebaseFirestore.getInstance().collection("pessoas");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "preco";
    private static final String ARG_PARAM2 = "uidPersonal";

    // TODO: Rename and change types of parameters
    private String preco;
    private String uidPersonal;

    public FragmentComentariosGerarPreco() {
        // Required empty public constructor
    }

    public static FragmentComentariosGerarPreco newInstance(Bundle bundle) {
        FragmentComentariosGerarPreco fragment = new FragmentComentariosGerarPreco();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            preco = getArguments().getString(ARG_PARAM1);
            uidPersonal = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comentarios, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.comentario_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnComentar = view.findViewById(R.id.btn_comentar);
        Button btnVoltar = view.findViewById(R.id.btn_voltar);
        comentariosList = new ArrayList<Map<String, Object>>();
        numeroComentarios=0;
        document = null;

        Log.i("FragmentoComentario","passei");

        user = FirebaseAuth.getInstance().getCurrentUser();
        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        isUser = modelData.isUser().getValue();
        btnComentar.setVisibility(View.GONE);
        //sonal ==null quer dizer que eh a conta do tipo personal


        intComentarios();

        if(!isUser){
            uidPersonal = user.getUid();
            btnVoltar.setVisibility(View.GONE);
        }else {
            marcacoesRef.whereEqualTo("uidUsuario",user.getUid())
                    .whereEqualTo("estado","terminada").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult()!=null && task.getResult().size()>0){
                    btnComentar.setVisibility(View.VISIBLE);
                    btnComentar.setOnClickListener(v -> {
                        criarDialogoComentario();
                    });
                }
            });
            btnVoltar.setOnClickListener(v -> {
                Navigation.findNavController(v).popBackStack();

            });


        }


        return view;
    }

    public void criarDialogoComentario(){
        alertDialog.setTitle("adicione uma avaliaçao");
        alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.avaliacao_layout_dialog,null);
        alertDialog.setView(dialogView);
        inputComentario = dialogView.findViewById(R.id.comentario_dialog_txtv);
        comentarioRating =dialogView.findViewById(R.id.comentario_rating);

        if(document!=null){
            inputComentario.setText(document.getString("comentario"));
            comentarioRating.setRating(Float.parseFloat(document.getString("ratingComentario")));
        }
        alertDialog.setPositiveButton("Adicionar", (dialog, whichButton) -> {
            HashMap<String, Object> comentarioData;
            comentarioData = new HashMap<String, Object>();
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            comentarioData.put("diaComentario", day + "/" + month + "/" + year );
            comentarioData.put("nomeComentador", user.getDisplayName());
            comentarioData.put("uidUsuario", user.getUid());
            comentarioData.put("comentario", inputComentario.getText().toString());
            comentarioData.put("uidPersonal", uidPersonal);
            comentarioData.put("ratingComentario",""+comentarioRating.getRating());
            if(document!=null){
                soma = soma - Float.parseFloat((String) document.get("ratingComentario"));
                numeroComentarios = numeroComentarios-1;
                comentariosList.set(indice,comentarioData);
                document.getReference().set(comentarioData);
            }else {
                comentariosRef.document().set(comentarioData);
                comentariosList.add(comentarioData);
            }
            soma = comentarioRating.getRating() + soma;
            pessoaRef.document(uidPersonal).update("rating",""+soma/numeroComentarios+1);

            recyclerView.setAdapter(new AdapterComentario(getActivity(), comentariosList));
        }).setNegativeButton("Cancelar", (dialog, whichButton) -> { }).show();
    }

    public void intComentarios(){
        Log.i("FragmentoComentario",""+uidPersonal);


        comentariosRef.whereEqualTo("uidPersonal",uidPersonal).get().addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()&& task12.getResult()!=null){
                soma=0.0f;
                numeroComentarios = task12.getResult().size();
                indice =0;
                int indiceAux = 0;
                for (DocumentSnapshot documentSnapshot : task12.getResult()){
                    comentariosList.add(documentSnapshot.getData());

                    soma = Float.parseFloat((String) documentSnapshot.get("ratingComentario")) + soma;
                    if(documentSnapshot.get("uidUsuario").equals(user.getUid())){
                        document=documentSnapshot;
                        indice =indiceAux;
                    }
                    indiceAux =indiceAux+ 1;
                }

                Log.i("FragmentoComentario",comentariosList.toString());


                adapterComentario = new AdapterComentario(getActivity(), comentariosList);
                recyclerView.setAdapter(adapterComentario);
            }
        });
    }

}
