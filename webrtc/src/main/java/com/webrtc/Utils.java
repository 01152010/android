package com.webrtc;

public class Utils {

    // Used to load the 'webrtc' library on application startup.
    static {
        System.loadLibrary("webrtc");
    }

    /**
     * A native method that is implemented by the 'webrtc' native library,
     * which is packaged with this application.
     */
    public static native int add(int i,int j);
}