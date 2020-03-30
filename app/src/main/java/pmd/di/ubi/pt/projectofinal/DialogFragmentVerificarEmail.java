package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DialogFragmentVerificarEmail extends DialogFragment {

    FirebaseUser user;

    public DialogFragmentVerificarEmail() {

    }

    public static DialogFragmentVerificarEmail newInstance() {

        return new DialogFragmentVerificarEmail();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialogfragment_verificacao_email, container, true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        final Button btnConfirmar = view.findViewById(R.id.btn_confirmar_email);
        final Button btnReenviar = view.findViewById(R.id.btn_reenviar_email);

        final TextView tvInfo = view.findViewById(R.id.tv_confirmacao_info);
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                     btnConfirmar.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnReenviar.setVisibility(View.VISIBLE);
                    }
                });

        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                btnConfirmar.setVisibility(View.VISIBLE);
                                btnReenviar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        btnConfirmar.setOnClickListener(v ->
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(getActivity(), "conta verificada com sucesso", Toast.LENGTH_LONG);
                            dismiss();
                        } else {
                            tvInfo.setVisibility(View.VISIBLE);
                        }
                    }
                }));
        return view;
    }
}
