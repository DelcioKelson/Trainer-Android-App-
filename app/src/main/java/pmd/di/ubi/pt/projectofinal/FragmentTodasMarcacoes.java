package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;



public class FragmentTodasMarcacoes extends Fragment implements View.OnClickListener, DialogFragmentConfirmarMarcacao.FlagMarcacaoConfirmadaDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    private TextView tvAceites;
    private TextView tvPendentes;
    private TextView tvPagas;
    private TextView tvHistorico;
    private FirebaseUser user;
    private FloatingActionButton btnNovaMarcacao;
    private boolean isUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todas_marcacoes, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);

        isUser =modelData.isUser().getValue();


        tvAceites = view.findViewById(R.id.tv_marcacoes_aceites);
        tvPagas = view.findViewById(R.id.tv_marcacoes_pagas);
        tvPendentes = view.findViewById(R.id.tv_marcacaoes_pendentes);
        tvHistorico = view.findViewById(R.id.tv_historico);

        btnNovaMarcacao = view.findViewById(R.id.btn_nova_marcacao);

        if(!isUser){
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

    public void numeroMarcacoes(){

        FirebaseFirestore.getInstance().collection("marcacoes").whereEqualTo("uidUsuario",user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                int numeroMarcacaosPedentes = 0;
                int numeroMarcacaosAceites = 0;
                int numeroMarcacaosPagas = 0;
                for (DocumentSnapshot document : queryDocumentSnapshots){
                    if (document!=null){
                        if (document.get("estado").equals("pendente")){
                            numeroMarcacaosPedentes = numeroMarcacaosPedentes +1;
                        }
                        if (document.get("estado").equals("aceite")){
                            numeroMarcacaosAceites=1+ numeroMarcacaosAceites;
                        }
                        if (document.get("estado").equals("pagas")){
                            numeroMarcacaosPagas = 1+ numeroMarcacaosPagas;
                        }
                    }
                    tvPendentes.setText("Marcacoes pendentes " + "(" +numeroMarcacaosPedentes +")");
                    tvAceites.setText("Marcacoes aceites " + "(" +numeroMarcacaosAceites +")");
                    tvPagas.setText("Marcacoes pagas " + "(" +numeroMarcacaosPagas +")");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        bundle.putString("tipoConta","usuario");
        switch(v.getId()){
            case R.id.tv_marcacoes_aceites:
                bundle.putString("estado", "aceite");
                break;
            case R.id.tv_marcacaoes_pendentes:
                bundle.putString("estado", "pendente");
                break;
            case R.id.tv_marcacoes_pagas:
                bundle.putString("estado","paga");
                break;
            case R.id.tv_historico:
                bundle.putString("estado", "default");
                break;
        }

        Navigation.findNavController(v).navigate(R.id.action_fragmentTodasMarcacoes_to_marcacoesFragment,bundle);
    }

    @Override
    public void onFinishEditDialog(int flag) {

    }
}
