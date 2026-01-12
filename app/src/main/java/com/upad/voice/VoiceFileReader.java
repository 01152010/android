package com.upad.voice;

import java.io.IOException;
import java.io.InputStream;

public class VoiceFileReader {

    private final InputStream in;
    private int channels = 1;
    private int frameSize = 160;
    private byte[] byteBuffer;

    public VoiceFileReader(InputStream in) {
        this.in = in;
    }

    public void start(int sampleRate, int frameSize, int channelCount) {
        this.frameSize = frameSize;
        this.channels = channelCount <= 0 ? 1 : channelCount;
        // 16-bit PCM little endian
        byteBuffer = new byte[frameSize * channels * 2];
    }

    /**
     * Read one interleaved multi-channel frame and return separated channels.
     * Returns null on EOF.
     */
    public short[][] frameMulti() {
        try {
            int toRead = byteBuffer.length;
            int read = 0;
            while (read < toRead) {
                int r = in.read(byteBuffer, read, toRead - read);
                if (r == -1) {
                    // EOF
                    return null;
                }
                read += r;
            }
            int fsize = frameSize;
            short[][] out = new short[channels][fsize];
            for (int i = 0; i < fsize; i++) {
                for (int c = 0; c < channels; c++) {
                    int idx = (i * channels + c) * 2;
                    int lo = byteBuffer[idx] & 0xff;
                    int hi = byteBuffer[idx + 1] & 0xff;
                    out[c][i] = (short) ((hi << 8) | lo);
                }
            }
            return out;
        } catch (IOException e) {
            return null;
        }
    }

    public void close() {
        try {
            in.close();
        } catch (IOException ignored) {}
    }
}
