package com.adkdevelopment.simpleflashlightadfree;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
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
    private String morseCode = "";

    // morse variables
    private int dot = 150;
    private int dash = 450;
    private int space = 1050;

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

        //get morse code from intent
        morseCode = intent.getStringExtra("morse");
        if (morseCode == null) {
            morseCode = "";
        }

        // Cancel while loop to start another activity
        if (flashlightSwitch != null) {
            flashlightSwitch.cancel(true);
        }

        Log.v(LOG_TAG, "flash: " + morseCode + " status: " + status);
        flashlightSwitch = new FlashlightSwitch();
        flashlightSwitch.execute(status);

        return 0;
    }

    public class FlashlightSwitch extends AsyncTask<Integer, Void, Void> {

        public FlashlightSwitch() {
        }

        @Override
        protected Void doInBackground(Integer... params) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String flashCameraId = "0";
                try {
                    for (String camera : manager.getCameraIdList()) {
                        if (manager
                                .getCameraCharacteristics(camera)
                                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                            flashCameraId = camera;
                        }
                    }
                } catch (CameraAccessException e) {
                    Log.e(LOG_TAG, "CameraAccessException " + e);
                }

                Log.v(LOG_TAG, "case 3: " + morseCode + " status: " + status);

                try {
                    switch (status) {
                        case 0:
                            manager.setTorchMode(flashCameraId, false);
                            break;
                        case 1:
                            manager.setTorchMode(flashCameraId, true);
                            break;
                        case 2:
                            manager.setTorchMode(flashCameraId, true);

                            // Blinking will stop on service re-start.
                            try {
                                while (true) {
                                    Thread.sleep(100);
                                    manager.setTorchMode(flashCameraId, true);
                                    Thread.sleep(100);
                                    manager.setTorchMode(flashCameraId, false);
                                }
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, "Interrupted " + e);
                            }
                            break;
                        case 3:

                            try {
                                while (true) {
                                    for (int i = 0, n = morseCode.length(); i < n; i++) {
                                        if (morseCode.charAt(i) == ' ') {
                                            manager.setTorchMode(flashCameraId, false);
                                            Thread.sleep(space);
                                        } else if (isVowel(morseCode.charAt(i))) {
                                            manager.setTorchMode(flashCameraId, true);
                                            Thread.sleep(dash);
                                            manager.setTorchMode(flashCameraId, false);
                                        } else if (isConsanant(morseCode.charAt(i))) {
                                            manager.setTorchMode(flashCameraId, true);
                                            Thread.sleep(dot);
                                            manager.setTorchMode(flashCameraId, false);
                                        }

                                        Thread.sleep(space);
                                    }
                                }
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, "Interrupted " + e);
                            }
                            break;
                    }
                } catch (CameraAccessException e) {
                    Log.e(LOG_TAG, "Error " + e);
                }

            } else {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) && camera == null) {
                    Log.v(LOG_TAG, "Number of cameras: " + Camera.getNumberOfCameras());
                    camera = Camera.open();
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
                    case 3:

                        try {
                            while (true) {
                                for (int i = 0, n = morseCode.length(); i < n; i++) {
                                    if (camera != null) {
                                        if (morseCode.charAt(i) == ' ') {

                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                            camera.setParameters(parameters);
                                            Thread.sleep(space);

                                        } else if (isVowel(morseCode.charAt(i))) {
                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                            camera.setParameters(parameters);
                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                            camera.setParameters(parameters);
                                            Thread.sleep(dash);
                                        } else if (isConsanant(morseCode.charAt(i))) {
                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                            camera.setParameters(parameters);
                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                            camera.setParameters(parameters);
                                            Thread.sleep(dot);
                                        }
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            Log.e(LOG_TAG, "Interrupted " + e);
                        }
                        break;
                }
            }

            return null;
        }

    }

    private boolean isVowel(char c) {
        return "AEIOUaeiou".indexOf(c) != -1;
    }

    public boolean isConsanant(char c) {
        return "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ".indexOf(c) != -1;
    }
}
