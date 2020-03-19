package pmd.di.ubi.pt.projectofinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDetalhesMarcacao#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDetalhesMarcacao extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "preco";
    private static final String ARG_PARAM2 = "horaTreino";

    private static final String ARG_PARAM3 = "diaTreino";
    private static final String ARG_PARAM4 = "tempoDemora";

    private static final String ARG_PARAM5 = "isUser";
    private static final String ARG_PARAM6 = "estado";
    private static final String ARG_PARAM7 = "idMarcacao";
    private static final String ARG_PARAM8 = "uidPersonal";

    // TODO: Rename and change types of parameters


    private ExtendedFloatingActionButton btnEsquero, btnDireito;
    private String estadoMarcacao,idMarcacao,diaTreino,horaTreino,tempoDuracao, uidPersonal,preco;
    private FirebaseUser user;
    private boolean isUser;

    private String marcacaoEstado;

    private TextView tvDiaTreino,tvHoraTreino, tvPreco,tvEstado,tvTempoDemora,tvObs;
    private DocumentSnapshot marcacao;

    private PaymentsClient paymentsClient;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private View mGooglePayButton;
    private Button btnCancelarMarcacao;

    private CollectionReference marcacoesRef;

    public FragmentDetalhesMarcacao() {
        // Required empty public constructor
    }


    public static FragmentDetalhesMarcacao newInstance(String param1, String param2, String param3, String param4, String param5, String param6, String param7, String param8) {
        FragmentDetalhesMarcacao fragment = new FragmentDetalhesMarcacao();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            estadoMarcacao = getArguments().getString(ARG_PARAM6);
            idMarcacao = getArguments().getString(ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detalhes_marcacao_fragment, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        modelData.isUser().observe(this, s -> isUser = s);

        marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");
        tvDiaTreino = view.findViewById(R.id.tv_dia_detalhe);
        tvHoraTreino = view.findViewById(R.id.tv_hora_detalhe);
        tvEstado = view.findViewById(R.id.tv_estado_detalhe);
        tvPreco = view.findViewById(R.id.tv_preco_detalhe);
        tvTempoDemora = view.findViewById(R.id.tv_tempo_detalhe);
        btnEsquero = view.findViewById(R.id.btn_esquerdo);
        btnDireito = view.findViewById(R.id.btn_direito);
        tvObs = view.findViewById(R.id.tv_obs);
        mGooglePayButton = view.findViewById(R.id.googlepay_button);
        btnCancelarMarcacao = view.findViewById(R.id.btn_cancelar_marcacao);

        if (idMarcacao!=null){
            marcacoesRef.document(idMarcacao)
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot!=null){
                        marcacao = documentSnapshot;
                        marcacaoEstado = marcacao.getString("estado");
                        tvPreco.setText("preço a pagar: " + marcacao.getString("preco"));
                        tvHoraTreino.setText("hora de inicio: " + marcacao.getString("horaTreino"));
                        tvDiaTreino.setText("dia do treino: " + marcacao.getString("diaTreino"));
                        tvTempoDemora.setText("tempo do treino:" + marcacao.getString("tempoDuracao"));
                        tvEstado.setText("estado da marcaçao :" + marcacaoEstado);

                        if (isUser) {
                            if (marcacaoEstado.equals("pendente")) {
                                btnEsquero.setVisibility(View.GONE);

                                btnDireito.setText("Cancelar Marcacao");
                                btnDireito.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        criarDialogCancelar();
                                    }
                                });
                            }
                            if(marcacaoEstado.equals("aceite")) {
                                btnEsquero.setVisibility(View.GONE);
                                btnDireito.setVisibility(View.GONE);
                                paymentsClient = PaymentsUtil.createPaymentsClient(getActivity());
                                possiblyShowGooglePayButton();

                                mGooglePayButton.setOnClickListener(v -> {
                                    //processarPagamento();
                                    requestPayment(v);
                                });

                                btnCancelarMarcacao.setOnClickListener(v -> {
                                    criarDialogCancelar();
                                });
                            }
                        }

                        if(isUser) {
                            if (marcacao!=null) {
                                       /* if (pessoa1!=null && pessoa1.getDoencas() != null) {
                                            tvObs.setText(pessoa1.getDoencas());
                                        } else {
                                            tvObs.setText("nenhum problema de saude");
                                        }*/
                                if (marcacaoEstado.equals("pedente")) {
                                    btnEsquero.setText("aceitar");
                                    btnEsquero.setOnClickListener(v -> marcacoesRef.document(marcacao.getId()).update("estado","aceite"));

                                    btnDireito.setText("Recusar");
                                    btnDireito.setOnClickListener(v -> criarDialogRecusar());
                                }

                            }
                        }
                    }
                }
            });
        }

        return view;
    }

    private void possiblyShowGooglePayButton() {
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
        task.addOnCompleteListener(getActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

    private void setGooglePayAvailable(Boolean result) {
        if(result){
            mGooglePayButton.setVisibility(View.VISIBLE);
            btnCancelarMarcacao.setVisibility(View.VISIBLE);
        }else {
        }
    }

    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        btnEsquero.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = PaymentsUtil.microsToString((Float) marcacao.get("preco"));

        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request), getActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOAD_PAYMENT_DATA_REQUEST_CODE==requestCode){
            if (RESULT_OK==resultCode){
                PaymentData paymentData = PaymentData.getFromIntent(data);
                handlePaymentSuccess(paymentData);
            }
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void criarDialogRecusar(){
        AlertDialog alertDialog =
                new AlertDialog.Builder(getActivity())
                        .setTitle("Recusar marcacao?")
                        .setMessage(
                                "Deseja realmente Recusar a marcaçao?")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                marcacoesRef.document(marcacao.getId()).update("estado","recusada");
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, FragmentModalidades.newInstance())
                                        .commitNow();
                                Toast.makeText(getActivity(),"Marcacao Recusada com sucesso",Toast.LENGTH_LONG).show();
                                //finish();
                            }
                        })
                        .setNegativeButton("cancelar",null)
                        .create();
        alertDialog.show();
    }

    public void criarDialogCancelar(){
        AlertDialog alertDialog =
                new AlertDialog.Builder(getActivity())
                        .setTitle("Cancelar marcacao?")
                        .setMessage(
                                "Deseja realmente cancelar a marcaçao?")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                marcacoesRef.document(marcacao.getId()).update("estado","cancelada");
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, FragmentModalidades.newInstance())
                                        .commitNow();
                                Toast.makeText(getActivity(),"Marcacao cancelada com sucesso",Toast.LENGTH_LONG).show();
                                // finish();
                            }
                        })
                        .setNegativeButton("cancelar",null)
                        .create();
        alertDialog.show();
    }
}