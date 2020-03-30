package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {

    private FirebaseAuth mAuth;
    private String email,password;
    private EditText etEmail, etPassword;

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
        Button btnLogin  = view.findViewById(R.id.btn_login);
        TextView tvRegistrar = view.findViewById(R.id.tv_registrar);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("loginEmail", "signInWithEmail:success");
                            Intent intent = new Intent(getContext(), ActivityMain.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("loginEmail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Email ou palavra-passe incorretos, tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    });
        });
        tvRegistrar.setOnClickListener(v ->
        Navigation.findNavController(v).navigate(R.id.action_fragmentLogin_to_fragmentRegistro1));

        return view;
    }
}
