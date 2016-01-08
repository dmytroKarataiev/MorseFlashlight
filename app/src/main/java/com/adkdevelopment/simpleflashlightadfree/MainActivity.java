package com.adkdevelopment.simpleflashlightadfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private int status = 0;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView button = (ImageView) findViewById(R.id.button_image);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start service on click
                Intent intent = new Intent(getApplication(), FlashlightService.class);

                if (status == 0) {
                    status = 1;
                    intent.putExtra("status", status);
                    button.setImageResource(R.drawable.switch_on);
                } else {
                    status = 0;
                    intent.putExtra("status", status);
                    button.setImageResource(R.drawable.switch_off);
                }
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
