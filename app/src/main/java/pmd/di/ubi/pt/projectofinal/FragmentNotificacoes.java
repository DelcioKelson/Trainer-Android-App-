package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.QuickContactBadge;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class FragmentNotificacoes extends Fragment {
    private ArrayList<Map<String,Object>> notificacoesList;
    AdapterNotificacoes adapterNotificacoes;
        CollectionReference notificacoesRef;

    private RecyclerView recyclerView;
    public FragmentNotificacoes() {
        // Required empty public constructor
    }

    public static FragmentNotificacoes newInstance() {
        return  new FragmentNotificacoes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        notificacoesRef = FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid())
                .collection("notificacoes");


        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        notificacoesList = new ArrayList<>();



        notificacoesRef.orderBy("data", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult()!=null){
                        for (DocumentSnapshot document:task.getResult()){
                            notificacoesList.add(document.getData());

                        }
                        ActivityMain.badge.setVisible(false);
                        adapterNotificacoes = new AdapterNotificacoes(getActivity(),notificacoesList, user.getUid());
                        recyclerView.setAdapter(adapterNotificacoes);
                    }
                });


        return view;
    }
}
