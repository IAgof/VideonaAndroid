package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.New_Gallery_Fragment_Gallery;
import com.videonasocialmedia.videona.presentation.views.fragment.VideoGalleryFragment;

/**
 * Created by videona on 9/06/16.
 */
public class NewGalleryActivity extends VideonaActivity implements ViewPager.OnPageChangeListener {


    PagerAdapter adapterViewPager;
    int selectedPage = 0;

    boolean sharing = false;


    private final String MASTERS_FRAGMENT_TAG="MASTERS";
    private final String EDITED_FRAGMENT_TAG="EDITED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Masters"));
        tabLayout.addTab(tabLayout.newTab().setText("Editados"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapterViewPager = new PagerAdapter(getFragmentManager(), savedInstanceState);
        viewPager.setAdapter(adapterViewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        selectedPage = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class PagerAdapter extends android.support.v13.app.FragmentPagerAdapter{
        private final int NUM_ITEMS = 2;


        private New_Gallery_Fragment_Gallery fragmentoPrincipal;
        private New_Gallery_Fragment_Gallery fragmentoEditados;

        public PagerAdapter(FragmentManager fragmentManager, Bundle savedStateInstance) {
            super(fragmentManager);
            if (savedStateInstance==null) {
                createFragments();
            }else{
                restoreFragments(fragmentManager, savedStateInstance);
            }
        }

        private void createFragments(){
            fragmentoPrincipal = New_Gallery_Fragment_Gallery.newInstance("","");
            fragmentoEditados = New_Gallery_Fragment_Gallery.newInstance("","");
        }

        private void restoreFragments(FragmentManager fragmentManager, Bundle savedStateInstance) {

            fragmentoPrincipal = (New_Gallery_Fragment_Gallery) fragmentManager.getFragment(savedStateInstance, MASTERS_FRAGMENT_TAG) ;
            fragmentoEditados = (New_Gallery_Fragment_Gallery) fragmentManager.getFragment(savedStateInstance, EDITED_FRAGMENT_TAG) ;

        }

        @Override
        public New_Gallery_Fragment_Gallery getItem(int position) {
            New_Gallery_Fragment_Gallery result;
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    result = fragmentoPrincipal;
                    break;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    result = fragmentoEditados;
                    break;
                default:
                    result = null;
            }
            return result;



        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

}
