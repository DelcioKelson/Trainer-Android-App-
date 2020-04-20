package pmd.di.ubi.pt.projectofinal;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.transition.Hold;
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

    private ArrayList<Map<String, Object>> marcacaoList;
    private AdapterMarcacao adapterMarcacao;
    private FirebaseUser user;
    private String estadoMarcacao;
    private boolean isUser;
    private RecyclerView recyclerView;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        try {
            isUser = modelData.isUser().getValue();
        } catch (Exception e) {

        }
        setExitTransition(new Hold());


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

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        user = FirebaseAuth.getInstance().getCurrentUser();
        tvInfo = view.findViewById(R.id.tv_page_info);

        if(estadoMarcacao.equals("aceite") && isUser){
            paymentsClient = PaymentsUtil.createPaymentsClient(getActivity());
            modelData.setPaymentsClient(paymentsClient);
        }


        marcacaoList = new ArrayList<>();
        adapterMarcacao = new AdapterMarcacao(getActivity(), marcacaoList, isUser,this,paymentsClient);
        adapterMarcacao.setOnRequestPaymentListener(this);
        recyclerView.setAdapter(adapterMarcacao);
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

                        if (marcacaoEstado != null) {
                            if (estadoMarcacao.equals("default") && (marcacaoEstado.equals("cancelada") || marcacaoEstado.equals("terminada"))) {
                                marcacaoList.add(document.getData());
                            }
                            if (marcacaoEstado.equals(estadoMarcacao)) {
                                marcacaoList.add(document.getData());
                            }
                        }
                    }
                }
                if (marcacaoList.isEmpty()){
                    tvInfo.setVisibility(View.VISIBLE);
                }
                SharedDataModel modelData = new ViewModelProvider(getActivity()).get(SharedDataModel.class);
                modelData.addMarcacoesList(marcacaoList);
                adapterMarcacao.notifyDataSetChanged();
            }
        });
    }

    private void scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(ActivityMain.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(ActivityMain.currentPosition));
                }
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
