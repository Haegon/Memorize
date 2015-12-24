package com.gohn.memorize.util;

import android.util.Log;

import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.enums.BuildType;

/**
 * Created by Gohn on 15. 12. 24..
 */
public class GLog {

    static String TAG = "VOCA_DIY";

    public static void Debug(String message) {
        if (CommonData.BUILD_TYPE == BuildType.DEBUG ) {
            Log.d(TAG,message);
        }
    }

    public static void Info(String message) {
        if (CommonData.BUILD_TYPE == BuildType.DEBUG ) {
            Log.i(TAG, message);
        }
    }

    public static void Warning(String message) {
        if (CommonData.BUILD_TYPE == BuildType.DEBUG ) {
            Log.w(TAG,message);
        }
    }

    public static void Error(String message) {
        if (CommonData.BUILD_TYPE == BuildType.DEBUG ) {
            Log.e(TAG, message);
        }
    }

    public static void Exception(Exception exception) {
        if (CommonData.BUILD_TYPE == BuildType.DEBUG ) {
            Log.e(TAG,exception.toString());
        }
    }

}
