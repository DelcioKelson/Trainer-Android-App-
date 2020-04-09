package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class AdapterTreinadoresSimples extends BaseAdapter {
    private Context context;
    private ArrayList<Map<String, String>> personalTrainerList;

     AdapterTreinadoresSimples(Context context, ArrayList<Map<String, String>> personalTrainerList){
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

        final Map<String, String> personalTrainer = personalTrainerList.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_personal_simples, null);
        }

        final TextView tvNome = convertView.findViewById(R.id.nomePersonal_simples);
        final ImageView imgPersonalTrainer = convertView.findViewById(R.id.personal_image_simples);
        final LinearLayout main = convertView.findViewById(R.id.card_personal_simples);
        final RatingBar ratingBar =  convertView.findViewById(R.id.rating_simples);
        final TextView tvPreco = convertView.findViewById(R.id.tv_preco_card_simples);

        String uid =  personalTrainer.get("uid");
        String nomePersonal =personalTrainer.get("nome");
        tvNome.setText("Nome: " + nomePersonal);
        String preco = personalTrainer.get("preco");
        tvPreco.setText("preço: " + preco+ "€");


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

        Log.i("Adaptertreinadores","ignored.");



        main.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("uidPersonal", uid);
            Navigation.findNavController(v).navigate(R.id.action_fragmentTodasMarcacoes_to_persoanlPerfilFragment,bundle);
        });
        return convertView;
    }

}
