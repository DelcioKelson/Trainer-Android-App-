package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class AdapterMarcacao extends RecyclerView.Adapter<AdapterMarcacao.MarcacaoHolder> {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    static OnRequestPaymentListener requestPayment;
    private Context context;
    private ArrayList<Map<String, Object>> marcacaoArrayList;
    private boolean isUser;
    private Fragment fragment;
    private PaymentsClient paymentsClient;

    AdapterMarcacao(Context context, ArrayList<Map<String, Object>> marcacaoArrayList, boolean isUser, Fragment fragment, PaymentsClient paymentsClient) {
        this.context = context;
        this.marcacaoArrayList = marcacaoArrayList;
        this.isUser = isUser;
        this.fragment = fragment;
        this.paymentsClient = paymentsClient;
    }

    public void setOnRequestPaymentListener(OnRequestPaymentListener requestPayment) {
        AdapterMarcacao.requestPayment = requestPayment;
    }

    @NonNull
    @Override
    public MarcacaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_marcacao, parent, false);
        return new MarcacaoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarcacaoHolder holder, int position) {
        Map<String, Object> marcacao = marcacaoArrayList.get(position);
        holder.setDetails(marcacao);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {

        return marcacaoArrayList.size();
    }

    public interface OnRequestPaymentListener {
        void request(String price);
    }

    public class MarcacaoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MarcacaoHolder(@NonNull View itemView) {
            super(itemView);

        }


        private void possiblyShowGooglePayButton(View mGooglePayButton) {
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
            task.addOnSuccessListener(fragment.requireActivity(),
                    task1 -> {
                        if (task1) {
                            mGooglePayButton.setVisibility(View.VISIBLE);
                        }

                    });
        }

        public void setDetails(Map<String, Object> marcacao) {

            final CardView marcacaoCard = itemView.findViewById(R.id.marcacao_card);

            final TextView tvDiaTreino = itemView.findViewById(R.id.tv_dia_detalhe);
            final TextView tvHoraTreino = itemView.findViewById(R.id.tv_hora_detalhe);
            final TextView tvPreco = itemView.findViewById(R.id.tv_preco_detalhe);
            final TextView tvTempoDemora = itemView.findViewById(R.id.tv_tempo_detalhe);
            final TextView tvPrecoPromocao = itemView.findViewById(R.id.tv_preco_promocao);

            final Button btnEsquero = itemView.findViewById(R.id.btn_esquerdo);
            final Button btnDireito = itemView.findViewById(R.id.btn_direito);
            final View mGooglePayButton = itemView.findViewById(R.id.googlepay_button);

            String marcacaoEstado = (String) marcacao.get("estado");
            tvPreco.setText(marcacao.get("preco") + "€");
            tvPreco.setPaintFlags(tvPreco.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvPrecoPromocao.setText("0.0€");

            tvHoraTreino.setText("Hora: " + marcacao.get("horaTreino"));
            tvDiaTreino.setText("Dia: " + marcacao.get("diaTreino"));
            tvTempoDemora.setText("Duração: " + marcacao.get("tempoDuracao"));
            final String marcacaoId = (String) marcacao.get("marcacaoId");
            final String uidPersonal = (String) marcacao.get("uidPersonal");

            marcacaoCard.setOnClickListener(this);

            if (isUser) {
                if (marcacaoEstado.equals("pendente")) {
                    btnEsquero.setVisibility(View.GONE);

                    btnDireito.setVisibility(View.VISIBLE);
                    //btnDireito.setText("Cancelar");
                    btnDireito.setCompoundDrawablesWithIntrinsicBounds(fragment.getResources().getDrawable(R.drawable.ic_close_white), null, null, null);
                    btnDireito.setOnClickListener(v -> {
                        FragmentTransaction ft1;
                        ft1 = fragment.getChildFragmentManager().beginTransaction();
                        Fragment prev1 = fragment.getChildFragmentManager().findFragmentByTag("dialog1");
                        if (prev1 != null) {
                            ft1.remove(prev1);
                        }
                        ft1.addToBackStack(null);
                        DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("cancelada", marcacaoId, (String) marcacao.get("uidUsuario"), uidPersonal);
                        //dialog.setTargetFragment(this, 300);
                        dialog.show(ft1, "dialog1");
                    });
                }
                if (marcacaoEstado.equals("aceite")) {

                    possiblyShowGooglePayButton(mGooglePayButton);

                    //processarPagamento();
                    mGooglePayButton.setOnClickListener(v -> {
                        requestPayment.request((String) marcacao.get("preco"));
                        Main.sharedDataModel.setPersonalIdMarcacao(uidPersonal, marcacaoId);
                    });
                }
            } else {

                if (marcacaoEstado.equals("pendente")) {
                    btnEsquero.setVisibility(View.VISIBLE);
                    //btnEsquero.setText("aceitar");
                    btnEsquero.setCompoundDrawablesWithIntrinsicBounds(fragment.getResources().getDrawable(R.drawable.ic_check_white), null, null, null);

                    btnEsquero.setOnClickListener(v -> {
                        // marcacoesRef.document(marcacao.getId()).update("estado","aceite");

                        FragmentTransaction ft2;

                        ft2 = fragment.getChildFragmentManager().beginTransaction();
                        Fragment prev2 = fragment.getChildFragmentManager().findFragmentByTag("dialog2");
                        if (prev2 != null) {
                            ft2.remove(prev2);
                        }
                        ft2.addToBackStack(null);
                        DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("aceite", marcacaoId, (String) marcacao.get("uidUsuario"), (String) marcacao.get("uidPersonal"));
                        dialog.show(ft2, "dialog2");
                    });
                    btnDireito.setVisibility(View.VISIBLE);

                    //btnDireito.setText("Recusar");
                    btnDireito.setCompoundDrawablesWithIntrinsicBounds(fragment.getResources().getDrawable(R.drawable.ic_close_white), null, null, null);

                    btnDireito.setOnClickListener(v -> {
                        FragmentTransaction ft3;

                        ft3 = fragment.getChildFragmentManager().beginTransaction();
                        Fragment prev3 = fragment.getChildFragmentManager().findFragmentByTag("dialog3");
                        if (prev3 != null) {
                            ft3.remove(prev3);
                        }
                        ft3.addToBackStack(null);
                        DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("recusada", marcacaoId, (String) marcacao.get("uidUsuario"), uidPersonal);
                        dialog.show(ft3, "dialog3");
                    });
                }
            }

        }

        @Override
        public void onClick(View v) {
            Main.currentPosition = getAdapterPosition();

            FragmentManager fm = fragment.requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            DialogFragment newFragment;
            newFragment = new FragmentViewPagerMarcacoes();
            newFragment.show(ft, "dialog");
        }
    }
}



