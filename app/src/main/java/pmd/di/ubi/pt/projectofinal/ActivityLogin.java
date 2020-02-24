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

public class ActivityLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String email,password;
    private Button btnLogin;
    private TextView tvRegistrar;
    private EditText etEmail, etPassword;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_pass);
        btnLogin  = findViewById(R.id.btn_login);
        tvRegistrar = findViewById(R.id.tv_registrar);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Login");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("loginEmail", "signInWithEmail:success");
                                    user =  mAuth.getCurrentUser();
                                    Intent intent = new Intent(ActivityLogin.this, ActivityInical.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d("loginEmail", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(ActivityLogin.this, "E-mail ou palavra-passe incorretos. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        tvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityRegistro.class));
            }
        });
    }

}
