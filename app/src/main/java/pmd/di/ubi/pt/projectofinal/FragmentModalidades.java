package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FragmentModalidades extends Fragment {

    private ArrayList<Map<String, Object>> modalidadeList;
    private AdapterModalidades adapterModalidades;
    private RecyclerView recyclerView;


    public static FragmentModalidades newInstance() {
        FragmentModalidades fragment = new FragmentModalidades();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);

        //Objects.requireNonNull(getContext().getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Modalidades");
        modalidadeList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));


        adapterModalidades = new AdapterModalidades(getActivity(), modalidadeList);
        recyclerView.setAdapter(adapterModalidades);


        String gymNome;
        if (getArguments() == null) {
            gymNome = "nao";

        } else {
            gymNome = getArguments().getString("nome");
        }

        Log.i("FragmentModalidades", gymNome);

        FirebaseFirestore.getInstance().collection("modalidades").whereEqualTo("gym", gymNome).get().addOnSuccessListener(task -> {
            for (QueryDocumentSnapshot document : task) {
                modalidadeList.add(document.getData());
                Log.d("FirebaseFirestore", document.getId() + " => " + modalidadeList);
            }
            adapterModalidades.notifyDataSetChanged();

        });
        return view;
    }
}
