package pmd.di.ubi.pt.projectofinal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class AdapterNotificacoes extends BaseAdapter {

    private FragmentActivity context;
    private ArrayList<Map<String,Object>> notificacoesList;

    public AdapterNotificacoes(FragmentActivity context, ArrayList<Map<String, Object>> notificacoesList) {
        this.context = context;
        this.notificacoesList = notificacoesList;
    }

    @Override
    public int getCount() {
        return notificacoesList.size();
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
        final Map<String,Object>  notificacao = notificacoesList.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_notificacao, null);
        }
        final TextView tvtTitulo = convertView.findViewById(R.id.tv_titulo_notificacao);
        final TextView tvMensagem = convertView.findViewById(R.id.tv_mensagem_notificacao);
        final TextView tvData = convertView.findViewById(R.id.tv_data_notificacao);

        Log.i("numero", ""+notificacoesList.size());

        tvtTitulo.setText((String) notificacao.get("titulo"));
        tvMensagem.setText((String) notificacao.get("mensagem"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long)notificacao.get("data"));

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minutos = calendar.get(Calendar.MINUTE);

        if(minutos>9){
            tvData.setText(mDay+"/"+mMonth+"/"+mYear +"     Hora:"+hora + ":" +minutos);
        }
        else {
            tvData.setText(mDay+"/"+mMonth+"/"+mYear +"     Hora:"+hora + ":0"+minutos);

        }



        //final LinearLayout main = (LinearLayout) convertView.findViewById(R.id.linear);
        return convertView;
    }
}
