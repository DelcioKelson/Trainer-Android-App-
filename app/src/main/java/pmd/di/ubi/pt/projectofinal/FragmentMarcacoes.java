package pmd.di.ubi.pt.projectofinal;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FragmentMarcacoes extends Fragment implements  AdapterMarcacao.OnRequestPaymentListener{

    private static final String ARG_PARAM2 = "estado";

    private ArrayList<Map<String, String>> marcacaoArrayList;
    private AdapterMarcacao adapterMarcacao;
    private FirebaseUser user;
    private String estadoMarcacao;
    private boolean isUser;
    private GridView gridView;
    TextView tvInfo;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient paymentsClient;

    public FragmentMarcacoes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            estadoMarcacao = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gridview_fragment, container, false);

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        try {
            isUser = modelData.isUser().getValue();
        } catch (Exception e) {

        }

        modelData.getAtualizarFragmentMarcaoes().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (Build.VERSION.SDK_INT >= 26) {
                        ft.setReorderingAllowed(false);
                    }
                    ft.detach(FragmentMarcacoes.this).attach(FragmentMarcacoes.this).commit() ;
                    modelData.setAtualizarFragmentMarcaoes(false);
                }
            }
        });

        marcacaoArrayList = new ArrayList<java.util.Map<String, String>>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        gridView = view.findViewById(R.id.gridview);
        tvInfo = view.findViewById(R.id.tv_page_info);
        gridView.setNumColumns(1);

        if(estadoMarcacao.equals("aceite") && isUser){
            paymentsClient = PaymentsUtil.createPaymentsClient(getActivity());
        }

        adapterMarcacao = new AdapterMarcacao(getActivity(), marcacaoArrayList, isUser,this,paymentsClient);
        adapterMarcacao.setOnRequestPaymentListener(this);
        gridView.setAdapter(adapterMarcacao);
        initMarcacoes();
        return view;
    }

    public void initMarcacoes() {
        String auxTipoConta = isUser ? "uidUsuario" : "uidPersonal";
        FirebaseFirestore.getInstance().collection("marcacoes").whereEqualTo(auxTipoConta, user.getUid())
                .get().addOnCompleteListener(task -> {
            QuerySnapshot documentSnapshots = task.getResult();
            if (documentSnapshots != null) {
                for (DocumentSnapshot document : documentSnapshots) {
                    if (document != null) {
                        String marcacaoEstado = (String) document.get("estado");
                        Map<String,String> newMap =new HashMap<String,String>();
                        for (Map.Entry<String, Object> entry : Objects.requireNonNull(document.getData()).entrySet()) {
                            if(entry.getValue() instanceof String){
                                newMap.put(entry.getKey(), (String) entry.getValue());
                            }
                        }
                        if (marcacaoEstado != null) {
                            if (estadoMarcacao.equals("default") && (marcacaoEstado.equals("cancelada") || marcacaoEstado.equals("terminada"))) {
                                marcacaoArrayList.add(newMap);
                            }
                            if (marcacaoEstado.equals(estadoMarcacao)) {
                                marcacaoArrayList.add(newMap);
                            }
                        }
                    }
                }
                Log.d("FirebaseFirestore", marcacaoArrayList.toString());
                if (marcacaoArrayList.isEmpty()){
                    tvInfo.setVisibility(View.VISIBLE);
                }
                adapterMarcacao.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void request(String price) {
        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest requestP =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        Log.w("loadPayment", "onrequest");

        if (requestP != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(requestP), getActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }
}
