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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentListaGyms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListaGyms extends Fragment {


    public FragmentListaGyms() {
        // Required empty public constructor
    }

    private ArrayList<Map<String,Object>> gymList;
    private AdapterGym adapterGym;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.recyclerview_layout, container, false);
        //Objects.requireNonNull(getContext().getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Modalidades");
        gymList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));


        adapterGym = new AdapterGym(getActivity(), gymList);
        recyclerView.setAdapter(adapterGym);

        FirebaseFirestore.getInstance().collection("gyms").get().addOnSuccessListener(task -> {
            for (QueryDocumentSnapshot document : task) {
                gymList.add(document.getData());
                Log.d("FirebaseFirestore", document.getId() + " => " + gymList);
            }
            adapterGym.notifyDataSetChanged();
        });
        return view;

    }
}
