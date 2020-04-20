package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Map;

public class AdapterViewPagerMarcacoes extends FragmentStatePagerAdapter {


    ArrayList<Map<String,Object>> marcacoes;
    public AdapterViewPagerMarcacoes(Fragment fragment) {

        // Note: Initialize with the child fragment manager.
        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        try {
            SharedDataModel modelData = new ViewModelProvider(fragment.getActivity()).get(SharedDataModel.class);

            modelData.getMarcacoesList().observe(fragment.getActivity(), new Observer<ArrayList<Map<String, Object>>>() {
                @Override
                public void onChanged(ArrayList<Map<String, Object>> maps) {
                    marcacoes=maps;
                }
            });

        }catch (Exception e){

        }
    }

    @Override
    public int getCount() {
        return marcacoes==null?0: marcacoes.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("estado", (String) marcacoes.get(position).get("estado"));
        bundle.putString("preco", (String) marcacoes.get(position).get("preco"));
        bundle.putString("horaTreino", (String) marcacoes.get(position).get("horaTreino"));
        bundle.putString("diaTreino", (String) marcacoes.get(position).get("diaTreino"));
        bundle.putString("tempoDuracao", (String) marcacoes.get(position).get("tempoDuracao"));
        return FragmentDetalhesMarcacao.newInstance(bundle);
    }
}