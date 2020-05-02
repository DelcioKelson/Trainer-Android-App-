package pmd.di.ubi.pt.projectofinal;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdapterPersonalTreiners extends RecyclerView.Adapter<AdapterPersonalTreiners.PersonalHolder> {

    private Fragment fragment;
    private ArrayList<Map<String, Object>> personalTrainerList;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Map<String,Boolean> favorite;


    private interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition, ArrayList<Map<String,Object>> personalList);
    }

    private final ViewHolderListener viewHolderListener;
    private final RequestManager requestManager;

    AdapterPersonalTreiners(Fragment fragment, ArrayList<Map<String, Object>> personalTrainerList){
        this.personalTrainerList = personalTrainerList;
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
        this.fragment =fragment;

    }
    @NonNull
    @Override
    public PersonalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.card_personaltrainer,parent,false);
        return new PersonalHolder(view,requestManager,viewHolderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPersonalTreiners.PersonalHolder holder, int position) {

         Map<String, Object> personalTrainer = personalTrainerList.get(position);
        holder.setDetails(personalTrainer,position);

    }

    @Override
    public int getItemCount() {
        return personalTrainerList.size();
    }

    class PersonalHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        private Bundle bundle = null;

        private RequestManager requestManager;
        public PersonalHolder(@NonNull View itemView, RequestManager requestManager, ViewHolderListener viewHolderListener) {
            super(itemView);
            this.requestManager =requestManager;
        }

        void setDetails(final Map<String, Object> personalTrainer,int position ){
            final TextView tvNome = itemView.findViewById(R.id.nomePersonal);
            final ImageView imgPersonalTrainer = itemView.findViewById(R.id.personal_image);
            final RatingBar ratingBar =  itemView.findViewById(R.id.rating);
            final TextView tvPreco = itemView.findViewById(R.id.tv_preco_card);
            final ToggleButton heartToggle = itemView.findViewById(R.id.button_favorite);


            bundle = new Bundle();
            try {
                favorite = (Map<String, Boolean>) personalTrainer.get("favorito");
                heartToggle.setChecked(favorite.get(user.getUid()).booleanValue());
            }catch (Exception e){ }

            String uid = (String) personalTrainer.get("uid");
            final String nomePersonal = (String) personalTrainer.get("nome");
            tvNome.setText("Nome: " + nomePersonal);
            String preco = (String) personalTrainer.get("preco");
            tvPreco.setText("preço: " + preco+ "€");
            bundle.putString("uidPersonal", uid);
            bundle.putInt("size",personalTrainerList.size());


            heartToggle.setOnClickListener(v -> {
                boolean b = heartToggle.isChecked();
                favorite.put(user.getUid(),b);
                FirebaseFirestore.getInstance().collection("pessoas").document(uid).update("favorito",favorite);
                if(b){
                    Snackbar.make(v,nomePersonal + " foi adicionado a sua lista de favoritos",Snackbar.LENGTH_LONG).show();

                }else {
                    Snackbar.make(v,nomePersonal + " foi removido da sua lista de favoritos",Snackbar.LENGTH_LONG).show();
                }

            });

            try {
                float classificao = Float.parseFloat(( String)personalTrainer.get("rating"));

                if(classificao!=0){
                    if ( classificao>0){
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setRating(Float.parseFloat(""+classificao));
                    }
                }
            }
            catch (Exception ignored){

            }


            imgPersonalTrainer.setTransitionName(uid);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+ uid);

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if(bytes.length!=0){
                    try {
                        personalTrainerList.get(position).put("imageBytes",bytes);
                        itemView.findViewById(R.id.card_personal).setOnClickListener(this);

                        requestManager
                                .load(bytes)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        viewHolderListener.onLoadCompleted(imgPersonalTrainer, position);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        viewHolderListener.onLoadCompleted(imgPersonalTrainer, position);
                                        return false;
                                    }
                                })
                                .into(imgPersonalTrainer);
                    }catch (Exception ignored){
                    }


                }
            });

        }

        @Override
        public void onClick(View v) {

            viewHolderListener.onItemClicked(v, getAdapterPosition(),personalTrainerList);

        }
    }

    private static class ViewHolderListenerImpl implements ViewHolderListener {

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }

        @Override
        public void onLoadCompleted(ImageView view, int position) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (Main.currentPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        @Override
        public void onItemClicked(View view, int adapterPosition,  ArrayList<Map<String,Object>> personalList) {

            Main.currentPosition = adapterPosition;
            ((Hold) fragment.getExitTransition()).excludeTarget(view, true);

            ImageView imgPersonalTrainer = view.findViewById(R.id.personal_image);

            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(imgPersonalTrainer, imgPersonalTrainer.getTransitionName())
                    .build();
            Navigation.findNavController(view).navigate(R.id.action_personalsFragment_to_imageHelp,null,null,extras);

        }
    }
}
