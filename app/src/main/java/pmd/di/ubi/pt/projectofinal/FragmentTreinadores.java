package pmd.di.ubi.pt.projectofinal;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.material.tabs.TabLayout;
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
    private ArrayList<Map<String, Object>> personalList,personalListOriginal;
    private int idOpOrdem =0;

    private boolean disponivel = false;
    private boolean diaDisponivel = false;
    private String diaDisponibilidade = "Dia/Mes/Ano";
    private AdapterTreinadores adapterTreinadores;
    private FirebaseUser user;

    public FragmentTreinadores() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            modalidade = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gridview_fragment, container, false);
        gridView =  view.findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        personalList = new ArrayList<>();
        initPersonalList();
        return view;
    }

    private void initPersonalList(){

        personalList = new ArrayList<Map<String, Object>>();
        FirebaseFirestore.getInstance().collection("pessoas").
                whereEqualTo("tipoConta","personal").
                whereEqualTo("especialidade",modalidade).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                for (DocumentSnapshot document : task.getResult()) {
                    if(document!=null){
                        personalList.add(document.getData());
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
       menu.clear();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFinishOrdernarMenuDialog(int selectedItem) {

        switch (selectedItem){
            case R.id.radioButton_p_mema:
                personalList.sort(Comparator.comparing(m -> Float.parseFloat((String) m.get("preco"))));
                break;
            case R.id.radioButton_p_mame:
                personalList.sort(Comparator.comparing(m -> Float.parseFloat((String) m.get("preco"))));
                Collections.reverse(personalList);
                break;
            case R.id.radioButton_c_mema:
                personalList.sort(Comparator.comparing(m -> Integer.parseInt((String) m.get("rating"))));
                break;
            case R.id.radioButton_c_mame:
                personalList.sort(Comparator.comparing(m -> Integer.parseInt((String) m.get("rating"))));
                Collections.reverse(personalList);
                break;
        }
        idOpOrdem = selectedItem;
        adapterTreinadores.notifyDataSetChanged();
    }

    @Override
    public void onFinishFiltrarMenuDialog(boolean disponiveis, boolean disponiveisDia, String diaMesAno) {
        if(disponiveis!=disponivel || disponiveisDia!=diaDisponivel || !diaDisponibilidade.equals(diaMesAno)){
            for(Map<String, Object> personal: personalListOriginal ) {
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
            for(Map<String, Object> personal: personalList ){
               try {
                   if(personal.get("disponivel").equals("nao")){
                       personalList.remove(personal);
                   }
               }catch (Exception e){ }
            }
        }

        if(disponiveisDia){
            for(Map<String, Object> personal: personalList ){
                String dias = (String) personal.get("diasIndisponiveis");
                if(dias!=null && dias.contains(diaMesAno)){
                    personalList.remove(personal);
                }
            }
        }
        adapterTreinadores.notifyDataSetChanged();


    }
}
