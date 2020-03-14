package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ActivityMudarPassword extends AppCompatActivity {

    private FirebaseUser user;
    private EditText etPasswordAtual,etNovaPassword, etConfirmarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudar_password);

        Button btnConfirmar,btnCancelar;
        user = FirebaseAuth.getInstance().getCurrentUser();
        etPasswordAtual = findViewById(R.id.edt_pass_atual);
        etNovaPassword = findViewById(R.id.edt_pass_nova);
        etConfirmarPassword = findViewById(R.id.edt_confirm_pass_nova);
        btnCancelar = findViewById(R.id.btn_mudarpass_cancelar);
        btnConfirmar = findViewById(R.id.btn_mudarpass_continuar);

        btnConfirmar.setOnClickListener(v -> {
            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()),etPasswordAtual.getText().toString());
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(etConfirmarPassword.getText().toString().equals(etNovaPassword.getText().toString())){
                    user.updatePassword(etNovaPassword.getText().toString()).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(ActivityMudarPassword.this,"palavra passe alterarada com sucesso",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityMudarPassword.this, ActivityDefinicoesConta.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });}else {
                        Toast.makeText(ActivityMudarPassword.this,"palavra passes nao coincidem",Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(ActivityMudarPassword.this,"palavra actual errada",Toast.LENGTH_LONG).show());
        });

       btnCancelar.setOnClickListener(v -> {
           Intent intent = new Intent(ActivityMudarPassword.this, ActivityDefinicoesConta.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent);
       });
    }
}
