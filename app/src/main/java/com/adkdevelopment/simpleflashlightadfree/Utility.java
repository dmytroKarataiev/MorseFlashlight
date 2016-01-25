package com.adkdevelopment.simpleflashlightadfree;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

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
}
