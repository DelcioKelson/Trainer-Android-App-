package pmd.di.ubi.pt.projectofinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Map;

public class FragmentViewPagerMarcacoes extends DialogFragment {

    private ViewPager viewPager;

    public FragmentViewPagerMarcacoes() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_layout_marcacao, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);


        Button btnClose = view.findViewById(R.id.btn_close);

        btnClose.setOnClickListener(v -> dismiss());

        TabLayout tabLayout = (TabLayout) viewPager.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);

        try {
            ArrayList<Map<String, Object>> maps = Main.sharedDataModel.getMarcacoesList().getValue();
            viewPager.setAdapter(new AdapterViewPagerMarcacoes(FragmentViewPagerMarcacoes.this, maps));
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            viewPager.setCurrentItem(Main.currentPosition);
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    Main.currentPosition = position;
                }
            });

            Main.sharedDataModel.getFecharViewPager().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        Main.sharedDataModel.setFecharViewPager(false);
                        dismiss();
                    }
                }
            });

        } catch (Exception ignored) {
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        // set the animations to use on showing and hiding the dialog
        getDialog().getWindow().setWindowAnimations(
                R.style.MyAnimation);
        // alternative way of doing it
        //getDialog().getWindow().getAttributes().
        //    windowAnimations = R.style.dialog_animation_fade;

        // ... other stuff you want to do in your onStart() method
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }
}
