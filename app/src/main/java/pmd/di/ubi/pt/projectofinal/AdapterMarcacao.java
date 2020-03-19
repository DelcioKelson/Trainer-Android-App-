package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Map;

public class AdapterMarcacao extends BaseAdapter {

    private Context context;
    private ArrayList<Map<String, Object>> marcacaoArrayList;
    private String tipoConta;

    AdapterMarcacao(Context context, ArrayList<Map<String, Object>> marcacaoArrayList, String tipoConta) {
        this.context = context;
        this.marcacaoArrayList = marcacaoArrayList;
        this.tipoConta = tipoConta;
    }

    @Override
    public int getCount() {
        return marcacaoArrayList.size();
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
        final Map<String, Object> marcacao = marcacaoArrayList.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_marcacao, null);
        }
        Log.d("FirebaseFirestore", "comentarioAp");

        final CardView marcacaoCard = convertView.findViewById(R.id.marcacao_card);
        final TextView tvDiaTreino = convertView.findViewById(R.id.tv_dia_marcado);
        final TextView tvHoraTreino = convertView.findViewById(R.id.tv_hora_marcada);
        final TextView tvEstado = convertView.findViewById(R.id.tv_estado_marcacao);


        tvDiaTreino.setText("treino para o dia: " + marcacao.get("diaTreino"));
        tvEstado.setText("estado:" + marcacao.get("estado"));
        tvHoraTreino.setText("hora do treino: " + marcacao.get("horaTreino"));

        marcacaoCard.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("idMarcacao",(String) marcacao.get("marcacaoId"));
            Navigation.findNavController(v).navigate(R.id.action_marcacoesFragment_to_detalhesMarcacaoFragment,bundle);


        });
        return convertView;
    }
}
