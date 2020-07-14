package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {

    private FirebaseAuth mAuth;
    private String email, password;
    private TextInputEditText etEmail, etPassword;
    private ProgressBar progressBar;


    public static FragmentLogin newInstance() {
        FragmentLogin fragment = new FragmentLogin();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_pass);
        Button btnLogin = view.findViewById(R.id.btn_login);
        TextView tvRegistrar = view.findViewById(R.id.tv_registrar);
        mAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progressbar_login);


        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(etPassword.getText())) {
                    etPassword.setError(null); //Clear the error
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(v -> {

            if (isEmailValid(etEmail.getText())) {

                if (isPasswordValid(etPassword.getText())) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setClickable(false);
                    etPassword.setError(null); // Clear the error

                    email = etEmail.getText().toString();
                    password = etPassword.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getContext(), Main.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "Email ou palavra-passe incorretos, tente novamente.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setClickable(true);
                                }
                            });
                } else {
                    etPassword.setError("Palavra passe invalida");
                }
            } else {
                etEmail.setError("Email invalido");

            }

        });
        tvRegistrar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.fragmentSelecionarTipoConta));
        return view;
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 6;
    }

    private boolean isEmailValid(Editable text) {
        return text != null && Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches();

    }
}
