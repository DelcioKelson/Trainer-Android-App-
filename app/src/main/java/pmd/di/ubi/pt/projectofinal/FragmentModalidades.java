package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialFade;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FragmentModalidades extends Fragment {

    private ArrayList<Map<String,Object>> modalidadeList;
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
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));


        adapterModalidades = new AdapterModalidades(getActivity(), modalidadeList);
        recyclerView.setAdapter(adapterModalidades);

        FirebaseFirestore.getInstance().collection("modalidades").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    modalidadeList.add(document.getData());
                    Log.d("FirebaseFirestore", document.getId() + " => " + modalidadeList);
                }
                adapterModalidades.notifyDataSetChanged();
            }
            else {
                Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
            }
        });
        return view;
    }
}
