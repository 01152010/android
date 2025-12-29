package com.core.utils;

import android.util.Log;

public class LogUtil {
    private static final String Tag = "andy.wang-core";
    /** 是否开启debug模式 */
//    public static boolean isDebug = BuildConfig.DEBUG;
    public static boolean isDebug = true;
    public static void Log(String msg) {
        if(isDebug){
            Log.i(Tag, msg);
        }
    }

    public static void Dbug(boolean isDbug,String msg) {
        if(isDbug){

        }
    }
}
