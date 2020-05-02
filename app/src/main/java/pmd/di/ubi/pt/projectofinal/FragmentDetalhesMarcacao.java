package pmd.di.ubi.pt.projectofinal;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class FragmentDetalhesMarcacao extends DialogFragment {

    boolean isUser = false;
    private PaymentsClient paymentsClient;

    private TextView tvDiaTreino, tvHoraTreino, tvEstado, tvPreco, tvTempoDemora;

    private Button btnEsquero, btnDireito;
    private View mGooglePayButton;
    private TextView tvNome, tvNomeCliente, tvMorada, tvSituacoes, tvTelefone, tvDataCriacao, tvPeso, tvAltura, tvGenero, tvIdade;

    private Animation animationUp;
    private Animation animationDown;

    private CardView cardView;
    private LinearLayout informacoes;
    private ToggleButton tbExpand;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private CollectionReference pessoasRef = FirebaseFirestore.getInstance().collection("pessoas");


    private Map<String, Object> marcacao = new HashMap<>();

    public FragmentDetalhesMarcacao() {
        // Required empty public constructor
    }

    public static FragmentDetalhesMarcacao newInstance(Bundle args) {
        FragmentDetalhesMarcacao fragment = new FragmentDetalhesMarcacao();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalhes_marcacao, container, false);

        tvDiaTreino = view.findViewById(R.id.tv_dia_detalhe);
        tvHoraTreino = view.findViewById(R.id.tv_hora_detalhe);
        tvEstado = view.findViewById(R.id.tv_estado_detalhe);
        tvPreco = view.findViewById(R.id.tv_preco_detalhe);
        tvTempoDemora = view.findViewById(R.id.tv_tempo_detalhe);
        tvNome = view.findViewById(R.id.tv_nome_detalhe);
        cardView = view.findViewById(R.id.card_informacoes);
        tvDataCriacao = view.findViewById(R.id.tv_data);

        informacoes = view.findViewById(R.id.informacoes_cliente);
        tbExpand = view.findViewById(R.id.tb_exapand);

        tvSituacoes = view.findViewById(R.id.tv_situacoes);
        tvNomeCliente = view.findViewById(R.id.tv_nome_cliente);
        tvMorada = view.findViewById(R.id.tv_morada);
        tvTelefone = view.findViewById(R.id.tv_telefone);
        tvGenero = view.findViewById(R.id.tv_genero);
        tvIdade = view.findViewById(R.id.tv_idade);
        tvAltura = view.findViewById(R.id.tv_altura);
        tvPeso = view.findViewById(R.id.tv_peso);

        btnEsquero = view.findViewById(R.id.btn_esquerdo);
        btnDireito = view.findViewById(R.id.btn_direito);
        mGooglePayButton = view.findViewById(R.id.googlepay_button);
        animationUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);


        try {
            paymentsClient = Main.sharedDataModel.getPaymentsClient().getValue();
            isUser = Main.sharedDataModel.isUser().getValue();
        } catch (Exception e) {

        }
        cardView.setOnClickListener(v -> {
            if (tbExpand.isChecked()) {
                tbExpand.setChecked(false);
                informacoes.setVisibility(View.GONE);
                informacoes.startAnimation(animationUp);
            } else {
                tbExpand.setChecked(true);
                informacoes.setVisibility(View.VISIBLE);
                informacoes.startAnimation(animationDown);
            }
        });


        String idMarcacao = getArguments().getString("marcacaoId");
        if (idMarcacao != null) {

            FirebaseFirestore.getInstance().collection("marcacoes").document(idMarcacao).get().
                    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {

                                marcacao = task.getResult().getData();
                                opcoesDialog();
                                inicializarDados();

                            }
                        }
                    });
            Button btnClose = view.findViewById(R.id.btn_close);
            btnClose.setVisibility(View.VISIBLE);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            mGooglePayButton.setOnClickListener(v -> AdapterNotificacoes.requestPayment.request((String) marcacao.get("preco")));


        } else {
            marcacao = Main.sharedDataModel.getMarcacoesList().getValue().get(getArguments().getInt("pos"));
            opcoesDialog();
            inicializarDados();

            mGooglePayButton.setOnClickListener(v -> AdapterMarcacao.requestPayment.request((String) marcacao.get("preco")));
        }

        Main.sharedDataModel.getAtualizar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    FragmentDetalhesMarcacao.this.dismiss();
                    Main.sharedDataModel.setAtualizar(false);
                }
            }
        });

        return view;
    }

    private void opcoesDialog() {
        String marcacaoEstado = (String) marcacao.get("estado");
        tvPreco.setText("Preço: " + marcacao.get("preco") + "€");
        tvHoraTreino.setText("Hora: " + marcacao.get("horaTreino"));
        tvDiaTreino.setText("Dia: " + marcacao.get("diaTreino"));
        tvTempoDemora.setText("Duração: " + marcacao.get("tempoDuracao"));
        tvEstado.setText("Estado: " + marcacaoEstado);
        tvDataCriacao.setText("Criada em: " + marcacao.get("diaMarcacao") + " ás " + marcacao.get("horaMarcacao") + "h");

        final String marcacaoId = (String) marcacao.get("marcacaoId");
        final String idPersonal = (String) marcacao.get("uidPersonal");
        final String idUsuario = (String) marcacao.get("uidUsuario");

        final DocumentReference marcacaoReference = FirebaseFirestore.getInstance().collection("marcacoes").document(marcacaoId);

        Log.i("marcacaoId", marcacaoId);


        if (isUser) {
            if (marcacaoEstado.equals("pendente")) {
                btnEsquero.setVisibility(View.GONE);

                btnDireito.setVisibility(View.VISIBLE);
                btnDireito.setText("Cancelar");
                btnDireito.setOnClickListener(v -> {

                    marcacaoReference.update("estado", "cancelada");
                    Toast.makeText(getActivity(), "Marcação cancelada com sucesso", Toast.LENGTH_LONG).show();

                    Map<String, Object> notificacaoData;
                    notificacaoData = new HashMap<>();
                    notificacaoData.put("data", System.currentTimeMillis());
                    notificacaoData.put("vista", false);
                    notificacaoData.put("marcacaoId", marcacaoId);

                    notificacaoData.put("mensagem", user.getDisplayName() + " cancelou uma marcação consigo");
                    notificacaoData.put("titulo", "Marcação cancelada");
                    DocumentReference notificacaoRef = pessoasRef
                            .document(idPersonal).collection("notificacoes").document();

                    notificacaoData.put("id", notificacaoRef.getId());
                    notificacaoRef.set(notificacaoData);
                    Main.sharedDataModel.setAtualizar(true);
                    Main.sharedDataModel.setFecharViewPager(true);

                });
            }
            if (marcacaoEstado.equals("aceite")) {

                possiblyShowGooglePayButton(mGooglePayButton);

                //processarPagamento();
            }
        } else {

            if (marcacaoEstado.equals("pendente")) {
                btnEsquero.setVisibility(View.VISIBLE);
                btnEsquero.setText("aceitar");
                btnEsquero.setOnClickListener(v -> {

                    // marcacoesRef.document(marcacao.getId()).update("estado","aceite");
                    marcacaoReference.update("estado", "aceite");
                    Toast.makeText(getActivity(), "Marcação aceite com sucesso", Toast.LENGTH_LONG).show();

                    Map<String, Object> notificacaoData;
                    notificacaoData = new HashMap<>();
                    notificacaoData.put("data", System.currentTimeMillis());
                    notificacaoData.put("vista", false);
                    notificacaoData.put("mensagem", user.getDisplayName() + " aceitou uma marcação consigo");
                    notificacaoData.put("titulo", "marcação aceite");
                    notificacaoData.put("marcacaoId", marcacaoId);

                    DocumentReference notificacaoRef = pessoasRef
                            .document(idUsuario).collection("notificacoes").document();

                    notificacaoData.put("id", notificacaoRef.getId());
                    notificacaoRef.set(notificacaoData);
                    Main.sharedDataModel.setAtualizar(true);
                    Main.sharedDataModel.setFecharViewPager(true);

                });
                btnDireito.setVisibility(View.VISIBLE);

                btnDireito.setText("Recusar");
                btnDireito.setOnClickListener(v -> {


                    marcacaoReference.update("estado", "recusada");
                    Toast.makeText(getActivity(), "Marcação recusada com sucesso", Toast.LENGTH_LONG).show();

                    Map<String, Object> notificacaoData;
                    notificacaoData = new HashMap<>();
                    notificacaoData.put("data", System.currentTimeMillis());
                    notificacaoData.put("vista", false);
                    notificacaoData.put("mensagem", user.getDisplayName() + " recusou a sua marcação");
                    notificacaoData.put("titulo", "Marcação recusada");

                    DocumentReference notificacaoRef = pessoasRef
                            .document(idUsuario).collection("notificacoes").document();

                    notificacaoData.put("id", notificacaoRef.getId());
                    notificacaoRef.set(notificacaoData);
                    Main.sharedDataModel.setAtualizar(true);
                    Main.sharedDataModel.setFecharViewPager(true);
                    notificacaoData.put("marcacaoId", marcacaoId);
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void possiblyShowGooglePayButton(View mGooglePayButton) {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(requireActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult()) {
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }


    void inicializarDados() {

        pessoasRef.document(isUser ? (String) marcacao.get("uidPersonal") : (String) marcacao.get("uidUsuario"))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                Map<String, Object> pessoa = task.getResult().getData();

                if (isUser) {
                    tvNome.setText("Personal Trainer: " + pessoa.get("nome"));
                } else {
                    cardView.setVisibility(View.VISIBLE);
                    tvNomeCliente.setText("Nome: " + pessoa.get("nome"));
                    tvMorada.setText("Morada: " + pessoa.get("morada"));
                    tvTelefone.setText("Telefone: " + pessoa.get("numeroTelefone"));
                    tvPeso.setText("Peso:" + pessoa.get("peso"));
                    tvAltura.setText("Altura:" + pessoa.get("altura"));

                    int atualAno = Calendar.getInstance().get(Calendar.YEAR);
                    String idadeString = (String) pessoa.get("dataNascimento");
                    int anoNascimento = Integer.parseInt(idadeString.split("/")[2]);

                    tvIdade.setText("Idade: " + (atualAno - anoNascimento));
                    tvGenero.setText("Gênero:" + pessoa.get("genero"));

                    String s = (String) pessoa.get("situacoes");
                    if (s != null) {
                        tvSituacoes.setText(s);
                    }
                }
            }
        });
    }
}
