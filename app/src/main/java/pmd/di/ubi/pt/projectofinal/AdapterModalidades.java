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

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

public class AdapterModalidades extends BaseAdapter {
    private FragmentActivity context;
    private ArrayList<Map<String,Object>> modalidadeList;

    public AdapterModalidades(FragmentActivity context, ArrayList<Map<String,Object>>  modalidadeList){
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
        final Map<String,Object>  modalidade = modalidadeList.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_modalidade, null);
        }
        final TextView txtTitulo = convertView.findViewById(R.id.titulo);
        final ImageView imgModalidade = convertView.findViewById(R.id.modalidade_imagem);
        final LinearLayout main = (LinearLayout) convertView.findViewById(R.id.linear);

        txtTitulo.setText((CharSequence) modalidade.get("nome"));

        //imgRestaurant.setImageResource(modalidade.getImg());
        Glide.with(imgModalidade.getContext())
                .load(modalidade.get("img"))
                .into(imgModalidade);

        main.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("modalidade", String.valueOf(modalidade.get("nome")));
            Navigation.findNavController(v).navigate(R.id.action_modalidadesFragment_to_personalsFragment,bundle);
        });
        return convertView;
    }
}
