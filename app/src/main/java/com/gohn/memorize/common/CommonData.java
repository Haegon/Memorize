package com.gohn.memorize.common;

import android.content.Context;
import android.graphics.Bitmap;

import com.gohn.memorize.enums.BuildType;
import com.gohn.memorize.util.LightBitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gohn on 15. 11. 24..
 */
public class CommonData {
    private static Map<Integer,Bitmap> bitmapMap = new HashMap<>();

    public static BuildType BUILD_TYPE = BuildType.DEBUG;

    public static int REQUEST_CODE_FILE_ACTIVITY = 10001;

    public static int RESULT_REFRESH = 20001;

    public static String INTENT_KEY_PROBLEM_TYPE = "key_problem_type";
    public static String INTENT_KEY_DRAWER_ITEM = "key_drawer_item";

    public static String INTENT_VALUE_MULTIPLE = "value_multiple";
    public static String INTENT_VALUE_WRITE = "value_write";

    public static String GLOBAL_KEY_RANDOM = "global_key_random";

    public static void putBitmap(Context context, Integer key) {
        bitmapMap.put(key, LightBitmap.decodeBitmapFromResource(context.getResources(), key));
    }

    public static Bitmap getBitmap(Context context, Integer key) {
        if ( !bitmapMap.containsKey(key)) {
            putBitmap(context, key);
        }
        return bitmapMap.get(key);
    }

    public static boolean isDebugMode() {
        if ( BUILD_TYPE == BuildType.DEBUG ) return true;
        else return false;
    }

}
