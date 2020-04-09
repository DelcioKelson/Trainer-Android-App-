package pmd.di.ubi.pt.projectofinal;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityMain extends AppCompatActivity {
    FirebaseUser user;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        navController = Navigation.findNavController(this, R.id.container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Log.i("userValue",""+user);
        if (user != null) {
             FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("activityInicial", "activityInicial");
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            toolbar.setVisibility(View.VISIBLE);
                            bottomNavigationView.setVisibility(View.VISIBLE);

                            SharedDataModel modelData = new ViewModelProvider(this).get(SharedDataModel.class);
                            modelData.init();
                            FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
                            if(document.getString("tipoConta").equals("personal")){
                                modelData.personal();
                                iniciarSessaoPersonal(document.getString("nome"));
                            }else {
                                modelData.usuario();
                                iniciarSessaoUsuario();
                            }
                            setSupportActionBar(toolbar);
                            mAppBarConfiguration =
                                    new AppBarConfiguration.Builder(navController.getGraph()).build();

                            NavigationUI.setupWithNavController(toolbar,navController);
                            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                            NavigationUI.setupWithNavController(bottomNavigationView, navController);
                        }
                    }else {
                        navController.setGraph(R.navigation.nav_graph_login);

                    }
                });

        } else {
            {
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
        return NavigationUI.onNavDestinationSelected(item,navController)||super.onOptionsItemSelected(item);
    }

    public void iniciarSessaoUsuario() {
        navController.setGraph(R.navigation.nav_graph_usuario);
        bottomNavigationView.inflateMenu(R.menu.bottom_usuario_menu);
    }
    public void iniciarSessaoPersonal(String nome) {
        OnCompleteListener<AuthResult> completeListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult()!= null) {
                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                        builder.setDisplayName(nome);
                        final UserProfileChangeRequest changeRequest = builder.build();
                        user.updateProfile(changeRequest);
                    }
                }
            }
        };

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("loadPayment", "onActivityresult");

        switch (requestCode) {

            // value passed in AutoResolveHelper
            case 991:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        Log.w("loadPayment", "sucess");

                        break;
                    case Activity.RESULT_CANCELED:
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
                break;
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
        Log.w("loadPaymentDatafailed", String.format("Error code: %d", statusCode));
    }
}


