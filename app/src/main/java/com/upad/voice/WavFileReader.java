package com.upad.voice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Simple WAV reader that supports PCM 16-bit WAV files (any channel count).
 * Usage:
 *  - new WavFileReader(inputStream)
 *  - call start(frameSize) to set frame size (samples per channel)
 *  - call frameMulti() repeatedly to receive interleaved frames split into short[channel][frameSize]
 *  - or call writePcmToFile(file) to extract entire PCM data to a raw little-endian file
 */
public class WavFileReader {
    private final InputStream in;
    private int sampleRate = 0;
    private int channels = 0;
    private int bitsPerSample = 0;
    private long dataLen = 0; // bytes remaining in data chunk
    private boolean headerParsed = false;

    private int frameSize = 160;
    private byte[] byteBuf;

    public WavFileReader(InputStream in) {
        this.in = in;
    }

    private int readLEInt(byte[] buf, int off) {
        return (buf[off] & 0xff) | ((buf[off+1] & 0xff) << 8) | ((buf[off+2] & 0xff) << 16) | ((buf[off+3] & 0xff) << 24);
    }

    private int readLEShort(byte[] buf, int off) {
        return (buf[off] & 0xff) | ((buf[off+1] & 0xff) << 8);
    }

    private void parseHeader() throws IOException {
        if (headerParsed) return;
        byte[] hdr = new byte[12];
        if (in.read(hdr) != hdr.length) throw new IOException("Invalid WAV header");
        String riff = new String(hdr, 0, 4, "ASCII");
        if (!"RIFF".equals(riff)) throw new IOException("Not a RIFF file");
        // skip file size
        String wave = new String(hdr, 8, 4, "ASCII");
        if (!"WAVE".equals(wave)) throw new IOException("Not a WAVE file");

        // read chunks until 'fmt ' and 'data' found
        boolean fmtFound = false;
        boolean dataFound = false;
        while (!fmtFound || !dataFound) {
            byte[] chunkHdr = new byte[8];
            if (in.read(chunkHdr) != chunkHdr.length) throw new IOException("Unexpected EOF parsing WAV chunks");
            String chunkId = new String(chunkHdr, 0, 4, "ASCII");
            int chunkSize = readLEInt(chunkHdr, 4);
            if ("fmt ".equals(chunkId)) {
                byte[] fmt = new byte[chunkSize];
                if (in.read(fmt) != fmt.length) throw new IOException("Unexpected EOF reading fmt chunk");
                int audioFormat = readLEShort(fmt, 0);
                if (audioFormat != 1) throw new IOException("Only PCM WAV supported");
                channels = readLEShort(fmt, 2);
                sampleRate = readLEInt(fmt, 4);
                bitsPerSample = readLEShort(fmt, 14);
                fmtFound = true;
            } else if ("data".equals(chunkId)) {
                dataLen = chunkSize;
                dataFound = true;
                break;
            } else {
                // skip unknown chunk
                long skipped = 0;
                while (skipped < chunkSize) {
                    long s = in.skip(chunkSize - skipped);
                    if (s <= 0) throw new IOException("Failed skipping chunk");
                    skipped += s;
                }
            }
        }
        if (!dataFound) throw new IOException("No data chunk found");
        if (bitsPerSample != 16) throw new IOException("Only 16-bit PCM WAV supported");
        headerParsed = true;
    }

    public void start(int frameSize) throws IOException {
        parseHeader();
        this.frameSize = frameSize;
        byteBuf = new byte[frameSize * channels * 2];
    }

    /**
     * Read next interleaved frame and return separated channels as short[channel][frameSize].
     * Returns null on EOF.
     */
    public short[][] frameMulti() throws IOException {
        if (!headerParsed) parseHeader();
        if (dataLen <= 0) return null;
        int toRead = byteBuf.length;
        int read = 0;
        while (read < toRead) {
            int r = in.read(byteBuf, read, toRead - read);
            if (r == -1) {
                dataLen = 0;
                return null;
            }
            read += r;
            dataLen -= r;
        }
        short[][] out = new short[channels][frameSize];
        ByteBuffer bb = ByteBuffer.wrap(byteBuf).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < frameSize; i++) {
            for (int c = 0; c < channels; c++) {
                out[c][i] = bb.getShort();
            }
        }
        return out;
    }

    /**
     * Write the raw PCM (16-bit little endian interleaved) to `out` file. Returns true on success.
     */
    public boolean writePcmToFile(File out) {
        try (FileOutputStream fos = new FileOutputStream(out)) {
            // ensure header parsed
            parseHeader();
            byte[] buf = new byte[4096];
            long remaining = dataLen;
            while (remaining > 0) {
                int toRead = (int) Math.min(buf.length, remaining);
                int r = in.read(buf, 0, toRead);
                if (r <= 0) break;
                fos.write(buf, 0, r);
                remaining -= r;
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getSampleRate() { return sampleRate; }
    public int getChannels() { return channels; }
    public int getBitsPerSample() { return bitsPerSample; }
}
