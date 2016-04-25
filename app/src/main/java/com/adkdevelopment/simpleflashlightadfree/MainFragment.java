package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karataev on 2/22/16.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    private int status;

    @Bind(R.id.button_image) ImageView mButtonImage;
    @Bind(R.id.flashlight_mode) TextView mStatusText;

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

        ButterKnife.bind(this, rootView);

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
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save status on rotate, possibly will remove rotation in the future
        outState.putInt(FlashlightService.STATUS, status);
    }
}
