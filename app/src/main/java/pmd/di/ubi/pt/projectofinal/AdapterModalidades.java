package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

public class AdapterModalidades extends RecyclerView.Adapter<AdapterModalidades.ModalidadesHolder> {
    private FragmentActivity context;
    private ArrayList<Map<String,Object>> modalidadeList;

    public AdapterModalidades(FragmentActivity context, ArrayList<Map<String,Object>>  modalidadeList){
        this.context = context;
        this.modalidadeList = modalidadeList;
    }


    @NonNull
    @Override
    public ModalidadesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_modalidade,parent,false);
        return new ModalidadesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModalidadesHolder holder, int position) {

        Map<String, Object> modalidade = modalidadeList.get(position);
        holder.setDetails(modalidade);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return modalidadeList.size();    }



    public class ModalidadesHolder extends RecyclerView.ViewHolder {
        public ModalidadesHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDetails(Map<String, Object> modalidade) {
            final TextView txtTitulo = itemView.findViewById(R.id.titulo);
            final ImageView imgModalidade = itemView.findViewById(R.id.modalidade_imagem);
            final LinearLayout main = (LinearLayout) itemView.findViewById(R.id.linear);

            final String nomeModalidade = String.valueOf(modalidade.get("nome"));
            final String gymNome = String.valueOf(modalidade.get("gym"));

            txtTitulo.setText(nomeModalidade);

            //imgRestaurant.setImageResource(modalidade.getImg());
            Glide.with(imgModalidade.getContext())
                    .load(modalidade.get("img"))
                    .into(imgModalidade);

            main.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("modalidade",nomeModalidade );

                if(gymNome.equals("nao")){
                    Navigation.findNavController(v).navigate(R.id.action_modalidadesFragment_to_personalsFragment,bundle);
                }else {
                    bundle.putString("gymNome",gymNome);
                    Navigation.findNavController(v).navigate(R.id.action_modalidadesFragment_to_fragmentGymPerfil,bundle);
                }
            });
        }
    }
}
