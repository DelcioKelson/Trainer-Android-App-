package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class FragmentTreinadoresFavoritosRecentes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    FirebaseUser user;
    private ArrayList<Map<String, Object>> personalListRecente, personalListFavorito;
    private ArrayList<String> idsPersonais;
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
        recyclerView = view.findViewById(R.id.my_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        personalListRecente = new ArrayList<>();
        personalListFavorito = new ArrayList<>();

        idsPersonais = new ArrayList<>();

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
                whereEqualTo("tipoConta", "personal").whereEqualTo("favorito." + user.getUid(), true).get().addOnSuccessListener(task -> {
            for (DocumentSnapshot document : task) {
                if (document != null) {
                    personalListFavorito.add(document.getData());
                    Log.d("personalAux", document.toString());
                }
            }
            Main.sharedDataModel.setPersonalListFavorito(personalListFavorito);
            adapterPersonalFavoritosRecentes = new AdapterPersonalFavoritosRecentes(getActivity(), personalListFavorito, true);
            recyclerView.setAdapter(adapterPersonalFavoritosRecentes);
        });

    }

    private void initPersonalListRecente() {

        FirebaseFirestore.getInstance().collection("marcacoes").
                whereEqualTo("uidUsuario", user.getUid()).get()
                .addOnSuccessListener(task -> {
                    String id = "";
                    int i = 0;
                    for (DocumentSnapshot documentSnapshot : task.getDocuments()) {
                        id = documentSnapshot.getString("uidPersonal");
                        if (i == 10) {
                            break;
                        }
                        if (!idsPersonais.contains(id)) {
                            idsPersonais.add(id);
                            i++;
                        }
                    }

                    try {
                        FirebaseFirestore.getInstance().collection("pessoas").
                                whereEqualTo("tipoConta", "personal").get().addOnSuccessListener(task1 -> {
                            for (DocumentSnapshot document : task1) {
                                if (document != null && idsPersonais.contains(document.getId())) {
                                    personalListRecente.add(document.getData());
                                    Log.d("personalAux1", document.toString());
                                }
                            }
                            Main.sharedDataModel.addPersonalList(personalListRecente);
                            adapterPersonalFavoritosRecentes = new AdapterPersonalFavoritosRecentes(getActivity(), personalListRecente, false);
                            recyclerView.setAdapter(adapterPersonalFavoritosRecentes);

                        });
                    } catch (Exception e) {
                    }

                });
    }
}
