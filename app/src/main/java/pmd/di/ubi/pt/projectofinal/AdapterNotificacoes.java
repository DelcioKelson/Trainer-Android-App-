package pmd.di.ubi.pt.projectofinal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import io.grpc.Context;

public class AdapterNotificacoes extends RecyclerView.Adapter<AdapterNotificacoes.NotificacaoHolder> {

    private Fragment fragment;
    private ArrayList<Map<String,Object>> notificacoesList;
    private AdapterNotificacoes adapterThis = this;
    private String idUser;

    static OnRequestPaymentListener requestPayment;

    AdapterNotificacoes(Fragment fragment, ArrayList<Map<String, Object>> notificacoesList, String idUser) {
        this.fragment = fragment;
        this.notificacoesList = notificacoesList;
        this.idUser = idUser;
    }


    void setOnRequestPaymentListener(OnRequestPaymentListener requestPayment){
        AdapterNotificacoes.requestPayment = requestPayment;
    }

    public interface OnRequestPaymentListener{
        void request(String price);
    }


    @NonNull
    @Override
    public NotificacaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.card_notificacao,parent,false);
        return new NotificacaoHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull NotificacaoHolder holder, int position) {
        Map<String, Object> notificacao = notificacoesList.get(position);
        holder.setDetails(notificacao);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return notificacoesList.size();    }



    public class NotificacaoHolder extends RecyclerView.ViewHolder{
        public NotificacaoHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void setDetails(Map<String, Object> notificacao) {
            final TextView tvtTitulo = itemView.findViewById(R.id.tv_titulo_notificacao);
            final TextView tvMensagem = itemView.findViewById(R.id.tv_mensagem_notificacao);
            final TextView tvData = itemView.findViewById(R.id.tv_data_notificacao);
            final LinearLayout linearLayout = itemView.findViewById(R.id.notification_linearLayout);
            final String ID =(String) notificacao.get("id");
            final String marcacaoId = (String) notificacao.get("marcacaoId");

            final DocumentReference notificacoesRef = FirebaseFirestore.getInstance().collection("pessoas").document(idUser)
                    .collection("notificacoes").document(ID);


            boolean vista = (boolean) notificacao.get("vista");
            if(!vista){
                linearLayout.setBackgroundColor(fragment.getResources().getColor(R.color.colorAccent));
                notificacoesRef.update("vista",true);
            }

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


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                    Fragment prev =fragment.getFragmentManager().findFragmentByTag("dialog");
                    DialogFragment newFragment;
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    Bundle b = new Bundle();
                    b.putString("marcacaoId",marcacaoId);
                    newFragment = FragmentDetalhesMarcacao.newInstance(b);
                    newFragment.show(ft, "dialog");

                }
            });

            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

                    builder.setMessage("Deseja eliminar a notificacao?");

                    builder.setPositiveButton("sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            notificacoesList.remove(notificacao);
                            adapterThis.notifyDataSetChanged();

                            notificacoesRef.delete();

                        }
                    });
                    builder.setNegativeButton("nao", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.create().show();

                    return false;
                }
            });

        }

    }
}
