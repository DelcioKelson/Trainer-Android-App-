package pmd.di.ubi.pt.projectofinal;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class DialogConfirmarFragmentMarcacao extends DialogFragment {
    private Bundle argumentos;

    private static final String ARG_PARAM1 = "preco";
    private static final String ARG_PARAM2 = "horaTreino";

    private static final String ARG_PARAM3 = "diaTreino";
    private static final String ARG_PARAM4 = "tempoDemora";

    private static final String ARG_PARAM8 = "uidPersonal";

    private String diaTreino,horaTreino,tempoDuracao, uidPersonal,preco;

    private CollectionReference marcacoesRef;
    private FirebaseUser user;




    public static DialogConfirmarFragmentMarcacao newInstance(Bundle bundle) {

        DialogConfirmarFragmentMarcacao dialogConfirmarFragmentMarcacao = new DialogConfirmarFragmentMarcacao();
        dialogConfirmarFragmentMarcacao.setArguments(bundle);
        return dialogConfirmarFragmentMarcacao;
    }

    public interface FlagMarcacaoConfirmadaDialogListener {
        void onFinishEditDialog(int flag);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");

        user = FirebaseAuth.getInstance().getCurrentUser();

        String message = "preço a pagar: " + preco
                + "\nhora de inicio: " + horaTreino
                + "\ndia do treino: " + diaTreino
                + "\ntempo do treino:" + tempoDuracao;



        return new android.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("Sim", (dialog, which) -> {
                    salvarMarcaccao();
                    dismiss();

                }).setNegativeButton("Nao",null)
                .create();
    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            preco = getArguments().getString(ARG_PARAM1);
            horaTreino = getArguments().getString(ARG_PARAM2);
            diaTreino = getArguments().getString(ARG_PARAM3);
            tempoDuracao = getArguments().getString(ARG_PARAM4);;
            uidPersonal = getArguments().getString(ARG_PARAM8);
        }

    }

    public void salvarMarcaccao(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minutos = c.get(Calendar.MINUTE);
        String marcacaoId;
        marcacaoId = marcacoesRef.document().getId();

        preco = preco.replace("€","");

        Map<String, Object> marcacaoData;
        marcacaoData = new HashMap<>();
        marcacaoData.put("diaMarcacao","" + day + "/" + month + "/" + year );
        marcacaoData.put("diaTreino",diaTreino);
        marcacaoData.put("estado", "pendente");
        marcacaoData.put("horaTreino",horaTreino);
        marcacaoData.put("uidPersonal",uidPersonal);
        marcacaoData.put("preco",preco);
        marcacaoData.put("tempoDuracao",tempoDuracao);
        marcacaoData.put("uidUsuario",user.getUid());
        marcacaoData.put("horaMarcacao","" + hora + ":" + minutos);
        marcacaoData.put("marcacaoId",marcacaoId);
        marcacoesRef.document(marcacaoId).set(marcacaoData);


        Map<String, Object> notificacaoData;
        notificacaoData = new HashMap<>();
        notificacaoData.put("titulo","Nova marcacao" );;
        notificacaoData.put("mensagem", user.getDisplayName()+ " criou uma marcacao consigo");
        notificacaoData.put("data", System.currentTimeMillis());
        notificacaoData.put("vista",false);

        DocumentReference notificacaoRef= FirebaseFirestore.getInstance().collection("pessoas")
                .document(uidPersonal).collection("notificacoes").document();

        notificacaoData.put("id",notificacaoRef.getId());
        notificacaoRef.set(notificacaoData);

        Toast.makeText(getActivity(),"Marcaçao realizada com sucesso",Toast.LENGTH_LONG).show();


        FlagMarcacaoConfirmadaDialogListener listener = (FlagMarcacaoConfirmadaDialogListener) getTargetFragment();
        listener.onFinishEditDialog(1);
        dismiss();

    }

}


