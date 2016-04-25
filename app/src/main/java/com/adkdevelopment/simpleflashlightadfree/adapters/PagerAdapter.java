package com.adkdevelopment.simpleflashlightadfree.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.adkdevelopment.simpleflashlightadfree.EmergencyFragment;
import com.adkdevelopment.simpleflashlightadfree.MainFragment;
import com.adkdevelopment.simpleflashlightadfree.MorseFragment;
import com.adkdevelopment.simpleflashlightadfree.PagerFragment;
import com.adkdevelopment.simpleflashlightadfree.R;

/**
 * Since this is an object collection, use a FragmentStatePagerAdapter,
 * and NOT a FragmentPagerAdapter.
 * Created by karataev on 4/25/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private PagerFragment pagerFragment;

    public PagerAdapter(PagerFragment pagerFragment, FragmentManager fm) {
        super(fm);
        this.pagerFragment = pagerFragment;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new MorseFragment();
            case 1:
                return new MainFragment();
            case 2:
                return new EmergencyFragment();
            default:
                return new MainFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return pagerFragment.getActivity().getString(R.string.morse_screen);
        } else if (position == 1) {
            return pagerFragment.getActivity().getString(R.string.flashlight_screen);
        } else {
            return pagerFragment.getActivity().getString(R.string.emergency_screen);
        }
    }
}
