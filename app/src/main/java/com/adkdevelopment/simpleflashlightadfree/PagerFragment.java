package com.adkdevelopment.simpleflashlightadfree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adkdevelopment.simpleflashlightadfree.adapters.PagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karataev on 2/22/16.
 */
public class PagerFragment extends Fragment {

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    PagerAdapter mPagerAdapter;
    @Bind(R.id.pager) ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);

        ButterKnife.bind(this, rootView);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mPagerAdapter = new PagerAdapter(this, getChildFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(MainActivity.current_fragment);

        return rootView;
    }
}
