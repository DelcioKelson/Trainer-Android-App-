package pmd.di.ubi.pt.projectofinal;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class FragmentNotificacoes extends Fragment implements AdapterNotificacoes.OnRequestPaymentListener {
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    AdapterNotificacoes adapterNotificacoes;
    CollectionReference notificacoesRef;
    private ArrayList<Map<String, Object>> notificacoesList;
    private PaymentsClient paymentsClient;


    private RecyclerView recyclerView;

    public FragmentNotificacoes() {
        // Required empty public constructor
    }

    public static FragmentNotificacoes newInstance() {
        return new FragmentNotificacoes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        notificacoesRef = FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid())
                .collection("notificacoes");


        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);


        paymentsClient = Main.sharedDataModel.getPaymentsClient().getValue();


        Main.sharedDataModel.getFecharViewPager().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Main.sharedDataModel.setFecharViewPager(false);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (Build.VERSION.SDK_INT >= 26) {
                        ft.setReorderingAllowed(false);
                    }
                    ft.detach(FragmentNotificacoes.this).attach(FragmentNotificacoes.this).commit();
                    Main.sharedDataModel.setAtualizar(false);
                }
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        notificacoesList = new ArrayList<>();


        notificacoesRef.orderBy("data", Query.Direction.DESCENDING).get().addOnSuccessListener(task -> {
            for (DocumentSnapshot document : task) {
                notificacoesList.add(document.getData());

            }
            Main.badge.setVisible(false);
            adapterNotificacoes = new AdapterNotificacoes(this, notificacoesList, user.getUid());
            adapterNotificacoes.setOnRequestPaymentListener(this);
            recyclerView.setAdapter(adapterNotificacoes);
        });


        return view;
    }

    @Override
    public void request(String price) {
        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest("0");
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
