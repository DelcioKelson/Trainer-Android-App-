package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialFade;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentTodasMarcacoes extends Fragment implements View.OnClickListener, DialogConfirmarFragmentMarcacao.FlagMarcacaoConfirmadaDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    private TextView tvAceites;
    private TextView tvPendentes;
    private TextView tvPagas;
    private TextView tvHistorico;
    private FirebaseUser user;
    private FloatingActionButton btnNovaMarcacao;
    private boolean isUser;


    private PagerAdapter pagerAdapter;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todas_marcacoes, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);

        setExitTransition(MaterialFade.create(requireContext()));
        setEnterTransition(MaterialFade.create(requireContext()));

        try {
            isUser = modelData.isUser().getValue();
        }catch (Exception e){
        }

        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_time_24dp);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite);

        FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).collection("notificacoes").whereEqualTo("vista",false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult()!=null && task.getResult().size()>0){
                    ActivityMain.badge.setVisible(true);
                    ActivityMain.badge.setNumber(task.getResult().size());
                    Log.i("notificacoes",task.getResult().size()+"");
                }
            }
        });

        if (isUser ) {
            viewPager.setVisibility(View.VISIBLE);
            if( !user.isEmailVerified()){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                DialogFragment newFragment = DialogFragmentVerificarEmail.newInstance();
                newFragment.setTargetFragment(this, 300);
                newFragment.setCancelable(false);
                newFragment.show(ft, "dialog");
            }
        }

        tvAceites = view.findViewById(R.id.tv_marcacoes_aceites);
        tvPagas = view.findViewById(R.id.tv_marcacoes_pagas);
        tvPendentes = view.findViewById(R.id.tv_marcacaoes_pendentes);
        tvHistorico = view.findViewById(R.id.tv_historico);

        btnNovaMarcacao = view.findViewById(R.id.btn_nova_marcacao);
        if (!isUser) {
            btnNovaMarcacao.setVisibility(View.GONE);
        }
        btnNovaMarcacao.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragmentTodasMarcacoes_to_modalidadesFragment));

        tvAceites.setOnClickListener(this);
        tvHistorico.setOnClickListener(this);
        tvPagas.setOnClickListener(this);
        tvPendentes.setOnClickListener(this);
        numeroMarcacoes();
        return view;
    }

    public void numeroMarcacoes() {

        String auxTipoConta = isUser ? "uidUsuario" : "uidPersonal";

        FirebaseFirestore.getInstance().collection("marcacoes").whereEqualTo(auxTipoConta, user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                int numeroMarcacaosPedentes = 0;
                int numeroMarcacaosAceites = 0;
                int numeroMarcacaosPagas = 0;
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    if (document != null) {
                        if (document.get("estado").equals("pendente")) {
                            numeroMarcacaosPedentes = numeroMarcacaosPedentes + 1;
                        }
                        if (document.get("estado").equals("aceite")) {
                            numeroMarcacaosAceites = 1 + numeroMarcacaosAceites;
                        }
                        if (document.get("estado").equals("pagas")) {
                            numeroMarcacaosPagas = 1 + numeroMarcacaosPagas;
                        }
                    }
                    tvPendentes.setText("pendentes " + "(" + numeroMarcacaosPedentes + ")");
                    tvAceites.setText("aceites " + "(" + numeroMarcacaosAceites + ")");
                    tvPagas.setText("pagas " + "(" + numeroMarcacaosPagas + ")");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        bundle.putString("tipoConta", "usuario");
        switch (v.getId()) {
            case R.id.tv_marcacoes_aceites:
                bundle.putString("estado", "aceite");
                break;
            case R.id.tv_marcacaoes_pendentes:
                bundle.putString("estado", "pendente");
                break;
            case R.id.tv_marcacoes_pagas:
                bundle.putString("estado", "paga");
                break;
            case R.id.tv_historico:
                bundle.putString("estado", "default");
                break;
        }

        Navigation.findNavController(v).navigate(R.id.action_fragmentTodasMarcacoes_to_marcacoesFragment, bundle);
    }

    @Override
    public void onFinishEditDialog(int flag) {

    }

    public static class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new FragmentTreinadoresFavoritosRecentes();
            Bundle args = new Bundle();
            if(position==0){
                args.putString("tab","recente");
            }else {
                args.putString("tab","favorito");
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position==0){
                return "Recentes";
            }else {
                return "Favoritos";
            }
        }
    }
}
