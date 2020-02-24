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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class AdapterComentario extends BaseAdapter {

    private Context context;
    private ArrayList<Comentario> comentariosList;

    public AdapterComentario(Context context, ArrayList<Comentario> comentariosList) {
        this.context = context;
        this.comentariosList = comentariosList;
    }

    public void setClassificacoes(ArrayList<Comentario> comentariosList) {
        this.comentariosList = comentariosList;
    }

    @Override
    public int getCount() {
        return comentariosList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Comentario comentario = comentariosList.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_comentario, null);
        }
        Log.d("FirebaseFirestore", "comentarioAp");

        final TextView txtComentador = convertView.findViewById(R.id.nome_comentador);
        final TextView txtComentario = convertView.findViewById(R.id.comentario);
        final RatingBar ratingBar = convertView.findViewById(R.id.comentario_rating);
        final ImageView imgPerfil = convertView.findViewById(R.id.img_comentador);
        FirebaseFirestore.getInstance().collection("/pessoas").document(Objects.requireNonNull(comentario.getComentador())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    Usuario comentador = document.toObject(Usuario.class);
                    txtComentador.setText(comentador.getNome());
                    txtComentario.setText(comentario.getComentario());
                    Glide.with(imgPerfil.getContext())
                            .load(comentador.getProfileUrl())
                            .into(imgPerfil);

                    ratingBar.setRating(comentario.getClassificacao());
                    Log.d("FirebaseFirestore", "comentarioAp");

                } else {
                    Log.d("FirebaseFirestore", "No such document");
                }
            } else {
                Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
            }
        }
    });
        return convertView;
    }
}
