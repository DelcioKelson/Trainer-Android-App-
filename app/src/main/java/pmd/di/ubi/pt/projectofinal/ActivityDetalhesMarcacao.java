package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Optional;


public class ActivityDetalhesMarcacao extends AppCompatActivity {

    private ExtendedFloatingActionButton btnEsquero, btnDireito;
    private String estadoMarcacao,idMarcacao,tipoConta,diaTreino,horaTreino,tempoDuracao, uuidPersonal;
    private Float preco;
    private FirebaseUser user;

    private TextView tvDiaTreino,tvHoraTreino, tvPreco,tvEstado,tvTempoDemora,tvObs;
    private Marcacao marcacao;
    private final CollectionReference pessoasRef = FirebaseFirestore.getInstance().collection("pessoas");

    private PaymentsClient paymentsClient;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private View mGooglePayButton;
    private Button btnCancelarMarcacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_marcacao);
        getSupportActionBar().setTitle("Detalhes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent vindoGerarActivity = getIntent();

        tvDiaTreino = findViewById(R.id.tv_dia_detalhe);
        tvHoraTreino = findViewById(R.id.tv_hora_detalhe);
        tvEstado = findViewById(R.id.tv_estado_detalhe);
        tvPreco = findViewById(R.id.tv_preco_detalhe);
        tvTempoDemora = findViewById(R.id.tv_tempo_detalhe);
        btnEsquero = findViewById(R.id.btn_esquerdo);
        btnDireito = findViewById(R.id.btn_direito);
        tvObs = findViewById(R.id.tv_obs);
        mGooglePayButton = findViewById(R.id.googlepay_button);
        btnCancelarMarcacao = findViewById(R.id.btn_cancelar_marcacao);
        preco = vindoGerarActivity.getFloatExtra("preco",0.0f);
        estadoMarcacao = vindoGerarActivity.getStringExtra("estado");
        idMarcacao = vindoGerarActivity.getStringExtra("idMarcacao");
        tipoConta = vindoGerarActivity.getStringExtra("tipoConta");
        diaTreino = vindoGerarActivity.getStringExtra("diaTreino");
        horaTreino = vindoGerarActivity.getStringExtra("tempoInicio");
        tempoDuracao = vindoGerarActivity.getStringExtra("TempoDemora");
        uuidPersonal = vindoGerarActivity.getStringExtra("uuidPersonal");

        if (idMarcacao!=null){
            pessoasRef.document(user.getUid()).collection("marcacoes").document(idMarcacao)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot!=null){
                                marcacao = documentSnapshot.toObject(Marcacao.class);
                                tvPreco.setText("preço a pagar: " + marcacao.getPreco());
                                tvHoraTreino.setText("hora de inicio: " + marcacao.getHoraTreino());
                                tvDiaTreino.setText("dia do treino: " + marcacao.getDiaTreino());
                                tvTempoDemora.setText("tempo do treino:" + marcacao.getTempoDemora());
                                tvEstado.setText("estado da marcaçao :" + marcacao.getEstado());

                                if (tipoConta.equals("usuario")) {
                                    if (marcacao.getEstado().equals("pedente")) {
                                        btnEsquero.setText("voltar");
                                        btnEsquero.setOnClickListener(v -> {
                                            //startActivity(new Intent(ActivityDetalhesMarcacao.this, ActivityModalidades.class));
                                            finish();
                                        });
                                    }
                                    if(marcacao.getEstado().equals("aceite")) {
                                        btnEsquero.setVisibility(View.GONE);
                                        btnDireito.setVisibility(View.GONE);
                                        paymentsClient = PaymentsUtil.createPaymentsClient(ActivityDetalhesMarcacao.this);
                                        possiblyShowGooglePayButton();

                                        mGooglePayButton.setOnClickListener(v -> {
                                            //processarPagamento();
                                            requestPayment(v);
                                        });

                                        btnCancelarMarcacao.setOnClickListener(v -> {
                                            AlertDialog alertDialog =
                                                    new AlertDialog.Builder(ActivityDetalhesMarcacao.this)
                                                            .setTitle("Cancelar marcacao?")
                                                            .setMessage(
                                                                    "Deseja realmente cancelar a marcaçao?")
                                                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    mudarEstadoMarcacao("cancelada");
                                                                    startActivity(new Intent(ActivityDetalhesMarcacao.this,ActivityModalidades.class));
                                                                    Toast.makeText(ActivityDetalhesMarcacao.this,"Marcacao cancelada com sucesso",Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                }
                                                            })
                                                            .setNegativeButton("cancelar",null)
                                                            .create();
                                            alertDialog.show();
                                        });
                                    }
                                }

                                if(tipoConta.equals("personal")) {
                                    if (marcacao!=null) {
                                       /* if (pessoa1!=null && pessoa1.getDoencas() != null) {
                                            tvObs.setText(pessoa1.getDoencas());
                                        } else {
                                            tvObs.setText("nenhum problema de saude");
                                        }*/
                                        if (marcacao.getEstado().equals("pedente")) {
                                            btnEsquero.setText("aceitar");
                                            btnEsquero.setOnClickListener(v -> mudarEstadoMarcacao("aceite"));

                                            btnDireito.setText("Recusar");
                                            btnDireito.setOnClickListener(v -> criarDialogRecusar());
                                        }

                                    }
                                }
                            }
                        }
                    });
                }
        else {
            tvPreco.setText("preço a pagar: " + preco);
            tvHoraTreino.setText("hora de inicio: " + horaTreino);
            tvDiaTreino.setText("dia do treino: " + diaTreino);
            tvTempoDemora.setText("tempo do treino:" + tempoDuracao);
            tvEstado.setText("estado da marcaçao :" + estadoMarcacao);
            btnEsquero.setText("confirmar");
            btnEsquero.setOnClickListener(v -> salvarMarcaccao());
            btnDireito.setText("cancelar");
            btnDireito.setOnClickListener(v -> finish());
        }
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
        task.addOnCompleteListener(this,
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
            String price = PaymentsUtil.microsToString(marcacao.getPreco());

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
                    paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        new AlertDialog.Builder(this)
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void mudarEstadoMarcacao(String estado){
        marcacao.setEstado(estado);
        pessoasRef.document(marcacao.getPersonalUuid()).collection("marcacoes").document(marcacao.getMarcacaoID()).set(marcacao);
        pessoasRef.document(marcacao.getUsuarioUuid()).collection("marcacoes").document(marcacao.getMarcacaoID()).set(marcacao);
    }

    public void salvarMarcaccao(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minutos = c.get(Calendar.MINUTE);
        DocumentReference marcacaoRefUser,marcacaoRefPersonal;
        marcacaoRefUser = pessoasRef.document(user.getUid()).collection("marcacoes").document();
        Marcacao marcacao = new Marcacao("" + day + "/" + month + "/" + year, diaTreino, "pedente", horaTreino, uuidPersonal, preco, tempoDuracao, user.getUid(), "" + hora + ":" + minutos, marcacaoRefUser.getId());

        marcacaoRefUser.set(marcacao);
        marcacaoRefPersonal = pessoasRef.document(uuidPersonal).collection("marcacoes").document(marcacao.getMarcacaoID());
        marcacaoRefPersonal.set(marcacao);

        Toast.makeText(ActivityDetalhesMarcacao.this,"Marcaçao realizada com sucesso",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ActivityDetalhesMarcacao.this, ActivityPersonalTrainerPerfil.class);
        intent.putExtra("uuid",uuidPersonal);
        finish();
        startActivity(intent);
    }
    public void criarDialogRecusar(){
        AlertDialog alertDialog =
                new AlertDialog.Builder(ActivityDetalhesMarcacao.this)
                        .setTitle("Recusar marcacao?")
                        .setMessage(
                                "Deseja realmente Recusar a marcaçao?")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mudarEstadoMarcacao("recusada");
                                startActivity(new Intent(ActivityDetalhesMarcacao.this,ActivityModalidades.class));
                                Toast.makeText(ActivityDetalhesMarcacao.this,"Marcacao Recusada com sucesso",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .setNegativeButton("cancelar",null)
                        .create();
        alertDialog.show();
    }
}


