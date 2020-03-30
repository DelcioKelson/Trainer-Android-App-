package pmd.di.ubi.pt.projectofinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static android.app.Activity.RESULT_OK;

public class AdapterMarcacao extends BaseAdapter{

    private Context context;
    private ArrayList<Map<String, String>> marcacaoArrayList;
    private boolean isUser;
    private Fragment B;
    private Map<String, String> marcacao;
    private OnRequestPaymentListener requestPayment;

    private PaymentsClient paymentsClient;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    AdapterMarcacao(Context context, ArrayList<Map<String, String>> marcacaoArrayList, boolean isUser, Fragment B, PaymentsClient paymentsClient) {
        this.context = context;
        this.marcacaoArrayList = marcacaoArrayList;
        this.isUser = isUser;
        this.B = B;
        this.paymentsClient = paymentsClient;
    }

    public void setOnRequestPaymentListener(OnRequestPaymentListener requestPayment ){
        this.requestPayment = requestPayment;
    }

    public interface OnRequestPaymentListener{
        public void request(String price);
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
        marcacao = marcacaoArrayList.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.card_marcacao, null);
        }
        Log.d("FirebaseFirestore", "comentarioAp");

        final CardView marcacaoCard = convertView.findViewById(R.id.marcacao_card);

        final TextView tvDiaTreino = convertView.findViewById(R.id.tv_dia_detalhe);
        final TextView tvHoraTreino = convertView.findViewById(R.id.tv_hora_detalhe);
        final TextView tvEstado = convertView.findViewById(R.id.tv_estado_detalhe);
        final TextView tvPreco = convertView.findViewById(R.id.tv_preco_detalhe);
        final TextView tvTempoDemora = convertView.findViewById(R.id.tv_tempo_detalhe);

        final Button btnEsquero = convertView.findViewById(R.id.btn_esquerdo);
        final Button btnDireito = convertView.findViewById(R.id.btn_direito);
        final View mGooglePayButton =convertView.findViewById(R.id.googlepay_button);

        String marcacaoEstado =  marcacao.get("estado");
        tvPreco.setText("preço a pagar: " + marcacao.get("preco"));
        tvHoraTreino.setText("hora de inicio: " + marcacao.get("horaTreino"));
        tvDiaTreino.setText("dia do treino: " + marcacao.get("diaTreino"));
        tvTempoDemora.setText("tempo do treino:" + marcacao.get("tempoDuracao"));
        tvEstado.setText("estado da marcaçao :" + marcacaoEstado);
        final  String marcacaoId = marcacao.get("marcacaoId");

        if (isUser) {
            if (marcacaoEstado.equals("pendente")) {
                btnEsquero.setVisibility(View.GONE);

                btnDireito.setVisibility(View.VISIBLE);
                btnDireito.setText("Cancelar Marcacao");
                btnDireito.setOnClickListener(v -> {
                     FragmentTransaction ft1;
                    ft1 = B.getChildFragmentManager().beginTransaction();
                    Fragment prev1 = B.getChildFragmentManager().findFragmentByTag("dialog1");
                    if (prev1 != null) {
                        ft1.remove(prev1);
                    }
                    ft1.addToBackStack(null);
                    DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("cancelada",marcacaoId,marcacao.get("uidUsuario"),marcacao.get("uidPersonal"));
                    //dialog.setTargetFragment(this, 300);
                    dialog.show(ft1,"dialog1");
                });
            }
            if(marcacaoEstado.equals("aceite")) {

                possiblyShowGooglePayButton(mGooglePayButton);

                //processarPagamento();
                mGooglePayButton.setOnClickListener(v -> requestPayment.request(marcacao.get("preco")));
            }
        }
        else  {

            if (marcacaoEstado.equals("pendente")) {
                btnEsquero.setVisibility(View.VISIBLE);
                btnEsquero.setText("aceitar");
                btnEsquero.setOnClickListener(v -> {
                    // marcacoesRef.document(marcacao.getId()).update("estado","aceite");

                    FragmentTransaction ft2;

                    ft2 = B.getChildFragmentManager().beginTransaction();
                    Fragment prev2 = B.getChildFragmentManager().findFragmentByTag("dialog2");
                    if (prev2 != null) {
                        ft2.remove(prev2);
                    }
                    ft2.addToBackStack(null);
                    DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("aceite",marcacaoId,marcacao.get("uidUsuario"),marcacao.get("uidPersonal"));
                    dialog.show(ft2,"dialog2");
                    Toast.makeText(context,"marcacao aceite com sucesso",Toast.LENGTH_LONG);
                });
                btnDireito.setVisibility(View.VISIBLE);

                btnDireito.setText("Recusar");
                btnDireito.setOnClickListener(v -> {
                    FragmentTransaction ft3;

                    ft3 = B.getChildFragmentManager().beginTransaction();
                    Fragment prev3 = B.getChildFragmentManager().findFragmentByTag("dialog3");
                    if (prev3 != null) {
                        ft3.remove(prev3);
                    }
                    ft3.addToBackStack(null);
                    DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("recusada",marcacaoId,marcacao.get("uidUsuario"),marcacao.get("uidPersonal"));
                    dialog.show(ft3,"dialog3");
                    Toast.makeText(context,"marcacao aceite com sucesso",Toast.LENGTH_LONG);
                    //getActivity().getSupportFragmentManager().popBackStack();
                } );
            }
        }
/*
        marcacaoCard.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("idMarcacao", marcacao.get("marcacaoId"));
            Navigation.findNavController(v).navigate(R.id.action_marcacoesFragment_to_detalhesMarcacaoFragment,bundle);

        });*/


        return convertView;
    }

    private void possiblyShowGooglePayButton(View mGooglePayButton ) {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(B.getActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            if(task.getResult()){
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }
}
