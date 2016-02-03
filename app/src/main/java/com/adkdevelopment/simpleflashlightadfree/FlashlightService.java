package com.adkdevelopment.simpleflashlightadfree;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
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
    FlashlightSwitch flashlightSwitch;

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(LOG_TAG, "released");
        // Cancel while loop to start another activity
        if (flashlightSwitch != null) {
            flashlightSwitch.cancel(true);
        }

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

        status = intent.getIntExtra("status", 1);

        // Cancel while loop to start another activity
        if (flashlightSwitch != null) {
            flashlightSwitch.cancel(true);
        }

        flashlightSwitch = new FlashlightSwitch();
        flashlightSwitch.execute(status);

        return 0;
    }

    public class FlashlightSwitch extends AsyncTask<Integer, Void, Void> {

        public FlashlightSwitch() {}

        @Override
        protected Void doInBackground(Integer... params) {

            if (camera == null) {
                Log.v(LOG_TAG, "Number of cameras: " + Camera.getNumberOfCameras());
                camera = Camera.open(0);
                parameters = camera.getParameters();
            }

            switch (status) {
                case 0:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    break;
                case 1:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                    break;
                case 2:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();

                    // Blinking will stop on service re-start.
                    try {
                        while (true) {
                            if (camera != null) {
                                Thread.sleep(100);
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(parameters);
                                Thread.sleep(100);
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(parameters);
                            }
                        }

                    } catch (InterruptedException e) {
                        Log.e(LOG_TAG, "Interrupted " + e);
                    }
                    break;
            }

            return null;
        }

    }
}
