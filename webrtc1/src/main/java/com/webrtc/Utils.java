package com.webrtc;

public class Utils {

    public static native int addNumbers(int num1, int num2);

    static {
        // 加载本地库
        System.loadLibrary("WRtcAudio");
    }
}
