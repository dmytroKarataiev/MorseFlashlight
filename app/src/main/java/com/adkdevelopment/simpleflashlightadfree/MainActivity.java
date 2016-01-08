package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private int status;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            status = savedInstanceState.getInt("status");

            if (status != 0) {
                Intent intent = new Intent(getApplication(), FlashlightService.class);
                intent.putExtra("status", status);
                getApplication().startService(intent);
            }
        } else {
            status = 0;
        }

        final ImageView button = (ImageView) findViewById(R.id.button_image);

        // check status and use correct image
        if (status == 0) {
            button.setImageResource(R.drawable.switch_on);
        } else {
            button.setImageResource(R.drawable.switch_off);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service on click
                Intent intent = new Intent(getApplication(), FlashlightService.class);

                if (status == 0) {
                    status = 1;
                    button.setImageResource(R.drawable.switch_off);
                } else {
                    status = 0;
                    button.setImageResource(R.drawable.switch_on);
                }
                intent.putExtra("status", status);
                getApplication().startService(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop service on application exit
        Intent intent = new Intent(getApplication(), FlashlightService.class);
        getApplication().stopService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save status on rotate, possibly will remove rotation in the future
        outState.putInt("status", status);
    }
}
