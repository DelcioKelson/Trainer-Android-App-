package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPersoanlPerfil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPersoanlPerfil extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "uidPersonal";

    // TODO: Rename and change types of parameters
    private String uidPersonal;

    private ImageView ivPerfil;
    private TextView nome,tvInfoPerfil,tvNumeroComentario;
    private RatingBar rbPerfil,comentarioRating;
    private EditText inputComentario;
    private FirebaseUser user;
    private GridView gridView;
    private AlertDialog.Builder alertDialog;
    private ArrayList<Map<String, String>> comentariosList;
    private AdapterComentario adapterComentario;
    private int userPersonalFlag;
    private DocumentSnapshot document;
    private int numeroComentarios;
    private float soma;
    int indice;
    private boolean isUser;

    private CollectionReference pessoaRef =  FirebaseFirestore.getInstance().collection("pessoas");
    private CollectionReference comentariosRef = FirebaseFirestore.getInstance().collection("comentarios");
    private CollectionReference marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");

    public FragmentPersoanlPerfil() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersoanlPerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPersoanlPerfil newInstance(String param1) {
        FragmentPersoanlPerfil fragment = new FragmentPersoanlPerfil();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uidPersonal = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_trainer_perfil, container, false);

        nome = view.findViewById(R.id.tv_nome_perfil);
        tvInfoPerfil=view.findViewById(R.id.tv_info_perfil);
        ivPerfil = view.findViewById(R.id.iv_perfil);
        rbPerfil = view.findViewById(R.id.rb_perfil);

        ExtendedFloatingActionButton btnComentar = view.findViewById(R.id.btn_comentar);
        ExtendedFloatingActionButton btnGerarPreco = view.findViewById(R.id.btn_gerar_preco);

        tvNumeroComentario = view.findViewById(R.id.tv_numero_comentarios);
        gridView = view.findViewById(R.id.gridview_comentario);
        comentariosList = new ArrayList<Map<String, String>>();

        numeroComentarios=0;
        document = null;
        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        isUser = modelData.isUser().getValue();



        userPersonalFlag=0;

        btnComentar.setVisibility(View.GONE);
//uidPersonal ==null quer dizer que eh a conta do tipo personal
        if(!isUser){
            uidPersonal = user.getUid();
            userPersonalFlag=1;
            btnGerarPreco.setVisibility(View.GONE);
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

            btnGerarPreco.setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putString("uidPersonal", String.valueOf(uidPersonal));
                Navigation.findNavController(v).navigate(R.id.action_persoanlPerfilFragment_to_gerarMarcacaoFragment,bundle);

              
            });
        }

        initPerfil();
        //opecoes de navgation slide

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
            Map<String, String> comentarioData;
            comentarioData = new HashMap<>();
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
            pessoaRef.document(uidPersonal).update("classificacao",soma/numeroComentarios+1);

            gridView.setAdapter(new AdapterComentario(getActivity(), comentariosList));
        }).setNegativeButton("Cancelar", (dialog, whichButton) -> { }).show();
    }

    public void initPerfil(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+ (uidPersonal+".jpeg"));
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            if(bytes.length!=0){
                try {
                    Glide.with(getActivity()).load(bytes).into(ivPerfil);
                }catch (Exception ignored){

                }

            }
        });

        pessoaRef.document(uidPersonal).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult() ;
                if (document!=null) {
                    if(userPersonalFlag==1){
                       // tvNavHeader.setText("Nome: " + document.getString("nome"));
                    }
                    nome.setText(document.getString("nome"));
                }
            }
        });

        comentariosRef.whereEqualTo("uidPersonal",uidPersonal).get().addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()&& task12.getResult()!=null){
                soma=0.0f;
                numeroComentarios = task12.getResult().size();
                indice =0;
                int indiceAux = 0;
                for (DocumentSnapshot documentSnapshot : task12.getResult()){
                    comentariosList.add(MapObjectToSring(documentSnapshot));
                    soma = Float.parseFloat((String) documentSnapshot.get("ratingComentario")) + soma;
                    if(documentSnapshot.get("uidUsuario").equals(user.getUid())){
                        document=documentSnapshot;
                        indice =indiceAux;
                    }
                    indiceAux =indiceAux+ 1;

                }

                Log.i("listaComentarios",comentariosList.toString());
                if(numeroComentarios==0) {
                    tvInfoPerfil.setVisibility(View.VISIBLE);
                }
                else {
                    rbPerfil.setVisibility(View.VISIBLE);
                    rbPerfil.setRating(soma/numeroComentarios);
                    tvNumeroComentario.setVisibility(View.VISIBLE);
                    if(numeroComentarios==1){
                        tvNumeroComentario.setText("1 comentario");
                    }else {
                        tvNumeroComentario.setText(numeroComentarios + " comentarios");
                    }
                    adapterComentario = new AdapterComentario(getActivity(), comentariosList);
                    gridView.setAdapter(adapterComentario);
                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent ;
        switch (item.getItemId()) {
            case R.id.bar_opcao_conta:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, FragmentDefinicoesConta.newInstance())
                        .commit();
                return true;
            case android.R.id.home:
                if(userPersonalFlag==0){
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public Map<String, String> MapObjectToSring(DocumentSnapshot documentSnapshot){
        Map<String,String> newMap =new HashMap<String,String>();
        for (Map.Entry<String, Object> entry : Objects.requireNonNull(documentSnapshot.getData()).entrySet()) {
            if(entry.getValue() instanceof String){
                newMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return newMap;
    }
}
