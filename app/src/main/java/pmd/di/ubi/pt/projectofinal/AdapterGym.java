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

public class AdapterGym extends RecyclerView.Adapter<AdapterGym.gymHolder> {

    private Context context;
    private ArrayList<Map<String, Object>> gymsList;

    public AdapterGym(Context context, ArrayList<Map<String, Object>> gymsList) {
        this.context = context;
        this.gymsList = gymsList;
    }

    @NonNull
    @Override
    public gymHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_gym,parent,false);
        return new gymHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull gymHolder holder, int position) {
        Map<String, Object> gym = gymsList.get(position);
        holder.setDetails(gym);
    }


    @Override
    public int getItemCount() {
        return gymsList!=null?gymsList.size():0;
    }


    public class gymHolder extends RecyclerView.ViewHolder {
        public gymHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDetails(Map<String, Object> gym) {
            final TextView txtComentador = itemView.findViewById(R.id.nome_gym);
            final TextView txtComentario = itemView.findViewById(R.id.endereço_gym);
            final LinearLayout gymLayout = itemView.findViewById(R.id.layout_gym);

            String nome = (String) gym.get("nome");

            txtComentador.setText(nome);
            txtComentario.setText((String)gym.get("endereço"));

            final Bundle bundle = new Bundle();
            bundle.putString("nome",nome);
            gymLayout.setOnClickListener(v ->  Navigation.findNavController(v).navigate(R.id.action_fragmentMapList_to_modalidadesFragment2,bundle));

        }
    }
}
