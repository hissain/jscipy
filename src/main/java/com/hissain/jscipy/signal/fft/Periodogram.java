package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.Detrend;
import com.hissain.jscipy.signal.DetrendType;
import com.hissain.jscipy.signal.JComplex;
import com.hissain.jscipy.signal.Windows;

/**
 * Implementation of Periodogram for Power Spectral Density estimation.
 * Matches scipy.signal.periodogram behavior with 'density' scaling.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/periodogram/periodogram_comparison_light.png"
 * alt="Periodogram Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 */
public class Periodogram {

    /**
     * Result holder for periodogram computation.
     */
    public static class PeriodogramResult {
        /**
         * Array of sample frequencies in Hz.
         * Length is nfft / 2 + 1 for real inputs.
         */
        public final double[] frequencies;

        /**
         * Power Spectral Density estimate.
         * Units are V**2/Hz (for 'density' scaling).
         */
        public final double[] psd;

        public PeriodogramResult(double[] frequencies, double[] psd) {
            this.frequencies = frequencies;
            this.psd = psd;
        }
    }

    /**
     * Computes the periodogram of a signal with default parameters.
     * Uses Hann window, nfft = signal length, and constant detrending.
     *
     * @param x  The input signal.
     * @param fs The sampling frequency in Hz.
     * @return PeriodogramResult containing frequencies and PSD.
     */
    public PeriodogramResult periodogram(double[] x, double fs) {
        return periodogram(x, fs, null, -1, DetrendType.CONSTANT);
    }

    /**
     * Computes the periodogram of a signal.
     * Matches scipy.signal.periodogram with scaling='density'.
     *
     * @param x       The input signal.
     * @param fs      The sampling frequency in Hz.
     * @param window  The window function to apply. If null, uses Hann window.
     * @param nfft    The FFT size. If &lt;= 0, uses signal length.
     * @param detrend Detrending type to apply before windowing. Null for none.
     * @return PeriodogramResult containing frequencies and PSD.
     */
    public PeriodogramResult periodogram(double[] x, double fs, double[] window,
            int nfft, DetrendType detrend) {
        int n = x.length;
        if (nfft <= 0) {
            nfft = n;
        }

        // Generate default Hann window if not provided
        // SciPy periodogram uses fftbins=True (periodic) by default
        if (window == null) {
            window = Windows.hanning(n, false); // sym=false for periodic
        }

        // Apply detrending if requested
        double[] signal = x;
        if (detrend != null) {
            Detrend detrender = new Detrend();
            signal = detrender.detrend(x, detrend);
        }

        // Apply window
        double[] windowed = new double[nfft];
        for (int i = 0; i < n && i < nfft; i++) {
            windowed[i] = signal[i] * window[i];
        }
        // Zero-pad if nfft > n

        // Compute FFT
        FFT fft = new FFT();
        JComplex[] fftResult = fft.rfft(windowed);

        // Calculate number of frequency bins
        int numFreqBins = fftResult.length;

        // Calculate frequencies
        double[] frequencies = new double[numFreqBins];
        for (int i = 0; i < numFreqBins; i++) {
            frequencies[i] = i * fs / nfft;
        }

        // Calculate window sum squared for density scaling
        double windowSumSquared = 0.0;
        for (double w : window) {
            windowSumSquared += w * w;
        }

        // Density scaling factor: 1 / (fs * sum(window^2))
        double scale = 1.0 / (fs * windowSumSquared);

        // Compute PSD
        double[] psd = new double[numFreqBins];
        for (int i = 0; i < numFreqBins; i++) {
            double real = fftResult[i].getReal();
            double imag = fftResult[i].getImaginary();
            double power = real * real + imag * imag;

            // Scale for one-sided spectrum (multiply by 2 except DC and Nyquist)
            if (i > 0 && i < numFreqBins - 1) {
                power *= 2.0;
            }

            psd[i] = power * scale;
        }

        return new PeriodogramResult(frequencies, psd);
    }
}
