package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Map;

public class AdapterViewPagerTreinadores extends FragmentStatePagerAdapter {


    private ArrayList<Map<String,Object>> personalList;
    public AdapterViewPagerTreinadores(Fragment fragment) {

        // Note: Initialize with the child fragment manager.
        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        try {

            personalList = Main.sharedDataModel.getPersonalList().getValue();
        }catch (Exception e){

        }


    }

    @Override
    public int getCount() {
        return personalList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("via",2);
        bundle.putInt("posPersonal",position);

        return FragmentPersoanlPerfil.newInstance(bundle);
    }



}