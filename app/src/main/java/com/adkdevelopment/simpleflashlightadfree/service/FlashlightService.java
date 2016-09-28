/*
 * MIT License
 *
 * Copyright (c) 2016. Dmytro Karataiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.adkdevelopment.simpleflashlightadfree.service;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to keep Flashlight Active when the app is in background
 * Created by karataev on 1/5/16.
 */
public class FlashlightService extends Service {

    private final String LOG_TAG = FlashlightService.class.getSimpleName();

    public static final String STATUS = "status";
    public static final String MORSE = "morse";

    public static final int STATUS_OFF = 0;
    public static final int STATUS_TORCH = 1;
    public static final int STATUS_BLINK = 2;
    public static final int STATUS_MORSE = 3;

    private int status = -1;
    private String morseCode = "";

    // morse variables
    public static int dot = 150;
    private final int dash = 3;
    private final int space = 7;
    public static final int INCREASE = 1;
    public static final int DECREASE = 0;
    public static final int AMOUNT = 10;

    public static int getDot() {
        return dot;
    }

    public static void changeDot(int status) {
        if (status == INCREASE) {
            dot += AMOUNT;
        } else if (dot > AMOUNT) {
            dot -= AMOUNT;
        }
    }

    private Camera camera;
    private Camera.Parameters parameters;
    private FlashlightSwitch flashlightSwitch;
    private CameraManager manager;
    private String flashCameraId;

    public static final Map<String, int[]> sMorseDict;

    static {
        Map<String, int[]> morseCodesMap = new HashMap<>();
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
        sMorseDict = Collections.unmodifiableMap(morseCodesMap);
    }

    @TargetApi(Build.VERSION_CODES.M)
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

        status = intent.getIntExtra(STATUS, 1);

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

        return START_NOT_STICKY;
    }

    public class FlashlightSwitch extends AsyncTask<Integer, Void, Void> {

        public FlashlightSwitch() {
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Integer... params) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                flashCameraId = "0";
                try {
                    for (String camera : manager.getCameraIdList()) {
                        CameraCharacteristics ch = manager.getCameraCharacteristics(camera);
                        //noinspection ConstantConditions
                        if (ch.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) != null &&
                                ch.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
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
                                //noinspection InfiniteLoopStatement
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

            int[] sequence = sMorseDict.get(character);

            if (sequence == null) {
                return new int[]{0, 0, 0, 0, 0};
            } else {
                return sequence;
            }
        }
    }
}
