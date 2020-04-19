package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Map;

public class ViewPagerTreinadoresAdapter extends FragmentStatePagerAdapter {


    ArrayList<Map<String,Object>> personalList;
    FragmentManager fragmentManager;
    public ViewPagerTreinadoresAdapter(Fragment fragment) {

        // Note: Initialize with the child fragment manager.
        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fragment.getChildFragmentManager();

        try {
            SharedDataModel modelData = new ViewModelProvider(fragment.getActivity()).get(SharedDataModel.class);


            modelData.getPersonalList().observe(fragment.getActivity(), new Observer<ArrayList<Map<String, Object>>>() {
                @Override
                public void onChanged(ArrayList<Map<String, Object>> maps) {
                    personalList=maps;
                }
            });

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
        bundle.putByteArray("imageBytes", (byte[]) personalList.get(position).get("imageBytes"));
        bundle.putString("uidPersonal", (String) personalList.get(position).get("uid"));
        bundle.putInt("via",2);
        bundle.putString("nome", (String) personalList.get(position).get("nome"));
        bundle.putString("preco", (String) personalList.get(position).get("preco"));
        bundle.putString("rating", (String) personalList.get(position).get("rating"));
        return FragmentPersoanlPerfil.newInstance(bundle);
    }



}