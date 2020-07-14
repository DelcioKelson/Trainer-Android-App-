package pmd.di.ubi.pt.projectofinal;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DialogFragmentSimples extends DialogFragment {

    private String marcacaoID, toastMessage, acao, uidUsuario, uidPersonal;

    static DialogFragmentSimples newInstance(String acao, String marcacaoId, String uidUsuario, String uidPersonal) {
        DialogFragmentSimples f = new DialogFragmentSimples();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("acao", acao);
        args.putString("marcacaoID", marcacaoId);
        args.putString("uidUsuario", uidUsuario);
        args.putString("uidPersonal", uidPersonal);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        acao = getArguments().getString("acao");
        marcacaoID = getArguments().getString("marcacaoID");
        uidUsuario = getArguments().getString("uidUsuario");
        uidPersonal = getArguments().getString("uidPersonal");
        String titulo, mensagem;

        switch (acao) {
            case "cancelada":
                titulo = "Cancelar marcação";
                mensagem = "Deseja realmente cancelar a marcação?";
                toastMessage = "marcação cancelada com sucesso";
                break;
            case "aceite":
                titulo = "Aceitar marcação";
                mensagem = "Deseja realmente aceitar a marcação?";
                toastMessage = "Marcação aceite com sucesso";
                break;
            case "recusada":
                titulo = "Recusar marcação";
                mensagem = "Deseja realmente recusar a marcação?";
                toastMessage = "Marcação recusada com sucesso";
                break;
            default:
                titulo = "error";
                mensagem = "error";
                break;

        }

        return new android.app.AlertDialog.Builder(getActivity())
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("Sim", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("marcacoes").document(marcacaoID).update("estado", acao);
                    Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG).show();

                    // atualizar fragment das marcacoes:
                    Main.sharedDataModel.setAtualizar(true);
                    boolean isUser = Main.sharedDataModel.isUser().getValue();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    Map<String, Object> notificacaoData;
                    notificacaoData = new HashMap<>();
                    notificacaoData.put("data", System.currentTimeMillis());
                    notificacaoData.put("vista", false);
                    notificacaoData.put("marcacaoId", marcacaoID);

                    switch (acao) {
                        case "cancelada":
                            notificacaoData.put("mensagem", user.getDisplayName() + " cancelou uma marcação consigo");
                            notificacaoData.put("titulo", "marcação cancelada");
                            break;
                        case "recusada":
                            notificacaoData.put("mensagem", user.getDisplayName() + " recusou a sua marcação");
                            notificacaoData.put("titulo", "marcação recusada");
                            break;

                        case "aceite":
                            notificacaoData.put("mensagem", user.getDisplayName() + " aceitou uma marcação consigo");
                            notificacaoData.put("titulo", "marcação aceite");
                            break;

                    }
                    if (isUser) {

                        DocumentReference notificacaoRef = FirebaseFirestore.getInstance().collection("pessoas")
                                .document(uidPersonal).collection("notificacoes").document();

                        notificacaoData.put("id", notificacaoRef.getId());
                        notificacaoRef.set(notificacaoData);
                    } else {
                        DocumentReference notificacaoRef = FirebaseFirestore.getInstance().collection("pessoas")
                                .document(uidUsuario).collection("notificacoes").document();

                        notificacaoData.put("id", notificacaoRef.getId());
                        notificacaoRef.set(notificacaoData);

                    }

                    dismiss();

                }).setNegativeButton("Nao", null)
                .create();
    }
}

