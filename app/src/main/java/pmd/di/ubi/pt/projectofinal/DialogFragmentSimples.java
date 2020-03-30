package pmd.di.ubi.pt.projectofinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DialogFragmentSimples extends DialogFragment {

    private String marcacaoID,toastMessage,acao ,uidUsuario, uidPersonal;

    static DialogFragmentSimples newInstance(String acao, String marcacaoId, String uidUsuario, String uidPersonal) {
        DialogFragmentSimples f = new DialogFragmentSimples();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("acao", acao);
        args.putString("marcacaoID",marcacaoId);
        args.putString("uidUsuario",uidUsuario);
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
        String titulo,mensagem;

        switch (acao) {
            case "cancelada":
                titulo = "Cancelar marcacao";
                mensagem = "Deseja realmente cancelar a marcaçao?";
                toastMessage = "Marcacao cancelada com sucesso";
                break;
            case "aceite":
                titulo = "Aceitar marcacao";
                mensagem = "Deseja realmente aceitar a marcaçao?";
                toastMessage = "Marcacao aceite com sucesso";
                break;
            case "recusada":
                titulo = "Recusar marcacao";
                mensagem = "Deseja realmente recusar a marcaçao?";
                toastMessage = "Marcacao recusada com sucesso";
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
                            FirebaseFirestore.getInstance().collection("marcacoes").document(marcacaoID).update("estado",acao);
                            Toast.makeText(getActivity(),toastMessage,Toast.LENGTH_LONG).show();

                            // atualizar fragment das marcacoes:
                            SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
                            modelData.setAtualizarFragmentMarcaoes(true);
                            boolean isUser = modelData.isUser().getValue();

                            FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

                            Calendar calendar = Calendar.getInstance();
                            //Returns current time in millis
                            long time = calendar.getTimeInMillis();

                            Map<String, Object> notificacaoData;
                            notificacaoData = new HashMap<>();
                            notificacaoData.put("data", time);

                            switch (acao){
                                case "cancelada":
                                    notificacaoData.put("mensagem", user.getDisplayName()+ " cancelou uma marcacao consigo");
                                    notificacaoData.put("titulo","marcaçao cancelada" );;
                                    break;
                                case "recusada":
                                    notificacaoData.put("mensagem", user.getDisplayName()+ " recusou a sua marcaçao");
                                    notificacaoData.put("titulo","marcaçao recusada" );;
                                    break;

                                case "aceite":
                                    notificacaoData.put("mensagem", user.getDisplayName()+ " aceitou uma marcacao consigo");
                                    notificacaoData.put("titulo","marcaçao aceite" );
                                    break;

                            }
                            if(isUser){
                                FirebaseFirestore.getInstance().collection("pessoas")
                                        .document(uidPersonal).collection("notificacoes").document().set(notificacaoData);
                            }
                            else {
                                FirebaseFirestore.getInstance().collection("pessoas")
                                    .document(uidUsuario).collection("notificacoes").document().set(notificacaoData);

                            }





                            dismiss();

                        }).setNegativeButton("Nao",null)
                        .create();
    }
}

