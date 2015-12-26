package com.gohn.memorize.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import com.gohn.memorize.enums.BuildType;
import com.gohn.memorize.util.LightBitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gohn on 15. 11. 24..
 */
public class CommonData {
    private static Map<Integer,Bitmap> bitmapMap = new HashMap<>();
    private static Typeface typefaceBold;
    private static Typeface typefaceThin;

    public static BuildType BUILD_TYPE = BuildType.DEBUG;

    public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyDQ0tKdx9qoaI23O9pTv8eXNSCixVaXhiw";
    public static final int RECOVERY_DIALOG_REQUEST = 1;


    public static int REQUEST_CODE_FILE_ACTIVITY = 10001;

    public static String FONT_BOLD = "bd_font.mp3";
    public static String FONT_REGULAR = "noto.mp3";
    public static String DBKEY_USER = "db_user";
    public static String DBKEY_SCORE = "db_score";

    public static int RESULT_REFRESH = 20001;

    public static String INTENT_KEY_REFRESH = "intent_refresh";


    public static String PROBLEM_INFO = "problem_info";

    public static void putBitmap(Context context, Integer key) {
        bitmapMap.put(key, LightBitmap.decodeBitmapFromResource(context.getResources(), key));
    }

    public static Bitmap getBitmap(Context context, Integer key) {
        if ( !bitmapMap.containsKey(key)) {
            putBitmap(context, key);
        }
        return bitmapMap.get(key);
    }

    public static Typeface getTypefaceBold(Context context) {
        if ( typefaceBold == null ) {
            typefaceBold = Typeface.createFromAsset(context.getAssets(), CommonData.FONT_BOLD);
        }
        return typefaceBold;
    }

    public static Typeface getTypefaceRegular(Context context) {
        if ( typefaceThin == null ) {
            typefaceThin = Typeface.createFromAsset(context.getAssets(), CommonData.FONT_REGULAR);
        }
        return typefaceThin;
    }

    public static boolean isDebugMode() {
        if ( BUILD_TYPE == BuildType.DEBUG ) return true;
        else return false;
    }

}
