package pmd.di.ubi.pt.projectofinal;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONObject;

import java.util.Optional;


public class FragmentDetalhesMarcacao extends Fragment {

    boolean isUser = false;
    private AdapterMarcacao.OnRequestPaymentListener requestPayment;
    private PaymentsClient paymentsClient;

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

        Bundle marcacao = getArguments();

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        try {
            paymentsClient = modelData.getPaymentsClient().getValue();
            isUser = modelData.isUser().getValue();
        } catch (Exception e) {

        }



        final TextView tvDiaTreino = view.findViewById(R.id.tv_dia_detalhe);
        final TextView tvHoraTreino = view.findViewById(R.id.tv_hora_detalhe);
        final TextView tvEstado = view.findViewById(R.id.tv_estado_detalhe);
        final TextView tvPreco = view.findViewById(R.id.tv_preco_detalhe);
        final TextView tvTempoDemora = view.findViewById(R.id.tv_tempo_detalhe);

         final Button btnEsquero = view.findViewById(R.id.btn_esquerdo);
        final Button btnDireito = view.findViewById(R.id.btn_direito);
        final View mGooglePayButton =view.findViewById(R.id.googlepay_button);

        String marcacaoEstado = (String) marcacao.get("estado");
        tvPreco.setText("preço a pagar: " + marcacao.get("preco"));
        tvHoraTreino.setText("hora de inicio: " + marcacao.get("horaTreino"));
        tvDiaTreino.setText("dia do treino: " + marcacao.get("diaTreino"));
        tvTempoDemora.setText("tempo do treino:" + marcacao.get("tempoDuracao"));
        tvEstado.setText("estado da marcaçao :" + marcacaoEstado);
        final  String marcacaoId = (String) marcacao.get("marcacaoId");




        if (isUser) {
            if (marcacaoEstado.equals("pendente")) {
                btnEsquero.setVisibility(View.GONE);

                btnDireito.setVisibility(View.VISIBLE);
                btnDireito.setText("Cancelar Marcacao");
                btnDireito.setOnClickListener(v -> {
                    FragmentTransaction ft1;
                    ft1 = getChildFragmentManager().beginTransaction();
                    Fragment prev1 = getChildFragmentManager().findFragmentByTag("dialog1");
                    if (prev1 != null) {
                        ft1.remove(prev1);
                    }
                    ft1.addToBackStack(null);
                    DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("cancelada",marcacaoId,(String) marcacao.get("uidUsuario"),(String) marcacao.get("uidPersonal"));
                    //dialog.setTargetFragment(this, 300);
                    dialog.show(ft1,"dialog1");
                });
            }
            if(marcacaoEstado.equals("aceite")) {

                possiblyShowGooglePayButton(mGooglePayButton);

                //processarPagamento();
                mGooglePayButton.setOnClickListener(v -> AdapterMarcacao.requestPayment.request((String) marcacao.get("preco")));
            }
        }
        else  {

            if (marcacaoEstado.equals("pendente")) {
                btnEsquero.setVisibility(View.VISIBLE);
                btnEsquero.setText("aceitar");
                btnEsquero.setOnClickListener(v -> {
                    // marcacoesRef.document(marcacao.getId()).update("estado","aceite");

                    FragmentTransaction ft2;

                    ft2 = getChildFragmentManager().beginTransaction();
                    Fragment prev2 = getChildFragmentManager().findFragmentByTag("dialog2");
                    if (prev2 != null) {
                        ft2.remove(prev2);
                    }
                    ft2.addToBackStack(null);
                    DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("aceite",marcacaoId,(String) marcacao.get("uidUsuario"),(String) marcacao.get("uidPersonal"));
                    dialog.show(ft2,"dialog2");
                    Toast.makeText(getContext(),"marcacao aceite com sucesso",Toast.LENGTH_LONG);
                });
                btnDireito.setVisibility(View.VISIBLE);

                btnDireito.setText("Recusar");
                btnDireito.setOnClickListener(v -> {
                    FragmentTransaction ft3;

                    ft3 = getChildFragmentManager().beginTransaction();
                    Fragment prev3 = getChildFragmentManager().findFragmentByTag("dialog3");
                    if (prev3 != null) {
                        ft3.remove(prev3);
                    }
                    ft3.addToBackStack(null);
                    DialogFragmentSimples dialog = DialogFragmentSimples.newInstance("recusada",marcacaoId,(String) marcacao.get("uidUsuario"),(String) marcacao.get("uidPersonal"));
                    dialog.show(ft3,"dialog3");
                    Toast.makeText(getContext(),"marcacao aceite com sucesso",Toast.LENGTH_LONG);
                    //getActivity().getSupportFragmentManager().popBackStack();
                } );
            }
        }


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void possiblyShowGooglePayButton(View mGooglePayButton ) {
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
        task.addOnCompleteListener(getActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            if(task.getResult()){
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

}
