package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class FragmentMudarPassword extends Fragment {
    private FirebaseUser user;
    private EditText etPasswordAtual, etNovaPassword, etConfirmarPassword;

    public FragmentMudarPassword() {
        // Required empty public constructor
    }

    public static FragmentMudarPassword newInstance() {
        return new FragmentMudarPassword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mudar_password, container, false);

        Button btnConfirmar, btnCancelar;
        user = FirebaseAuth.getInstance().getCurrentUser();
        etPasswordAtual = view.findViewById(R.id.edt_pass_atual);
        etNovaPassword = view.findViewById(R.id.edt_pass_nova);
        etConfirmarPassword = view.findViewById(R.id.edt_confirm_pass_nova);
        btnCancelar = view.findViewById(R.id.btn_mudarpass_cancelar);
        btnConfirmar = view.findViewById(R.id.btn_mudarpass_continuar);

        btnConfirmar.setOnClickListener(v -> {

            if (isPasswordValid(etNovaPassword.getText())) {

                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), etPasswordAtual.getText().toString());

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (etConfirmarPassword.getText().toString().equals(etNovaPassword.getText().toString())) {
                            user.updatePassword(etNovaPassword.getText().toString()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getActivity(), "palavra passe alterarada com sucesso", Toast.LENGTH_LONG).show();
                                    Navigation.findNavController(getView()).popBackStack();

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "palavra passes nao coincidem", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(getActivity(), "palavra actual errada", Toast.LENGTH_LONG).show());
            } else {
                etNovaPassword.setError("palavra passe deve ter 6 digitos ou mais");
                return;
            }
        });

        btnCancelar.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text.length() >= 6;
    }

}
