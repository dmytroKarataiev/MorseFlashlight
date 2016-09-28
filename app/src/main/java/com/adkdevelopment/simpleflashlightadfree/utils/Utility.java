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

package com.adkdevelopment.simpleflashlightadfree.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.adkdevelopment.simpleflashlightadfree.R;
import com.adkdevelopment.simpleflashlightadfree.service.FlashlightService;

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

        StringBuilder output = new StringBuilder();

        if (message.length() == 0) {
            return "";
        }

        for (int i = 0; i < message.length(); i++) {
            int[] sequence = FlashlightService.sMorseDict
                    .get(Character.toString(message.charAt(i)).toUpperCase());

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
                button.setImageResource(R.drawable.ic_switch_on);
                mode.setText(R.string.flashlight_status_on);
                break;
            case FlashlightService.STATUS_TORCH:
                button.setImageResource(R.drawable.ic_switch_blink);
                mode.setText(R.string.flashlight_status_blink);
                break;
            case FlashlightService.STATUS_BLINK:
            case FlashlightService.STATUS_MORSE:
                button.setImageResource(R.drawable.ic_switch_off);
                mode.setText(R.string.flashlight_status_off);
                break;
        }
    }
}
