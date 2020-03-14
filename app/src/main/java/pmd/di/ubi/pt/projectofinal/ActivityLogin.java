package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email,password;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_pass);
        Button btnLogin  = findViewById(R.id.btn_login);
        TextView tvRegistrar = findViewById(R.id.tv_registrar);
        mAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");

        btnLogin.setOnClickListener(v -> {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(ActivityLogin.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("loginEmail", "signInWithEmail:success");
                            Intent intent = new Intent(ActivityLogin.this, ActivityInical.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            } else {
                            // If sign in fails, display a message to the user.
                            Log.d("loginEmail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(ActivityLogin.this, "E-mail ou palavra-passe incorretos. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    });
        });
        tvRegistrar.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ActivityRegistro.class)));
    }

}
