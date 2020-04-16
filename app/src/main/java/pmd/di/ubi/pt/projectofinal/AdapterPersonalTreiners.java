package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class AdapterPersonalTreiner extends RecyclerView.Adapter<AdapterPersonalTreiner.PersonalHolder> {

    private Context context;
    private ArrayList<Map<String, Object>> personalTrainerList;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Map<String,Boolean> favorite;


    AdapterPersonalTreiner(Context context, ArrayList<Map<String, Object>> personalTrainerList){
        this.context = context;
        this.personalTrainerList = personalTrainerList;
    }
    @NonNull
    @Override
    public PersonalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_personaltrainer,parent,false);
        return new PersonalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPersonalTreiner.PersonalHolder holder, int position) {

         Map<String, Object> personalTrainer = personalTrainerList.get(position);
        holder.setDetails(personalTrainer,position);

    }

    @Override
    public int getItemCount() {
        return personalTrainerList.size();
    }

    class PersonalHolder extends RecyclerView.ViewHolder{


        public PersonalHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setDetails(final Map<String, Object> personalTrainer,int position ){
            final TextView tvNome = itemView.findViewById(R.id.nomePersonal);
            final ImageView imgPersonalTrainer = itemView.findViewById(R.id.personal_image);
            final LinearLayout main = itemView.findViewById(R.id.card_personal);
            final RatingBar ratingBar =  itemView.findViewById(R.id.rating);
            final TextView txtInfo = itemView.findViewById(R.id.infoclassificacao);
            final TextView tvPreco = itemView.findViewById(R.id.tv_preco_card);
            final ToggleButton heartToggle = itemView.findViewById(R.id.button_favorite);

            imgPersonalTrainer.setTransitionName("image"+position);
            tvNome.setTransitionName("nome"+position);
            try {
                favorite = (Map<String, Boolean>) personalTrainer.get("favorito");
                heartToggle.setChecked(favorite.get(user.getUid()).booleanValue());
            }catch (Exception e){ }

            String uid = (String) personalTrainer.get("uid");
            final String nomePersonal = (String) personalTrainer.get("nome");
            tvNome.setText("Nome: " + nomePersonal);
            String preco = (String) personalTrainer.get("preco");
            tvPreco.setText("preço: " + preco+ "€");

            View finalConvertView = itemView;

            heartToggle.setOnClickListener(v -> {
                boolean b = heartToggle.isChecked();
                favorite.put(user.getUid(),b);
                FirebaseFirestore.getInstance().collection("pessoas").document(uid).update("favorito",favorite);
                if(b){
                    Snackbar.make(finalConvertView,nomePersonal + " foi adicionado a sua lista de favoritos",Snackbar.LENGTH_LONG).show();

                }else {
                    Snackbar.make(finalConvertView,nomePersonal + " foi removido da sua lista de favoritos",Snackbar.LENGTH_LONG).show();
                }

            });

            try {
                float classificao = Float.parseFloat(( String)personalTrainer.get("rating"));

                if(classificao!=0){
                    if ( classificao>0){
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setRating(Float.parseFloat(""+classificao));
                    }
                }else {
                    txtInfo.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception ignored){

            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+ uid + ".jpeg");

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if(bytes.length!=0){
                    try {
                        Glide.with(context.getApplicationContext() )
                                .load(bytes)
                                .into(imgPersonalTrainer);
                    }catch (Exception ignored){
                    }

                    main.setOnClickListener(v -> {

                        Bundle bundle = new Bundle();
                        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                                .addSharedElement(imgPersonalTrainer, "imageView")
                                .addSharedElement(tvNome, "nome")
                                .build();
                        bundle.putString("uidPersonal", uid);
                        bundle.putByteArray("imageBytes",bytes);
                        Navigation.findNavController(v).navigate(R.id.action_personalsFragment_to_persoanlPerfilFragment,bundle,null,extras);
                    });
                }
            });

        }
    }
}
