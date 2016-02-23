package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by karataev on 2/22/16.
 */
public class MorseFragment extends android.support.v4.app.Fragment {

    private int status;
    private boolean torch;

    public static final String ARG_OBJECT = "object";


    public MorseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            status = savedInstanceState.getInt("status");

            if (status != 0) {
                Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);
                intent.putExtra("status", status);
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

        final ImageView button = (ImageView) rootView.findViewById(R.id.button_image);
        final TextView statusText = (TextView) rootView.findViewById(R.id.flashlight_mode);

        // check status and use correct image
        setSwitchColor(statusText, button, status);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service on click
                status = (status + 1) % 3;

                Intent intent = new Intent(getActivity().getApplication(), FlashlightService.class);

                // Set button drawable
                setSwitchColor(statusText, button, status);

                intent.putExtra("status", status);
                getActivity().getApplication().startService(intent);
            }
        });

        Bundle args = getArguments();
//        statusText.setText(Integer.toString(args.getInt(ARG_OBJECT)));

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save status on rotate, possibly will remove rotation in the future
        outState.putInt("status", status);
    }

    /**
     * Method to set flashlight button drawable
     *
     * @param button to switch flashlight
     * @param status flashlight mode
     */
    private void setSwitchColor(TextView mode, ImageView button, int status) {
        switch (status) {
            case 0:
                button.setImageResource(R.drawable.switch_on);
                mode.setText(R.string.flashlight_status_on);
                break;
            case 1:
                button.setImageResource(R.drawable.switch_blink);
                mode.setText(R.string.flashlight_status_blink);
                break;
            case 2:
                button.setImageResource(R.drawable.switch_off);
                mode.setText(R.string.flashlight_status_off);
                break;
        }
    }
}
