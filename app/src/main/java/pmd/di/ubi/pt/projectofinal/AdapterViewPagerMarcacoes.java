package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Map;

public class AdapterViewPagerMarcacoes extends FragmentStatePagerAdapter {


    ArrayList<Map<String, Object>> marcacoes;

    public AdapterViewPagerMarcacoes(Fragment fragment, ArrayList<Map<String, Object>> marcacoes) {

        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.marcacoes = marcacoes;

    }

    @Override
    public int getCount() {
        return marcacoes == null ? 0 : marcacoes.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);

        return FragmentDetalhesMarcacao.newInstance(bundle);
    }
}