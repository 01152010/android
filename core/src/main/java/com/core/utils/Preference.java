package com.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private static final String ONCE = "ONCE";
    private static final String FOREVER = "FOREVER";

    public static final String PreferenceKeyAccessToken = "preference_key_access_token";
    public static final String PreferenceKeyAddress = "preference_key_address";
    public static final String PreferenceKeyDeviceNo = "preference_key_deviceNo";
    public static final String PreferenceKeyHotelId = "preference_key_HotelId";
    public static final String PreferenceKeyRoomId = "preference_key_RoomId";
    public static final String PreferenceKeyRoomNo = "preference_key_RoomNo";
    public static final String PreferenceKeySMScene = "preference_key_SMScene";
    public static final String PreferenceKeyVoiceOut = "VoiceOut";
    public static final String PreferenceKeyCoreVersion = "preference_key_CoreVersion";
    public static final String PreferenceKeyWakeupConfig = "preference_key_Wakeup_Config";

    public static void clearPreference(Context context){
        SharedPreferences preferences = context.getSharedPreferences(ONCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void setString(Context context,boolean isForever,String key,String value){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getString(Context context,boolean isForever,String key,String defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE,Activity.MODE_PRIVATE);
        return preferences.getString(key,defaultValue);
    }


    public static void setBoolean(Context context,boolean isForever, String key, boolean value){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static Boolean getBoolean(Context context,boolean isForever,String key,boolean defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE, Activity.MODE_PRIVATE);
        return preferences.getBoolean(key,defaultValue);
    }

    public static void setInt(Context context,boolean isForever, String key, int value){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public static int getInt(Context context,boolean isForever,String key,int defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE, Activity.MODE_PRIVATE);
        return preferences.getInt(key,defaultValue);
    }

    public static void setLong(Context context,boolean isForever, String key, long value){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key,value);
        editor.commit();
    }

    public static long getLong(Context context,boolean isForever,String key,long defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(isForever?FOREVER:ONCE, Activity.MODE_PRIVATE);
        return preferences.getLong(key,defaultValue);
    }
}
