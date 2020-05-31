package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLugarTreino#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLugarTreino extends Fragment {

    public FragmentLugarTreino() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lugar_treino, container, false);

        MaterialCardView casa, gym;


        casa = view.findViewById(R.id.op_casa);
        gym = view.findViewById(R.id.op_gym);


        casa.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragmentLugarTreino_to_modalidadesFragment));


        gym.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragmentLugarTreino_to_fragmentMapList));

        return view;
    }
}
