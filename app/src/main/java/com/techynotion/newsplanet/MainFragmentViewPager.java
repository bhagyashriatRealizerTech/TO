package com.techynotion.newsplanet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dell on 12/27/2016.
 */
public class MainFragmentViewPager extends Fragment {

    ViewPager mViewPager;
    String title;
    SamplePagerAdapter samplePagerAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab_layout, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        title = getArguments().getString("Category");
        /** set the adapter for ViewPager */
        samplePagerAdapter = new SamplePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(samplePagerAdapter);

       mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

           }

           @Override
           public void onPageSelected(int position) {

            Bundle bundle = new Bundle();
            if(position == 0)
                bundle.putString("Category", title);
            else
                bundle.putString("Category", "Favorites");

            Singlton obj = Singlton.getInstance();

            if(obj.getResultReceiver() != null){
                obj.getResultReceiver().send(100,bundle);
            }
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });

        return rootView;
    }

    /** Defining a FragmentPagerAdapter class for controlling the fragments to be shown when user swipes on the screen. */
    public class SamplePagerAdapter extends FragmentStatePagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /** Show a Fragment based on the position of the current screen */
            Fragment fragment;
            if (position == 0) {
                fragment = new NewsListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Category", title);
                fragment.setArguments(bundle);

                return fragment;
            } else {
                fragment = new FavFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Category", "Favorites");
                fragment.setArguments(bundle);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 1;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return title;
            } else {
                return "Favorites";
            }
        }

    }
}
