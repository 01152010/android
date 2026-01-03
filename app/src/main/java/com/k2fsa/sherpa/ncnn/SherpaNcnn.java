package com.k2fsa.sherpa.ncnn;

import android.content.res.AssetManager;

public class SherpaNcnn {
    private com.k2fsa.sherpa.ncnn.RecognizerConfig config;
    private final long ptr;

    static {
        System.loadLibrary("sherpa-ncnn-jni");
    }

    public SherpaNcnn(com.k2fsa.sherpa.ncnn.RecognizerConfig config, AssetManager assetManager) {
        this.config = config;
        if (assetManager != null) {
            this.ptr = newFromAsset(assetManager, config);
        } else {
            this.ptr = newFromFile(config);
        }
    }

    public SherpaNcnn(com.k2fsa.sherpa.ncnn.RecognizerConfig config) {
        this(config, null);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete(ptr);
        } finally {
            super.finalize();
        }
    }

    public void acceptSamples(float[] samples) {
        acceptWaveform(ptr, samples, config.getFeatConfig().getSampleRate());
    }

    public boolean isReady() {
        return isReady(ptr);
    }

    public void decode() {
        decode(ptr);
    }

    public void inputFinished() {
        inputFinished(ptr);
    }

    public boolean isEndpoint() {
        return isEndpoint(ptr);
    }

    public void reset() {
        reset(ptr, false);
    }

    public void reset(boolean recreate) {
        reset(ptr, recreate);
    }

    public String getText() {
        return getText(ptr);
    }

    public com.k2fsa.sherpa.ncnn.RecognizerConfig getConfig() {
        return config;
    }

    public void setConfig(com.k2fsa.sherpa.ncnn.RecognizerConfig config) {
        this.config = config;
    }

    // Native methods
    private native long newFromAsset(AssetManager assetManager, com.k2fsa.sherpa.ncnn.RecognizerConfig config);
    private native long newFromFile(com.k2fsa.sherpa.ncnn.RecognizerConfig config);
    private native void delete(long ptr);
    private native void acceptWaveform(long ptr, float[] samples, float sampleRate);
    private native void inputFinished(long ptr);
    private native boolean isReady(long ptr);
    private native void decode(long ptr);
    private native boolean isEndpoint(long ptr);
    private native void reset(long ptr, boolean recreate);
    private native String getText(long ptr);
}