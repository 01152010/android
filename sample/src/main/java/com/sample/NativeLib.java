package com.sample;

public class NativeLib {

    // Used to load the 'sample' library on application startup.
    static {
        System.loadLibrary("sample");
    }

    /**
     * A native method that is implemented by the 'sample' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}