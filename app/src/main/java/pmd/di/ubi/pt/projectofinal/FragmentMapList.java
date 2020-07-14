package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMapList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMapList extends Fragment {

    public FragmentMapList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map_list, container, false);


        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.pager_gym);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    public static class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == 0) {
                fragment = new FragmentMapGyms();
            } else {
                fragment = new FragmentListaGyms();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Mapa";
            } else {
                return "Lista";
            }
        }
    }
}
