package pmd.di.ubi.pt.projectofinal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterModalidades extends BaseAdapter {
    private Context context;
    private ArrayList<Modalidade> modalidadeList;

    public AdapterModalidades(Context context, ArrayList<Modalidade> modalidadeList){
        this.context = context;
        this.modalidadeList = modalidadeList;
    }

    @Override
    public int getCount() {
        return modalidadeList.size();
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
        final Modalidade modalidade = modalidadeList.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_modalidade, null);
        }
        final TextView txtTitulo = convertView.findViewById(R.id.titulo);
        final ImageView imgModalidade = convertView.findViewById(R.id.modalidade_imagem);
        final LinearLayout main = (LinearLayout) convertView.findViewById(R.id.linear);

        txtTitulo.setText(modalidade.getNome());

        //imgRestaurant.setImageResource(modalidade.getImg());
        Glide.with(imgModalidade.getContext())
                .load(modalidade.getImg())
                .into(imgModalidade);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityPersonalList.class);
                intent.putExtra("modalidade",modalidade.getNome());
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
            }
        });
        return convertView;
    }
}
