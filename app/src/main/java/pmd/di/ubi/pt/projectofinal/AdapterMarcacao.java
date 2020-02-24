package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Map;

public class AdapterMarcacao extends BaseAdapter {

    private Context context;
    private ArrayList<Marcacao> marcacaoArrayList;
    private String tipoConta;

    public AdapterMarcacao(Context context, ArrayList<Marcacao> marcacaoArrayList, String tipoConta) {
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
        final Marcacao marcacao = marcacaoArrayList.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_marcacao, null);
        }
        Log.d("FirebaseFirestore", "comentarioAp");

        final CardView marcacaoCard = convertView.findViewById(R.id.marcacao_card);
        final TextView tvDiaTreino = convertView.findViewById(R.id.tv_dia_marcado);
        final TextView tvHoraTreino = convertView.findViewById(R.id.tv_hora_marcada);
        final TextView tvEstado = convertView.findViewById(R.id.tv_estado_marcacao);

        tvDiaTreino.setText("treino para o dia: " + marcacao.getDiaTreino());
        tvEstado.setText("estado:" + marcacao.getEstado());
        tvHoraTreino.setText("hora do treino: " + marcacao.getHoraTreino());

        marcacaoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ActivityDetalhesMarcacao.class);
                intent.putExtra("idMarcacao",marcacao.getMarcacaoID());
                intent.putExtra("tipoConta",tipoConta);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
