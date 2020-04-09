package pmd.di.ubi.pt.projectofinal;

        import android.content.Context;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CompoundButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RatingBar;
        import android.widget.TextView;
        import android.widget.ToggleButton;

        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentActivity;
        import androidx.navigation.NavDirections;
        import androidx.navigation.Navigation;
        import androidx.navigation.fragment.FragmentNavigator;

        import com.bumptech.glide.Glide;
        import com.bumptech.glide.request.RequestOptions;
        import com.google.android.material.snackbar.Snackbar;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

public class AdapterTreinadores extends BaseAdapter {
    private Context context;
    private ArrayList<Map<String, Object>> personalTrainerList;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Map<String,Boolean> favorite;

     AdapterTreinadores(Context context, ArrayList<Map<String, Object>> personalTrainerList){
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

        final Map<String, Object> personalTrainer = personalTrainerList.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_personaltrainer, null);
        }

        final TextView tvNome = convertView.findViewById(R.id.nomePersonal);
        final ImageView imgPersonalTrainer = convertView.findViewById(R.id.personal_image);
        final LinearLayout main = convertView.findViewById(R.id.card_personal);
        final RatingBar ratingBar =  convertView.findViewById(R.id.rating);
        final TextView txtInfo = convertView.findViewById(R.id.infoclassificacao);
        final TextView tvPreco = convertView.findViewById(R.id.tv_preco_card);
        final ToggleButton heartToggle = convertView.findViewById(R.id.button_favorite);

        try {
            favorite = (Map<String, Boolean>) personalTrainer.get("favorito");
                heartToggle.setChecked(favorite.get(user.getUid()).booleanValue());
        }catch (Exception e){ }

        String uid = (String) personalTrainer.get("uid");
        final String nomePersonal = (String) personalTrainer.get("nome");
        tvNome.setText("Nome: " + nomePersonal);
        String preco = (String) personalTrainer.get("preco");
        tvPreco.setText("preço: " + preco+ "€");

        View finalConvertView = convertView;

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
            }
        });

        Log.i("Adaptertreinador","ignored.");

        main.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("uidPersonal", uid);
            Navigation.findNavController(v).navigate(R.id.action_personalsFragment_to_persoanlPerfilFragment,bundle);
        });
        return convertView;
    }

}
