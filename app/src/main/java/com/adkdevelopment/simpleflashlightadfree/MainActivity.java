package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button flaslightSwitch = (Button) findViewById(R.id.flashlight_switch);
        flaslightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start service on click
                Intent intent = new Intent(getApplication(), FlashlightService.class);
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
}
