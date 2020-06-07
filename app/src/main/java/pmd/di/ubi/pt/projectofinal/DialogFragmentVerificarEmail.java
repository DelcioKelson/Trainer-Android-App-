package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class DialogFragmentVerificarEmail extends DialogFragment {

    private FirebaseUser user;
    private Button btnConfirmar,btnReenviar,btnSair;

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
        btnConfirmar = view.findViewById(R.id.btn_confirmar_email);
        btnReenviar = view.findViewById(R.id.btn_reenviar_email);
        btnSair = view.findViewById(R.id.btn_sair);

        final TextView tvInfo = view.findViewById(R.id.tv_confirmacao_info);
        user.sendEmailVerification()
                .addOnSuccessListener(task -> {
                     btnConfirmar.setVisibility(View.VISIBLE);
                     btnReenviar.setVisibility(View.VISIBLE);

                });

        btnReenviar.setOnClickListener(v ->
                user.sendEmailVerification().addOnSuccessListener(task -> {
                        Snackbar.make(view,"Foi enviado um novo email de verificaçao",Snackbar.LENGTH_LONG).show();


                }));

        btnConfirmar.setOnClickListener(v ->
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(getActivity(), "conta verificada com sucesso", Toast.LENGTH_LONG);
                            dismiss();
                        } else {
                            Snackbar.make(v,"Conta nao verificada",Snackbar.LENGTH_LONG).show();
                        }
                    }
                }));


        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());
            Intent intent = new Intent(getActivity(), Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        return view;
    }
}
