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

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class AdapterPersonalFavoritosRecentes extends RecyclerView.Adapter<AdapterPersonalFavoritosRecentes.PersonalHolder> {
    private Context context;
    private ArrayList<Map<String, Object>> personalTrainerList;
    private boolean favoritoList;

    AdapterPersonalFavoritosRecentes(Context context, ArrayList<Map<String, Object>> personalTrainerList, boolean favoritoList) {
        this.context = context;
        this.personalTrainerList = personalTrainerList;
        this.favoritoList = favoritoList;

    }

    @NonNull
    @Override
    public AdapterPersonalFavoritosRecentes.PersonalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_personal_simples, parent, false);
        return new AdapterPersonalFavoritosRecentes.PersonalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPersonalFavoritosRecentes.PersonalHolder holder, int position) {
        Map<String, Object> personalTrainer = personalTrainerList.get(position);
        holder.setDetails(personalTrainer);
    }

    @Override
    public int getItemCount() {
        return personalTrainerList.size();
    }

    class PersonalHolder extends RecyclerView.ViewHolder {


        public PersonalHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setDetails(final Map<String, Object> personalTrainer) {
            final TextView tvNome = itemView.findViewById(R.id.nomePersonal_simples);
            final ImageView imgPersonalTrainer = itemView.findViewById(R.id.personal_image_simples);
            final LinearLayout main = itemView.findViewById(R.id.card_personal_simples);
            final RatingBar ratingBar = itemView.findViewById(R.id.rating_simples);
            final TextView tvPreco = itemView.findViewById(R.id.tv_preco_card_simples);

            String uid = (String) personalTrainer.get("uid");
            String nomePersonal = (String) personalTrainer.get("nome");
            tvNome.setText("Nome: " + nomePersonal);
            String preco = (String) personalTrainer.get("preco");
            tvPreco.setText("preço: " + preco + "€");


            try {
                float classificao = Float.parseFloat((String) personalTrainer.get("rating"));

                if (classificao != 0) {
                    if (classificao > 0) {
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setRating(Float.parseFloat("" + classificao));
                    }
                }
            } catch (Exception ignored) {
            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/" + uid);

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if (bytes.length != 0) {
                    try {
                        personalTrainerList.get(getLayoutPosition()).put("imageBytes", bytes);
                        Glide.with(context.getApplicationContext())
                                .load(bytes)
                                .into(imgPersonalTrainer);
                    } catch (Exception ignored) {
                    }
                    main.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();

                        bundle.putInt("via", 1);
                        bundle.putInt("posPersonal", getLayoutPosition());
                        bundle.putBoolean("favorito", favoritoList);

                        Navigation.findNavController(v).navigate(R.id.action_fragmentTodasMarcacoes_to_persoanlPerfilFragment, bundle);
                    });
                }
            });
        }
    }

}
