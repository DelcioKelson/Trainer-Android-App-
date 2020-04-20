package pmd.di.ubi.pt.projectofinal;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import androidx.navigation.fragment.FragmentNavigator;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialFade;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FragmentPersoanlPerfil extends Fragment {

    private static final String ARG_PARAM1 = "uidPersonal";
    private String uidPersonal;
    private ImageView ivPerfil;
    private TextView nome,tvInfo;
    private String preco;
    private FirebaseUser user;
    private RatingBar rbPerfil;
    private CollectionReference pessoaRef =  FirebaseFirestore.getInstance().collection("pessoas");
    private byte[] imageBytes=null;
    private View view;
    private boolean isUser;
    private String nomePersonal;
    private String ratingText;


    private int via;

    public FragmentPersoanlPerfil() {
        // Required empty public constructor
    }

    public static FragmentPersoanlPerfil newInstance(Bundle bundle) {

        FragmentPersoanlPerfil fragmentPersoanlPerfil = new FragmentPersoanlPerfil();
        fragmentPersoanlPerfil.setArguments(bundle);
        return fragmentPersoanlPerfil;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uidPersonal = getArguments().getString(ARG_PARAM1);
            imageBytes = getArguments().getByteArray("imageBytes");
            via = getArguments().getInt("via");
            nomePersonal = getArguments().getString("nome");
            ratingText = getArguments().getString("rating");

        }
        SharedDataModel modelData = new ViewModelProvider(getActivity()).get(SharedDataModel.class);
        isUser = modelData.isUser().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_personal_trainer_perfil, container, false);
        nome = view.findViewById(R.id.tv_nome_perfil);


        ivPerfil = view.findViewById(R.id.iv_perfil);
        rbPerfil = view.findViewById(R.id.rb_perfil);
        user = FirebaseAuth.getInstance().getCurrentUser();
        tvInfo= view.findViewById(R.id.tv_info_perfil);

        if (savedInstanceState == null && via==2) {
            postponeEnterTransition();
        }


        if(!isUser){
            uidPersonal = user.getUid();

            Bundle bundle = new Bundle();
            bundle.putString("uidPersonal",uidPersonal);
            bundle.putString("preco",preco);
            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_comentarios_preco);

            NavHostFragment.findNavController(fragment).setGraph(R.navigation.nav_graph_comentarios,bundle);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+uidPersonal);
            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if(bytes.length!=0){
                    try {
                        Glide.with(getActivity()).load(bytes).into(ivPerfil);

                    }catch (Exception ignored){
                    }
                }
            });

            FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).collection("notificacoes").whereEqualTo("vista",false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && task.getResult()!=null && task.getResult().size()>0){
                        ActivityMain.badge.setVisible(true);
                        ActivityMain.badge.setNumber(task.getResult().size());
                        Log.i("notificacoes",task.getResult().size()+"");
                    }
                }
            });
            pessoaRef.document(uidPersonal).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult() ;
                    if (document!=null) {
                        String nometxt = document.getString("nome");
                        preco = document.getString("preco");
                        nome.setText(nometxt);
                        try {
                            float rating = Float.parseFloat((String) document.get("rating"));
                            if (rating!=0){
                                rbPerfil.setVisibility(View.VISIBLE);
                                rbPerfil.setRating(rating);
                            }else {
                                tvInfo.setVisibility(View.VISIBLE);
                            }

                        }catch (Exception i){

                        }
                    }
                }
            });

        }else {
            ivPerfil.setTransitionName(uidPersonal);
        }

        initPerfil();
        return view;
    }

    public void initPerfil(){

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_comentarios_preco);

        if (isUser) {
            NavHostFragment.findNavController(fragment).setGraph(R.navigation.nav_graph_gerar_preco_comentarios,getArguments());
            if(via==2){
                try {
                    Glide.with(getActivity()).load(imageBytes).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                                target, boolean isFirstResource) {
                            // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                            // startPostponedEnterTransition() should also be called on it to get the transition
                            // going in case of a failure.
                            getParentFragment().startPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                            // startPostponedEnterTransition() should also be called on it to get the transition
                            // going when the image is ready.
                            getParentFragment().startPostponedEnterTransition();
                            return false;
                        }
                    })
                            .into(ivPerfil);

                } catch (Exception e) {

                }
            }else {

                try {
                    Glide.with(getActivity()).load(imageBytes).into(ivPerfil);

                }catch (Exception ignored){
                }
            }

            float rating = Float.parseFloat(ratingText);
            if (rating!=0){
                rbPerfil.setVisibility(View.VISIBLE);
                rbPerfil.setRating(rating);
            }

            nome.setText(nomePersonal);


        }
    }
}
