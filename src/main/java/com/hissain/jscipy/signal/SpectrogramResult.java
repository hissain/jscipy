package com.hissain.jscipy.signal;

/**
 * Result holder for spectrogram computation.
 * Contains frequencies, times, and the spectrogram matrix.
 */
public class SpectrogramResult {
    /**
     * Array of sample frequencies.
     * <p>
     * Length is {@code nfft / 2 + 1} for real inputs.
     * Units are the same as the sampling frequency {@code fs}.
     */
    public final double[] frequencies;

    /**
     * Array of segment times.
     * <p>
     * Length corresponds to the number of time segments (frames).
     * Units are seconds.
     */
    public final double[] times;

    /**
     * Spectrogram data (Power Spectral Density).
     * <p>
     * 2D array with dimensions {@code [frequencies.length][times.length]}.
     * {@code Sxx[i][j]} corresponds to frequency {@code frequencies[i]} and time
     * {@code times[j]}.
     */
    public final double[][] Sxx;

    /**
     * Constructs a new SpectrogramResult.
     *
     * @param frequencies Array of sample frequencies.
     * @param times       Array of segment times.
     * @param Sxx         Spectrogram data (PSD).
     */
    public SpectrogramResult(double[] frequencies, double[] times, double[][] Sxx) {
        this.frequencies = frequencies;
        this.times = times;
        this.Sxx = Sxx;
    }
}
