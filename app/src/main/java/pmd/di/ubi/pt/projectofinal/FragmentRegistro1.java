package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegistro1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistro1 extends Fragment {
    private CheckBox c1,c2,c3,c4,c5,c6;


    public static FragmentRegistro1 newInstance() {
        FragmentRegistro1 fragment = new FragmentRegistro1();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_registro1, container, false);

        c1 = view.findViewById(R.id.cb1);
        c2 = view.findViewById(R.id.cb2);
        c3 = view.findViewById(R.id.cb3);
        c4 = view.findViewById(R.id.cb4);
        c5 = view.findViewById(R.id.cb5);
        c6 = view.findViewById(R.id.cb6);
        Button btnProximoPasso = (Button) view.findViewById(R.id.btn_proximo_passo);


        btnProximoPasso.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_fragmentRegistro1_to_fragmentRegistro2)
);
        return view;
    }


    public String getDoencas(){
        String doencas = "";

        if(c1.isChecked()){
            doencas = c1.getText().toString();
        }
        if(c2.isChecked()){
            doencas = doencas +","+ c2.getText().toString();
        }
        if(c3.isChecked()){
            doencas = doencas +","+ c3.getText().toString();
        }
        if(c4.isChecked()){
            doencas = doencas +","+ c4.getText().toString();

        }
        if(c5.isChecked()){
            doencas = doencas +","+ c5.getText().toString();
        }
        if(c6.isChecked()){
            doencas = doencas +","+ c6.getText().toString();
        }
        return doencas;
    }
}
