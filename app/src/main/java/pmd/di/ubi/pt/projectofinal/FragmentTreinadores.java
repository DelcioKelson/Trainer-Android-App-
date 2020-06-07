package pmd.di.ubi.pt.projectofinal;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FragmentTreinadores extends Fragment implements DialogFragmentOrdernarFiltrar.DialogFragmentOrdernarMenuDialogListener, DialogFragmentOrdernarFiltrar.DialogFragmentFiltrarMenuDialogListener {

    private static final String ARG_PARAM1 = "modalidade";

    // TODO: Rename and change types of parameters
    private String modalidade;
    private RecyclerView recyclerView;

    private AdapterPersonalTreiners adapterPersonalTreiners;
    private ArrayList<Map<String, Object>> personalList,personalListOriginal;
    private int idOpOrdem =0;

    private boolean disponivel = false;
    private boolean diaDisponivel = false;
    private String diaDisponibilidade = "Dia/Mes/Ano";
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }

    private void scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(Main.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(Main.currentPosition));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        personalList = new ArrayList<>();
        setExitTransition(new Hold());

        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = recyclerView
                                .findViewHolderForAdapterPosition(Main.currentPosition);
                        if (selectedViewHolder == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.personal_image));
                    }
                });

        initPersonalList();
        return view;
    }

    private void initPersonalList(){

        personalList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("pessoas").
                whereEqualTo("tipoConta", "personal").
                whereEqualTo("modalidades." + modalidade, true).get().addOnSuccessListener(task -> {
            for (DocumentSnapshot document : task) {
                if (document != null) {
                    personalList.add(document.getData());
                }
            }
            Main.sharedDataModel.addPersonalList(personalList);
            personalListOriginal = new ArrayList<>(personalList);
            adapterPersonalTreiners = new AdapterPersonalTreiners(this, personalList);
            recyclerView.setAdapter(adapterPersonalTreiners);


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
        adapterPersonalTreiners.notifyDataSetChanged();
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
        adapterPersonalTreiners.notifyDataSetChanged();

    }
}
