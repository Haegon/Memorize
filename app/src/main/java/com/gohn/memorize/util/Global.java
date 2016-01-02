package com.gohn.memorize.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Gohn on 16. 1. 2..
 */
public class Global {

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key,defaultValue);
    }
    public static int getInt(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }
    public static float getFloat(Context context, String key, float defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(key, defaultValue);
    }
    public static long getLong(Context context, String key, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }
    public static String getString(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static boolean setBoolean(Context context, String key, boolean value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).commit();
    }
    public static boolean setInt(Context context, String key, int value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
    }
    public static boolean setFloat(Context context, String key, float value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(key, value).commit();
    }
    public static boolean setLong(Context context, String key, long value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).commit();
    }
    public static boolean setString(Context context, String key, String value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).commit();
    }

}
