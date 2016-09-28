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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
public class MorseFragment extends android.support.v4.app.Fragment {

    private int status;
    private String morseCode, beforeChangeMorse;

    @BindView(R.id.button_image) ImageView mButtonImage;
    @BindView(R.id.flashlight_mode) TextView mStatusText;
    @BindView(R.id.morse_current_text) TextView mCurrentText;
    @BindView(R.id.edittext_morse) EditText mMorseInput;
    @BindView(R.id.button_increase) ImageView mImageIncrease;
    @BindView(R.id.button_decrease) ImageView mImageDecrease;
    @BindView(R.id.morse_speed) TextView mTextSpeed;
    private Unbinder mUnbinder;

    public MorseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            status = savedInstanceState.getInt(FlashlightService.STATUS);

            if (status != 0) {
                Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);
                intent.putExtra(FlashlightService.STATUS, status);

                // add morse code
                intent.putExtra(FlashlightService.MORSE, morseCode);

                getActivity().getApplication().startService(intent);
            }
        } else {
            status = 0;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_morse, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        // check status and use correct image
        Utility.setSwitchColor(mStatusText, mButtonImage, status);

        mButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service on click
                if (status == FlashlightService.STATUS_OFF) {
                    status = FlashlightService.STATUS_MORSE;
                } else {
                    status = FlashlightService.STATUS_OFF;
                }

                // Set button drawable
                Utility.setSwitchColor(mStatusText, mButtonImage, status);

                startService();
            }
        });

        mMorseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeChangeMorse = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                morseCode = s.toString();
                mCurrentText.setText(Utility.getMorseMessage(morseCode));

                if (s.length() > 0 || beforeChangeMorse.length() > 0) {
                    startService();
                }
            }
        });

        mImageIncrease.setOnClickListener(mSpeedListener);
        mImageDecrease.setOnClickListener(mSpeedListener);
        mTextSpeed.setText(getString(R.string.morse_speed, FlashlightService.getDot()));

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop service on application exit
        Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);
        getActivity().getApplication().stopService(intent);
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

    private void startService() {
        Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);

        intent.putExtra(FlashlightService.STATUS, status);

        // add morse code
        intent.putExtra(FlashlightService.MORSE, morseCode);

        getActivity().getApplication().startService(intent);
    }

    final View.OnClickListener mSpeedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == mImageIncrease.getId()) {
                FlashlightService.changeDot(FlashlightService.INCREASE);
            } else {
                FlashlightService.changeDot(FlashlightService.DECREASE);
            }
            mTextSpeed.setText(getString(R.string.morse_speed, FlashlightService.getDot()));
            startService();
        }
    };

}
