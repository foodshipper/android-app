package de.foodshippers.foodship;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * Created by soenke on 19.11.16.
 */
public class Utils {

    /**
     * Source: https://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences#18098090
     */
    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static String getUserId(Context c) {
        return Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
