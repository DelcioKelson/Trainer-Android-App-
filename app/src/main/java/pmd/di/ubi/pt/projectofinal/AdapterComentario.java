package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.ComentarioHolder> {

    private Context context;
    private ArrayList<Map<String, Object>> comentariosList;

    public AdapterComentario(Context context, ArrayList<Map<String, Object>> comentariosList) {
        this.context = context;
        this.comentariosList = comentariosList;
    }

    @NonNull
    @Override
    public ComentarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_comentario,parent,false);
        return new ComentarioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioHolder holder, int position) {
        Map<String, Object> comentario = comentariosList.get(position);
        holder.setDetails(comentario);
    }


    @Override
    public int getItemCount() {
        return comentariosList!=null?comentariosList.size():0;
    }


    public class ComentarioHolder extends RecyclerView.ViewHolder {
        public ComentarioHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDetails(Map<String, Object> comentario) {
            final TextView txtComentador = itemView.findViewById(R.id.nome_comentador);
            final TextView txtComentario = itemView.findViewById(R.id.comentario);
            final RatingBar ratingBar = itemView.findViewById(R.id.comentario_rating);
            final ImageView imgPerfil = itemView.findViewById(R.id.img_comentador);

            txtComentador.setText((String) comentario.get("nomeComentador"));
            txtComentario.setText((String)comentario.get("comentario"));

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("image/"+comentario.get("uidUsuario"));

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if(bytes.length!=0){
                    try {
                        Glide.with(context.getApplicationContext())
                                .load(bytes)
                                .into(imgPerfil);
                    }catch (Exception ignored){

                    }
                }
            });
            ratingBar.setRating(Float.parseFloat((String) comentario.get("ratingComentario")));
        }
    }
}
