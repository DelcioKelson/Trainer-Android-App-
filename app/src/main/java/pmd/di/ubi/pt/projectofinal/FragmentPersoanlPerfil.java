package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FragmentPersoanlPerfil extends Fragment {

    private static final String ARG_PARAM1 = "uidPersonal";
    private String uidPersonal;
    private ImageView ivPerfil;
    private TextView nome;
    private String preco;
    private FirebaseUser user;
    private RatingBar rbPerfil;
    private CollectionReference pessoaRef =  FirebaseFirestore.getInstance().collection("pessoas");

    public FragmentPersoanlPerfil() {
        // Required empty public constructor
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
        ivPerfil = view.findViewById(R.id.iv_perfil);
        rbPerfil = view.findViewById(R.id.rb_perfil);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(uidPersonal==null){
            uidPersonal = user.getUid();
        }

        initPerfil();
        return view;
    }

    public void initPerfil(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+uidPersonal+".jpeg");
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
                    nome.setText(document.getString("nome"));
                    preco = document.getString("preco");
                    try {


                    float rating = Float.parseFloat((String) document.get("rating"));
                    if (rating!=0){
                        rbPerfil.setVisibility(View.VISIBLE);
                        rbPerfil.setRating(rating);
                    }
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("uidPersonal",uidPersonal);
                    bundle.putString("preco",preco);
                    Fragment newFragment = FragmentComentariosGerarPreco.newInstance(bundle);
                    ft.replace(R.id.fragment_comentarios,newFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                    }catch (Exception i){

                    }
                }
            }
        });
    }
}
