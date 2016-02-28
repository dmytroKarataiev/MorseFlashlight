package com.adkdevelopment.simpleflashlightadfree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by karataev on 2/22/16.
 */
public class PagerFragment extends Fragment {

    //pager
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getChildFragmentManager());

        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setCurrentItem(MainActivity.current_fragment);

        return rootView;
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            if (i == 0) {
                MorseFragment morseFragment = new MorseFragment();
                return morseFragment;
            }

            MainFragment fragment = new MainFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(MainFragment.ARG_OBJECT, i + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getActivity().getString(R.string.morse_screen);
            } else if (position == 1) {
                return getActivity().getString(R.string.flashlight_screen);
            } else {
                return getActivity().getString(R.string.emergency_screen);
            }
        }
    }
}