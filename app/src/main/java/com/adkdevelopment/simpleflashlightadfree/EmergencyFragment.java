package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karataev on 2/22/16.
 */
public class EmergencyFragment extends android.support.v4.app.Fragment {

    private static final String TAG = EmergencyFragment.class.getSimpleName();

    private int status;
    MediaPlayer mMediaPlayer;

    @Bind(R.id.button_image) ImageView mButtonImage;
    @Bind(R.id.flashlight_mode) TextView mStatusText;
    @Bind(R.id.layout) LinearLayout mLinearLayout;

    public EmergencyFragment() {}

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
                if (status == FlashlightService.STATUS_OFF) {
                    status = FlashlightService.STATUS_BLINK;
                } else {
                    status = FlashlightService.STATUS_OFF;
                }

                emergencySignal();

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

    /**
     * Starts emergency sound from assets
     */
    public void emergencySignal() {

        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying() && status == FlashlightService.STATUS_OFF) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;

                mLinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
            } else if (status == FlashlightService.STATUS_BLINK) {

                final AnimationDrawable drawable = new AnimationDrawable();
                final Handler handler = new Handler();

                drawable.addFrame(new ColorDrawable(Color.RED), 400);
                drawable.addFrame(new ColorDrawable(Color.BLUE), 400);
                drawable.setOneShot(false);

                mLinearLayout.setBackground(drawable);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawable.start();
                    }
                }, 100);

                mMediaPlayer = new MediaPlayer();
                AssetFileDescriptor descriptor = getActivity().getAssets().openFd("sews.mp3");
                mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();

                mMediaPlayer.prepare();
                mMediaPlayer.setVolume(1f, 1f);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
