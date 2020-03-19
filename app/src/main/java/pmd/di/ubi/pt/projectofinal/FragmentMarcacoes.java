package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMarcacoes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMarcacoes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tipoConta";
    private static final String ARG_PARAM2 = "estado";

    private ArrayList<Map<String, Object>> marcacaoArrayList;
    private AdapterMarcacao adapterMarcacao;
    private FirebaseUser user;
    private String estadoMarcacao, tipoDeConta;

    // TODO: Rename and change types of parameters


    public FragmentMarcacoes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarcacoesFragment.
     */
    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipoDeConta = getArguments().getString(ARG_PARAM1);
            estadoMarcacao = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gridview_fragment, container, false);

        marcacaoArrayList = new ArrayList<java.util.Map<String, Object>>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        adapterMarcacao = new AdapterMarcacao(getActivity(), marcacaoArrayList, tipoDeConta);
        gridView.setAdapter(adapterMarcacao);
        initMarcacoes();


        return view;
    }


    public void initMarcacoes() {

        String auxTipoConta = tipoDeConta.equals("usuario")? "uidUsuario":"uidPersonal";
        FirebaseFirestore.getInstance().collection("marcacoes").whereEqualTo(auxTipoConta,user.getUid())
                .get().addOnCompleteListener(task -> {
            QuerySnapshot documentSnapshots = task.getResult();
            if (documentSnapshots != null) {
                for (DocumentSnapshot document : documentSnapshots) {
                    if (document!=null){
                        String marcacaoEstado = (String) document.get("estado");
                        if(marcacaoEstado!=null){
                            if (estadoMarcacao.equals("default") && (marcacaoEstado.equals("cancelada") || marcacaoEstado.equals("terminada"))) {
                                marcacaoArrayList.add(document.getData());
                            }
                            if (marcacaoEstado.equals(estadoMarcacao)) {
                                marcacaoArrayList.add(document.getData());
                            }
                        }

                    }
                }
                Log.d("FirebaseFirestore", marcacaoArrayList.toString());
                adapterMarcacao.notifyDataSetChanged();
            }
        });
    }
}
