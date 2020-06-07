package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;


public class FragmentSelecionarTipoConta extends Fragment {

    public FragmentSelecionarTipoConta() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_selecionar_tipo_conta, container, false);

        MaterialCardView cliente, personal;

        view.findViewById(R.id.btn_voltar).setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();

        });

        cliente = view.findViewById(R.id.op_cliente);
        personal = view.findViewById(R.id.op_personal);

        cliente.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragmentSelecionarTipoConta_to_fragmentRegistro1));

        personal.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragmentSelecionarTipoConta_to_fragmentRegistroPersonal1));

        return view;
    }
}
