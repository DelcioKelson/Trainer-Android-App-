package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FragmentNotificacoes extends Fragment {
    private GridView gridView;
    private ArrayList<Map<String,Object>> notificacoesList;
    AdapterNotificacoes adapterNotificacoes;



    public FragmentNotificacoes() {
        // Required empty public constructor
    }

    public static FragmentNotificacoes newInstance() {
        return  new FragmentNotificacoes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gridview_fragment, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        gridView = view.findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        notificacoesList = new ArrayList<>();
        adapterNotificacoes = new AdapterNotificacoes(getActivity(),notificacoesList);
        gridView.setAdapter(adapterNotificacoes);

        FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid())
                .collection("notificacoes").orderBy("data").get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult()!=null){
                        for (DocumentSnapshot document:task.getResult()){
                            notificacoesList.add(document.getData());
                        }
                        adapterNotificacoes.notifyDataSetChanged();
                    }
                });


        return view;
    }
}
