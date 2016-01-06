package com.adkdevelopment.simpleflashlightadfree;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Service to keep Flashlight Active when the app is in background
 * Created by karataev on 1/5/16.
 */
public class FlashlightService extends Service {

    private final String LOG_TAG = FlashlightService.class.getSimpleName();

    private int status = -1;
    private Camera camera;
    private Camera.Parameters parameters;

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(LOG_TAG, "released");

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.v(LOG_TAG, "started");

        if (camera == null) {
            camera = Camera.open();
            parameters = camera.getParameters();
        }

        status = (status + 1) % 2;

        switch (status) {
            case 0:
                parameters.setFlashMode("torch");
                camera.setParameters(parameters);
                camera.startPreview();
                break;
            case 1:
                parameters.setFlashMode("off");
                camera.setParameters(parameters);
                camera.stopPreview();
                break;
        }

        return 0;
    }
}