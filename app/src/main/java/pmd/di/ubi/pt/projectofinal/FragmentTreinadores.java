package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentTreinadores extends Fragment implements DialogFragmentOrdernarFiltrar.DialogFragmentOrdernarMenuDialogListener, DialogFragmentOrdernarFiltrar.DialogFragmentFiltrarMenuDialogListener {

    private static final String ARG_PARAM1 = "modalidade";

    // TODO: Rename and change types of parameters
    private String modalidade;
    private GridView gridView;
    private ArrayList<Map<String, String>> personalList,personalListOriginal;
    private int idOpOrdem =0;

    private boolean disponivel = false;
    private boolean diaDisponivel = false;
    private String diaDisponibilidade = "Dia/Mes/Ano";
    private AdapterTreinadores adapterTreinadores;
    private FirebaseUser user;
    private final Comparator<Map<String, String>> precoMapComparador = (p1, p2) -> p1.get("preco").compareTo(p2.get("preco"));
    private final Comparator<Map<String, String>> ratingMapComparador = (p1, p2) -> p1.get("rating").compareTo(p2.get("rating"));


    public FragmentTreinadores() {
        // Required empty public constructor
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
        setHasOptionsMenu(true);
        gridView =  view.findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        personalList = new ArrayList<>();
        initPersonalList();
        return view;
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
                adapterTreinadores = new AdapterTreinadores(getActivity(), personalList);
                gridView.setAdapter(adapterTreinadores);
            }
            else {
                Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
            }
        });
    }

   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_personais, menu);
       super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        DialogFragment newFragment;
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        switch (item.getItemId()) {
            case R.id.ordenar_menu:
                newFragment = DialogFragmentOrdernarFiltrar.newInstance(idOpOrdem, disponivel, diaDisponivel,diaDisponibilidade);
                newFragment.setTargetFragment(this, 300);
                newFragment.show(ft, "dialog");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFinishOrdernarMenuDialog(int selectedItem) {

        switch (selectedItem){
            case R.id.radioButton_p_mema:
                Collections.sort(personalList,precoMapComparador);
                break;
            case R.id.radioButton_p_mame:
                Collections.sort(personalList,precoMapComparador);
                Collections.reverse(personalList);
                break;
            case R.id.radioButton_c_mema:
                Collections.sort(personalList,ratingMapComparador);
                break;
            case R.id.radioButton_c_mame:
                Collections.sort(personalList,ratingMapComparador);
                Collections.reverse(personalList);
                break;
        }
        idOpOrdem = selectedItem;
        adapterTreinadores.notifyDataSetChanged();
    }

    @Override
    public void onFinishFiltrarMenuDialog(boolean disponiveis, boolean disponiveisDia, String diaMesAno) {

        if(disponiveis!=disponivel || disponiveisDia!=diaDisponivel || !diaDisponibilidade.equals(diaMesAno)){
            for(Map<String, String> personal: personalListOriginal ) {
                if (!personalList.contains(personal)) {
                    personalList.add(personal);
                }
            }
            idOpOrdem=0;
        }

        diaDisponivel = disponiveisDia;
        diaDisponibilidade = diaMesAno;
        disponivel = disponiveis;

        if(disponiveis){
            for(Map<String, String> personal: personalList ){
                if(personal.get("disponivel").equals("nao")){
                    personalList.remove(personal);
                }
            }
        }

        if(disponiveisDia){
            for(Map<String, String> personal: personalList ){
                String dias = personal.get("diasIndisponiveis");
                if(dias!=null && dias.contains(diaMesAno)){
                    personalList.remove(personal);
                }
            }
        }
        adapterTreinadores.notifyDataSetChanged();
    }
}
