package com.upad.voice;

/**
 * Simple far-end / near-end synchronizer using time-domain cross-correlation.
 * Caches last N frames of far-end and estimates delay (ms) between far-end and a near-end frame.
 * Not highly optimized — sufficient for moderate frame sizes and testing.
 */
public class EchoSynchronizer {

    private final int sampleRate;
    private final int frameSize;
    private final int maxDelayMs;
    private final int maxHistoryMs;
    private final int maxHistoryFrames;
    private final short[][] history; // circular buffer of farend frames
    private int writeIndex = 0;
    private int filled = 0;

    public EchoSynchronizer(int sampleRate, int frameSize, int maxDelayMs, int maxHistoryMs) {
        this.sampleRate = sampleRate;
        this.frameSize = frameSize;
        this.maxDelayMs = maxDelayMs;
        this.maxHistoryMs = maxHistoryMs;
        this.maxHistoryFrames = Math.max(1, (maxHistoryMs + (frameSize * 1000 / sampleRate) - 1) / (frameSize * 1000 / sampleRate));
        history = new short[maxHistoryFrames][];
    }

    public void bufferFarend(short[] farendFrame) {
        if (farendFrame == null) return;
        if (farendFrame.length != frameSize) return;
        history[writeIndex] = farendFrame.clone();
        writeIndex = (writeIndex + 1) % maxHistoryFrames;
        if (filled < maxHistoryFrames) filled++;
    }

    /**
     * Estimate delay in milliseconds between buffered far-end and given near-end frame.
     * Returns non-negative ms (0..maxDelayMs). If insufficient history, returns 0.
     */
    public int estimateDelayMs(short[] nearendFrame) {
        if (nearendFrame == null) return 0;
        if (nearendFrame.length != frameSize) return 0;
        if (filled == 0) return 0;

        // build concatenated farend buffer (most recent last)
        int totalSamples = filled * frameSize;
        short[] farAll = new short[totalSamples];
        int idx = 0;
        int start = (writeIndex - filled + maxHistoryFrames) % maxHistoryFrames;
        for (int f = 0; f < filled; f++) {
            short[] fr = history[(start + f) % maxHistoryFrames];
            if (fr == null) continue;
            System.arraycopy(fr, 0, farAll, idx, frameSize);
            idx += frameSize;
        }

        // max lag in samples
        int maxLagSamples = Math.min((maxDelayMs * sampleRate) / 1000, totalSamples - frameSize);
        if (maxLagSamples < 0) maxLagSamples = 0;

        // find lag (0..maxLagSamples) that maximizes cross-correlation
        long bestScore = Long.MIN_VALUE;
        int bestLag = 0;
        // search lags where farAll segment starts at (totalSamples - frameSize - lag)
        int baseEnd = totalSamples - frameSize;
        for (int lag = 0; lag <= maxLagSamples; lag++) {
            int startPos = baseEnd - lag;
            if (startPos < 0) break;
            long score = 0;
            for (int i = 0; i < frameSize; i++) {
                score += (long) nearendFrame[i] * (long) farAll[startPos + i];
            }
            if (score > bestScore) {
                bestScore = score;
                bestLag = lag;
            }
        }

        int delaySamples = bestLag;
        int delayMs = (int) ((delaySamples * 1000L) / sampleRate);
        if (delayMs < 0) delayMs = 0;
        if (delayMs > maxDelayMs) delayMs = maxDelayMs;
        return delayMs;
    }
}
