/*
 * MIT License
 *
 * Copyright (c) 2016. Dmytro Karataiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.adkdevelopment.simpleflashlightadfree.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.adkdevelopment.simpleflashlightadfree.ui.EmergencyFragment;
import com.adkdevelopment.simpleflashlightadfree.ui.MainFragment;
import com.adkdevelopment.simpleflashlightadfree.ui.MorseFragment;
import com.adkdevelopment.simpleflashlightadfree.ui.PagerFragment;
import com.adkdevelopment.simpleflashlightadfree.R;

/**
 * Since this is an object collection, use a FragmentStatePagerAdapter,
 * and NOT a FragmentPagerAdapter.
 * Created by karataev on 4/25/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private final PagerFragment pagerFragment;

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
