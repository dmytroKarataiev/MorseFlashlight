package com.adkdevelopment.simpleflashlightadfree;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by karataev on 1/24/16.
 */
public class Utility {

    /**
     * Method to get orientation status and return boolean value: true if locked, false if not
     * @param context from which call is being made
     * @return boolean value, true if locked orientation
     */
    public static boolean getOrientationStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_orientation_key), true);

    }

    /**
     * Sets orientation in the activity from which call was made
     * @param context from which call is being made
     */
    public static void setOrientation(Activity context) {
        int status = Utility.getOrientationStatus(context) ? 1 : 0;

        switch (status) {
            case 1:
                // No rotation
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                break;
            case 0:
                // Rotation is enabled
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
        }
    }

    /**
     * Method to get dots and dashes from a String
     * @param message textual String
     * @return dots and dashes String
     */
    public static String getMorseMessage(String message) {

        HashMap<String, int[]> morseCodesMap = new HashMap<>();
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

        StringBuilder output = new StringBuilder();

        if (message.length() == 0) {
            return "";
        }

        for (int i = 0; i < message.length(); i++) {
            int[] sequence = morseCodesMap.get(Character.toString(message.charAt(i)).toUpperCase());

            for (int k = 0; k < 5; k++) {
                if (sequence == null) {
                    output.append(" ");
                } else if (sequence[k] == 1) {
                    output.append(". ");
                } else if (sequence[k] == 3) {
                    output.append("— ");
                }
            }
            output.append("  ");
        }

        return output.toString();
    }

    /**
     * Method to set flashlight button drawable
     *
     * @param button to switch flashlight
     * @param status flashlight mode
     */
    public static void setSwitchColor(TextView mode, ImageView button, int status) {
        switch (status) {
            case FlashlightService.STATUS_OFF:
                button.setImageResource(R.drawable.switch_on);
                mode.setText(R.string.flashlight_status_on);
                break;
            case FlashlightService.STATUS_TORCH:
                button.setImageResource(R.drawable.switch_blink);
                mode.setText(R.string.flashlight_status_blink);
                break;
            case FlashlightService.STATUS_BLINK:
            case FlashlightService.STATUS_MORSE:
                button.setImageResource(R.drawable.switch_off);
                mode.setText(R.string.flashlight_status_off);
                break;
        }
    }
}
