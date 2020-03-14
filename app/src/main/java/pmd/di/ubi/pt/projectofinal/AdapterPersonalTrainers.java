package pmd.di.ubi.pt.projectofinal;

        import android.app.Activity;
        import android.app.ActivityOptions;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RatingBar;
        import android.widget.TextView;

        import androidx.annotation.NonNull;

        import com.bumptech.glide.Glide;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.Objects;

public class AdapterPersonalTrainers extends BaseAdapter {
    private Context context;
    private ArrayList<PersonalTrainer> personalTrainerList;

     AdapterPersonalTrainers(Context context, ArrayList<PersonalTrainer> personalTrainerList){
        this.context = context;
        this.personalTrainerList = personalTrainerList;
    }

    @Override
    public int getCount() {
        return personalTrainerList.size();
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

        final PersonalTrainer personalTrainer = personalTrainerList.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_personaltrainer, null);
        }

        final TextView txtTitulo = convertView.findViewById(R.id.nomePersonal);
        final ImageView imgPersonalTrainer = convertView.findViewById(R.id.personal_image);
        final LinearLayout main = convertView.findViewById(R.id.card_personal);
        final RatingBar ratingBar =  convertView.findViewById(R.id.rating);
        final TextView txtInfo = convertView.findViewById(R.id.infoclassificacao);

        txtTitulo.setText(personalTrainer.getNome());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+ personalTrainer.getUuid() + ".jpeg");

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            if(bytes.length!=0){

               try {
                   Glide.with(context.getApplicationContext() )
                           .load(bytes)
                           .into(imgPersonalTrainer);
               }catch (Exception ignored){

               }
            }
        });

        if (personalTrainer.getClassificacao()>0.0f){
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(personalTrainer.getClassificacao());
        //tvNumeroComentario.setVisibility(View.VISIBLE);
        }else {
            txtInfo.setVisibility(View.VISIBLE);
        }

        main.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityPersonalTrainerPerfil.class);
            intent.putExtra("uuid",personalTrainer.getUuid());
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
        });
        return convertView;
    }

}
