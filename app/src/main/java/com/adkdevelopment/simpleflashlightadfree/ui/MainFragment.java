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

package com.adkdevelopment.simpleflashlightadfree.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adkdevelopment.simpleflashlightadfree.service.FlashlightService;
import com.adkdevelopment.simpleflashlightadfree.R;
import com.adkdevelopment.simpleflashlightadfree.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by karataev on 2/22/16.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    private int status;

    @BindView(R.id.button_image) ImageView mButtonImage;
    @BindView(R.id.flashlight_mode) TextView mStatusText;
    private Unbinder mUnbinder;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            status = savedInstanceState.getInt(FlashlightService.STATUS);

            if (status != FlashlightService.STATUS_OFF) {
                Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);
                intent.putExtra(FlashlightService.STATUS, status);
                getActivity().getApplication().startService(intent);
            }
        } else {
            status = FlashlightService.STATUS_OFF;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        // check status and use correct image
        Utility.setSwitchColor(mStatusText, mButtonImage, status);

        mButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service on click
                status = (status + 1) % 3;

                Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);

                // Set button drawable
                Utility.setSwitchColor(mStatusText, mButtonImage, status);

                intent.putExtra(FlashlightService.STATUS, status);
                getActivity().getApplication().startService(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save status on rotate, possibly will remove rotation in the future
        outState.putInt(FlashlightService.STATUS, status);
    }
}
