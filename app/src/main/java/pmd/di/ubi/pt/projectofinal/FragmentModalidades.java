package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentModalidades#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentModalidades extends Fragment {

    private ArrayList<Map<String,Object>> modalidadeList;
    private AdapterModalidades adapterModalidades;


    public static FragmentModalidades newInstance() {
        FragmentModalidades fragment = new FragmentModalidades();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.gridview_fragment, container, false);

        //Objects.requireNonNull(getContext().getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Modalidades");
        modalidadeList = new ArrayList<>();




        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        adapterModalidades = new AdapterModalidades(getActivity(), modalidadeList);
        gridView.setAdapter(adapterModalidades);

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bar_opcao_conta:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, FragmentDefinicoesConta.newInstance())
                        .commit();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }
}
