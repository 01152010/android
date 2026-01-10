package com.upad.voice;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.annotation.RequiresPermission;

public class VoiceRecorder {

    private AudioRecord recorder;
    private short[] buffer;

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void start(int sampleRate, int frameSize) {
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        buffer = new short[frameSize];
        recorder.startRecording();
    }

    public short[] frame() {
        recorder.read(buffer, 0, buffer.length);
        return buffer;
    }

    public void stop() {
        recorder.stop();
    }

    public void release() {
        recorder.release();
    }
}