package pmd.di.ubi.pt.projectofinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity {
    FirebaseUser user;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    static BadgeDrawable badge;
    static SharedDataModel sharedDataModel;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";
    static int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            return;
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.first_layout);

        iniciarSessao();
    }

    public void iniciarSessao() {
        Log.i("userValue", "" + user);
        if (user != null) {
            FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Log.i("activityInicial", "activityInicial");
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        setContentView(R.layout.activity_main);
                        toolbar = findViewById(R.id.toolbar);
                        navController = Navigation.findNavController(this, R.id.container);
                        bottomNavigationView = findViewById(R.id.bottom_navigation);
                        toolbar.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        sharedDataModel = new ViewModelProvider(this).get(SharedDataModel.class);
                        sharedDataModel.init();
                        String tipoConta = document.getString("tipoConta");
                        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
                        sharedDataModel.setUsuarioAtual(document.getData());

                        if (tipoConta.equals("personal") || tipoConta.equals("usuario")) {
                            if (tipoConta.equals("personal")) {
                                sharedDataModel.personal();
                                iniciarSessaoPersonal();
                            } else if (tipoConta.equals("usuario")) {
                                sharedDataModel.usuario();
                                iniciarSessaoUsuario();
                            }
                            badge = bottomNavigationView.getOrCreateBadge(R.id.fragmentNotificacoes);
                            badge.setVisible(false);

                        } else if (tipoConta.equals("pendente")) {
                            navController.setGraph(R.navigation.nav_graph_personal_pendente);
                            bottomNavigationView.setVisibility(View.GONE);
                        }

                        setSupportActionBar(toolbar);
                        mAppBarConfiguration =
                                new AppBarConfiguration.Builder(navController.getGraph()).build();

                        NavigationUI.setupWithNavController(toolbar, navController);
                        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                        NavigationUI.setupWithNavController(bottomNavigationView, navController);
                    }
                } else {
                    setContentView(R.layout.activity_main);
                    navController = Navigation.findNavController(this, R.id.container);
                    navController.setGraph(R.navigation.nav_graph_login);
                }
            });
        } else {
            {
                setContentView(R.layout.activity_main);
                navController = Navigation.findNavController(this, R.id.container);
                navController.setGraph(R.navigation.nav_graph_login);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    public void iniciarSessaoUsuario() {
        navController.setGraph(R.navigation.nav_graph_usuario);
        bottomNavigationView.inflateMenu(R.menu.bottom_usuario_menu);
    }

    public void iniciarSessaoPersonal() {
        navController.setGraph(R.navigation.nav_graph_personal);
        bottomNavigationView.inflateMenu(R.menu.bottom_personal_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("loadPayment", "onActivityresult");

        if (LOAD_PAYMENT_DATA_REQUEST_CODE==requestCode && data!=null){
                switch (resultCode) {
                        case android.app.Activity.RESULT_OK:
                            PaymentData paymentData = PaymentData.getFromIntent(data);
                            handlePaymentSuccess(paymentData);
                            Log.w("loadPayment", "sucess");

                            break;
                        case android.app.Activity.RESULT_CANCELED:
                            // Nothing to here normally - the user simply cancelled without selecting a
                            // payment method.
                            Log.w("loadPayment", "cancela");

                            break;
                        case AutoResolveHelper.RESULT_ERROR:
                            Status status = AutoResolveHelper.getStatusFromIntent(data);
                            handleError(status.getStatusCode());
                            break;
                        default:
                            // Do nothing.
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

                // marcacoesRef.document(marcacao.getId()).update("estado","aceite");

                String marcacaoId = sharedDataModel.getPersonalIdMarcacao().getValue().get("idmarcacao");
                String idPersonal = sharedDataModel.getPersonalIdMarcacao().getValue().get("idpersonal");

                CollectionReference pessoasRef = FirebaseFirestore.getInstance().collection("pessoas");

                DocumentReference marcacaoReference = FirebaseFirestore.getInstance().collection("marcacoes").document(marcacaoId);

                marcacaoReference.update("estado", "paga");
                //Toast.makeText(this, "Marcação paga com sucesso", Toast.LENGTH_LONG).show();

                Map<String, Object> notificacaoData;
                notificacaoData = new HashMap<>();
                notificacaoData.put("data", System.currentTimeMillis());
                notificacaoData.put("vista", false);
                notificacaoData.put("mensagem", user.getDisplayName() + " pagou uma marcação consigo");
                notificacaoData.put("titulo", "marcação paga");
                notificacaoData.put("marcacaoId", marcacaoId);

                DocumentReference notificacaoRef = pessoasRef
                        .document(idPersonal).collection("notificacoes").document();

                sharedDataModel.setFecharViewPager(true);
                sharedDataModel.setAtualizar(true);

                notificacaoData.put("id", notificacaoRef.getId());
                notificacaoRef.set(notificacaoData);
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Marcação paga com sucesso")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();

                sharedDataModel.setFecharViewPager(false);
                sharedDataModel.setAtualizar(false);
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
        Log.w("loadPaymentDatafailed", String.format("Error code: %d", statusCode));
    }
}