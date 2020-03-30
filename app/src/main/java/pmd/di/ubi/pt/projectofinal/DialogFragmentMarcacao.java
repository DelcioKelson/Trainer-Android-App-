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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class DialogFragmentMarcacao extends DialogFragment {
    private Bundle argumentos;

    private static final String ARG_PARAM1 = "preco";
    private static final String ARG_PARAM2 = "horaTreino";

    private static final String ARG_PARAM3 = "diaTreino";
    private static final String ARG_PARAM4 = "tempoDemora";

    private static final String ARG_PARAM5 = "tipoConta";
    private static final String ARG_PARAM6 = "estado";
    private static final String ARG_PARAM7 = "idMarcacao";
    private static final String ARG_PARAM8 = "uidPersonal";

    private String diaTreino,horaTreino,tempoDuracao, uidPersonal,preco;

    private TextView tvDiaTreino,tvHoraTreino, tvPreco,tvEstado,tvTempoDemora,tvObs;
    private CollectionReference marcacoesRef;
    private FirebaseUser user;

    private Button btnCancelar,btnConfirmar;



    public static DialogFragmentMarcacao newInstance(Bundle bundle) {

        DialogFragmentMarcacao dialogFragmentMarcacao = new DialogFragmentMarcacao();
        dialogFragmentMarcacao.setArguments(bundle);
        return dialogFragmentMarcacao;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialogfragment_confirmar_marcacao,container,true);

        marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");

        user = FirebaseAuth.getInstance().getCurrentUser();

        tvDiaTreino = view.findViewById(R.id.tv_dia_detalhe);
        tvHoraTreino = view.findViewById(R.id.tv_hora_detalhe);
        tvPreco = view.findViewById(R.id.tv_preco_detalhe);
        tvTempoDemora = view.findViewById(R.id.tv_tempo_detalhe);
        btnCancelar = view.findViewById(R.id.btn_cancelar);
        btnConfirmar =  view.findViewById(R.id.btn_confirmar);
        btnCancelar.setOnClickListener(v -> dismiss());
        btnConfirmar.setOnClickListener(v -> salvarMarcaccao());

        tvPreco.setText("preço a pagar: " + preco);
        tvHoraTreino.setText("hora de inicio: " + horaTreino);
        tvDiaTreino.setText("dia do treino: " + diaTreino);
        tvTempoDemora.setText("tempo do treino:" + tempoDuracao);

        return view;
    }

    public interface FlagMarcacaoConfirmadaDialogListener {
        void onFinishEditDialog(int flag);
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


        Calendar calendar = Calendar.getInstance();
        //Returns current time in millis
        long time = calendar.getTimeInMillis();

        Map<String, Object> notificacaoData;
        notificacaoData = new HashMap<>();
        notificacaoData.put("titulo","Nova marcacao" );;
        notificacaoData.put("mensagem", user.getDisplayName()+ " criou uma marcacao consigo");
        notificacaoData.put("data", time);

        FirebaseFirestore.getInstance().collection("pessoas")
                .document(uidPersonal).collection("notificacoes").document().set(notificacaoData);

        Toast.makeText(getActivity(),"Marcaçao realizada com sucesso",Toast.LENGTH_LONG).show();


        FlagMarcacaoConfirmadaDialogListener listener = (FlagMarcacaoConfirmadaDialogListener) getTargetFragment();
        listener.onFinishEditDialog(1);
        dismiss();

    }

}


