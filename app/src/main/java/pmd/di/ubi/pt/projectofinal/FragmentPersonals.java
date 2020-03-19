package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPersonals#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPersonals extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "modalidade";

    // TODO: Rename and change types of parameters
    private String modalidade;
    private GridView gridView;
    private ArrayList<Map<String, String>> personalList,personalListOriginal;

    private String mParam;
    private AdapterPersonalTrainers adapterPersonalTrainers;
    private Button btnOrdenarPreco,btnOrdenarRating;
    private int jaClicadoRating,jaClicadoPreco;
    private FirebaseUser user;
    private final Comparator<Map<String, String>> precoMapComparador = (p1, p2) -> p1.get("preco").compareTo(p2.get("preco"));
    private final Comparator<Map<String, String>> ratingMapComparador = (p1, p2) -> p1.get("rating").compareTo(p2.get("rating"));


    public FragmentPersonals() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentPersonals newInstance(String param1) {
        FragmentPersonals fragment = new FragmentPersonals();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modalidade = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gridview_fragment, container, false);

        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        LinearLayout llOrdenarOpcoes = view.findViewById(R.id.ll_ordenar_opcoes);
        btnOrdenarPreco = view.findViewById(R.id.btn_ordenar_preco);
        btnOrdenarRating = view.findViewById(R.id.btn_ordenar_rating);
        llOrdenarOpcoes.setVisibility(View.VISIBLE);
        jaClicadoRating = 0;
        jaClicadoPreco = 0;

        gridView =  view.findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        personalList = new ArrayList<>();

        initPersonalList();

        btnOrdenarRating.setOnClickListener(v -> {

            if(jaClicadoRating==0){

                Collections.sort(personalList,precoMapComparador);
                jaClicadoRating =1;
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_more_black_18dp, 0);
                jaClicadoPreco =0;
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            else if (jaClicadoRating==1){
                Collections.reverse(personalList);
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_less_black_18dp, 0);
                jaClicadoRating =2;
            }else if (jaClicadoRating==2){
                personalList = new ArrayList<>(personalListOriginal);
                jaClicadoRating =0;
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            Log.i("jaClicadoRating",jaClicadoRating+"");

            adapterPersonalTrainers = new AdapterPersonalTrainers(getActivity(), personalList);
            gridView.setAdapter(adapterPersonalTrainers);
        });

        btnOrdenarPreco.setOnClickListener(v -> {
            if(jaClicadoPreco==0){
                Collections.sort(personalList,ratingMapComparador);
                jaClicadoPreco =1;
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_more_black_18dp, 0);
                jaClicadoRating =0;
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            else
            if (jaClicadoPreco==1){
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_less_black_18dp, 0);

                Collections.reverse(personalList);
                jaClicadoPreco =2;
            }else
            if (jaClicadoPreco==2){
                personalList = new ArrayList<Map<String, String>>(personalListOriginal);
                jaClicadoPreco =0;
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            adapterPersonalTrainers = new AdapterPersonalTrainers(getActivity(), personalList);
            gridView.setAdapter(adapterPersonalTrainers);
        });


        return view;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent ;
        switch (item.getItemId()) {

            case R.id.bar_opcao_conta:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, FragmentDefinicoesConta.newInstance())
                        .commit();
                return true;

            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void initPersonalList(){


        personalList = new ArrayList<Map<String, String>>();
        FirebaseFirestore.getInstance().collection("pessoas").
                whereEqualTo("tipoConta","personal").
                whereEqualTo("especialidade",modalidade).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                for (DocumentSnapshot document : task.getResult()) {
                    if(document!=null){

                        Map<String,String> newMap =new HashMap<String,String>();
                        for (Map.Entry<String, Object> entry : Objects.requireNonNull(document.getData()).entrySet()) {
                            if(entry.getValue() instanceof String){
                                newMap.put(entry.getKey(), (String) entry.getValue());
                            }
                        }
                        personalList.add(newMap);
                        Log.d("personalAux", document.toString());
                    }
                }
                personalListOriginal = new ArrayList<> (personalList);
                adapterPersonalTrainers = new AdapterPersonalTrainers(getActivity(), personalList);
                gridView.setAdapter(adapterPersonalTrainers);
            }
            else {
                Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
            }
        });



    }


}
