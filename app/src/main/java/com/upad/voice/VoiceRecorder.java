package com.upad.voice;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.annotation.RequiresPermission;

public class VoiceRecorder {

    private AudioRecord recorder;
    private short[] buffer;
    private int channels = 1;

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void start(int sampleRate, int frameSize) {
        // default single channel
        start(sampleRate, frameSize, 1);
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void start(int sampleRate, int frameSize, int channelCount) {
        channels = channelCount <= 0 ? 1 : channelCount;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        if (channels == 6) {
            // try to use 5.1 input mask (may require API/device support)
            channelConfig = AudioFormat.CHANNEL_OUT_5POINT1;
        } else if (channels == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, Math.max(minBufferSize, frameSize * channels * 2));
        buffer = new short[frameSize * channels];
        recorder.startRecording();
    }

    public short[] frame() {
        recorder.read(buffer, 0, buffer.length);
        if (channels == 1) return buffer;
        // return first channel if caller expects mono
        short[] out = new short[buffer.length / channels];
        for (int i = 0; i < out.length; i++) out[i] = buffer[i * channels];
        return out;
    }

    /**
     * Read one multi-channel frame and return per-channel arrays (interleaved -> separated).
     * Returned array length equals channel count; each channel array length equals frameSize.
     */
    public short[][] frameMulti() {
        recorder.read(buffer, 0, buffer.length);
        int frameSize = buffer.length / channels;
        short[][] out = new short[channels][frameSize];
        for (int c = 0; c < channels; c++) {
            for (int i = 0; i < frameSize; i++) {
                out[c][i] = buffer[i * channels + c];
            }
        }
        return out;
    }

    public void stop() {
        recorder.stop();
    }

    public void release() {
        recorder.release();
    }
}