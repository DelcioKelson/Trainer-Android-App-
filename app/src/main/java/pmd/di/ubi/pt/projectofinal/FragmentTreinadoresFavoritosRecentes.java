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

import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTreinadoresFavoritosRecentes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTreinadoresFavoritosRecentes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private GridView gridView;
    private ArrayList<Map<String, Object>> personalList;
    private String[] idsPersonais;
    FirebaseUser user;

    private RecyclerView recyclerView;

    private AdapterPersonalFavoritosRecentes adapterPersonalFavoritosRecentes;


    public FragmentTreinadoresFavoritosRecentes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        personalList = new ArrayList<>();

        idsPersonais = new String[10];

        String tab = getArguments().getString("tab");
        Log.i("tabName", tab);

        if (tab.equals("recente")) {
            initPersonalListRecente();
        } else {
            initPersonalListFavorito();
        }

        return view;
    }

    private void initPersonalListFavorito() {

        FirebaseFirestore.getInstance().collection("pessoas").
                whereEqualTo("tipoConta", "personal").whereEqualTo("favorito."+user.getUid(), true).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot document : task.getResult()) {
                    if (document != null) {
                        personalList.add(document.getData());
                        Log.d("personalAux", document.toString());
                    }
                }
                adapterPersonalFavoritosRecentes = new AdapterPersonalFavoritosRecentes(getActivity(), personalList);
                recyclerView.setAdapter(adapterPersonalFavoritosRecentes);
            } else {
                Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
            }
        });

    }

    private void initPersonalListRecente(){

        FirebaseFirestore.getInstance().collection("marcacoes").
                whereEqualTo("uidUsuario",user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        int limit = 10;

                        if(documents.size()<10){
                            limit = documents.size();
                        }
                        for(int i= 0; i<limit ; i++){
                            idsPersonais[i]=documents.get(i).getString("uidPersonal");
                        }

                        try {
                            FirebaseFirestore.getInstance().collection("pessoas").
                                    whereEqualTo("tipoConta","personal").whereIn("uid", Arrays.asList(idsPersonais)).get().addOnCompleteListener(task1 -> {
                                if (task.isSuccessful() && task1.getResult()!=null) {
                                    for (DocumentSnapshot document : task1.getResult()) {
                                        if(document!=null){
                                            personalList.add(document.getData());
                                            Log.d("personalAux", document.toString());
                                        }
                                    }
                                    adapterPersonalFavoritosRecentes = new AdapterPersonalFavoritosRecentes(getActivity(), personalList);
                                    recyclerView.setAdapter(adapterPersonalFavoritosRecentes);
                                }
                                else {
                                    Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
                                }
                            });
                        }catch (Exception e){

                        }


                    }
                });

    }
}
