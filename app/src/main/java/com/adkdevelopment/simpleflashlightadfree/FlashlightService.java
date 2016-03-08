package com.adkdevelopment.simpleflashlightadfree;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

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
    private int dash = 3;
    private int space = 7;

    private Camera camera;
    private Camera.Parameters parameters;
    FlashlightSwitch flashlightSwitch;
    CameraManager manager;
    String flashCameraId;

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

        if (manager != null) {
            try {
                manager.setTorchMode(flashCameraId, false);
            } catch (CameraAccessException e) {
                Log.e(LOG_TAG, "Error: " + e);
            }
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

        flashlightSwitch = new FlashlightSwitch();
        flashlightSwitch.execute(status);

        return 0;
    }

    public class FlashlightSwitch extends AsyncTask<Integer, Void, Void> {

        HashMap<String, int[]> morseCodesMap;

        public FlashlightSwitch() {

            // Morse HashMap to retrieve sequences in O(1)

            morseCodesMap = new HashMap<>();
            morseCodesMap.put("A", new int[]{1, 3, 0, 0, 0});
            morseCodesMap.put("B", new int[]{3, 1, 1, 1, 0});
            morseCodesMap.put("C", new int[]{3, 1, 3, 1, 0});
            morseCodesMap.put("D", new int[]{3, 1, 1, 0, 0});
            morseCodesMap.put("E", new int[]{1, 0, 0, 0, 0});
            morseCodesMap.put("F", new int[]{1, 1, 3, 1, 0});
            morseCodesMap.put("G", new int[]{3, 3, 1, 0, 0});
            morseCodesMap.put("H", new int[]{1, 1, 1, 1, 0});
            morseCodesMap.put("I", new int[]{1, 1, 0, 0, 0});
            morseCodesMap.put("J", new int[]{1, 3, 3, 3, 0});
            morseCodesMap.put("K", new int[]{3, 1, 3, 0, 0});
            morseCodesMap.put("L", new int[]{1, 3, 1, 1, 0});
            morseCodesMap.put("M", new int[]{3, 3, 0, 0, 0});
            morseCodesMap.put("N", new int[]{3, 1, 0, 0, 0});
            morseCodesMap.put("O", new int[]{3, 3, 3, 0, 0});
            morseCodesMap.put("P", new int[]{1, 3, 3, 1, 0});
            morseCodesMap.put("Q", new int[]{3, 3, 1, 3, 0});
            morseCodesMap.put("R", new int[]{1, 3, 1, 0, 0});
            morseCodesMap.put("S", new int[]{1, 1, 1, 0, 0});
            morseCodesMap.put("T", new int[]{3, 0, 0, 0, 0});
            morseCodesMap.put("U", new int[]{1, 1, 3, 0, 0});
            morseCodesMap.put("V", new int[]{1, 1, 1, 3, 0});
            morseCodesMap.put("W", new int[]{1, 3, 3, 0, 0});
            morseCodesMap.put("X", new int[]{3, 1, 1, 3, 0});
            morseCodesMap.put("Y", new int[]{3, 1, 3, 3, 0});
            morseCodesMap.put("Z", new int[]{3, 3, 1, 1, 0});
            morseCodesMap.put("1", new int[]{1, 3, 3, 3, 3});
            morseCodesMap.put("2", new int[]{1, 1, 3, 3, 3});
            morseCodesMap.put("3", new int[]{1, 1, 1, 3, 3});
            morseCodesMap.put("4", new int[]{1, 1, 1, 1, 3});
            morseCodesMap.put("5", new int[]{1, 1, 1, 1, 1});
            morseCodesMap.put("6", new int[]{3, 1, 1, 1, 1});
            morseCodesMap.put("7", new int[]{3, 3, 1, 1, 1});
            morseCodesMap.put("8", new int[]{3, 3, 3, 1, 1});
            morseCodesMap.put("9", new int[]{3, 3, 3, 3, 1});
            morseCodesMap.put("0", new int[]{3, 3, 3, 3, 3});
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Integer... params) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                flashCameraId = "0";
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
                                    Thread.sleep(dot);
                                    manager.setTorchMode(flashCameraId, true);
                                    Thread.sleep(dot);
                                    manager.setTorchMode(flashCameraId, false);
                                }
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, "Interrupted " + e);
                            }
                            break;
                        case 3:

                            try {
                                manager.setTorchMode(flashCameraId, false);
                                while (true) {
                                    for (int i = 0, n = morseCode.length(); i < n; i++) {
                                        if (morseCode.charAt(i) == ' ') {
                                            manager.setTorchMode(flashCameraId, false);
                                            Thread.sleep(dot * space);
                                        } else {
                                            int[] sequence = getMorseSequence(Character.toString(morseCode.charAt(i)).toUpperCase());
                                            for (int k = 0; k < 5; k++) {
                                                if (sequence[k] != 0) {
                                                    manager.setTorchMode(flashCameraId, true);
                                                    Thread.sleep(dot * sequence[k]);
                                                    manager.setTorchMode(flashCameraId, false);
                                                    Thread.sleep(dot);
                                                }
                                            }
                                        }

                                        Thread.sleep(dot * dash);
                                    }
                                    Thread.sleep(dot * space);
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
                    camera = Camera.open(0);
                    parameters = camera.getParameters();
                }

                try {
                    SurfaceTexture dummy = new SurfaceTexture(1);
                    camera.setPreviewTexture(dummy);

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
                                        Thread.sleep(dot);
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                        camera.setParameters(parameters);
                                        Thread.sleep(dot);
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                        camera.setParameters(parameters);
                                    }
                                }

                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, "Interrupted " + e);
                            }
                            break;
                        case 3:
                            if (morseCode.length() > 0) {
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            } else {
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            }
                            camera.setParameters(parameters);
                            camera.startPreview();

                            try {
                                while (true) {
                                    for (int i = 0, n = morseCode.length(); i < n; i++) {
                                        if (morseCode.charAt(i) == ' ') {
                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                            camera.setParameters(parameters);
                                            Thread.sleep(dot * space);
                                        } else {
                                            int[] sequence = getMorseSequence(Character.toString(morseCode.charAt(i)).toUpperCase());
                                            for (int k = 0; k < 5; k++) {
                                                if (sequence[k] != 0) {
                                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                                    camera.setParameters(parameters);
                                                    Thread.sleep(dot * sequence[k]);
                                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                                    camera.setParameters(parameters);
                                                    Thread.sleep(dot);
                                                }
                                            }
                                        }
                                        Thread.sleep(dot * dash);
                                    }
                                    Thread.sleep(dot * space);
                                }
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, "Interrupted " + e);
                            }
                            break;
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error " + e);
                }

            }

            return null;
        }

        /**
         * Method to return int[] with sequence of morse code for each latin letter or number
         *
         * @param character to get the sequence for
         * @return int array with the sequence
         */
        private int[] getMorseSequence(String character) {

            int[] sequence = morseCodesMap.get(character);

            if (sequence == null) {
                return new int[]{0, 0, 0, 0, 0};
            } else {
                return sequence;
            }

        }


    }


}
