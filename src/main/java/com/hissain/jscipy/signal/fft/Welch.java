package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;

import com.hissain.jscipy.signal.Windows;

/**
 * Implementation of Welch's method for spectral density estimation.
 */
public class Welch {

    /**
     * Holds the result of Welch's method for spectral density estimation.
     */
    public static class WelchResult {
        /**
         * Array of sample frequencies.
         */
        public final double[] f;
        /**
         * Power Spectral Density or power spectrum of x.
         */
        public final double[] Pxx;

        /**
         * Constructs a WelchResult.
         *
         * @param f   Array of sample frequencies.
         * @param Pxx Power Spectral Density or power spectrum.
         */
        public WelchResult(double[] f, double[] Pxx) {
            this.f = f;
            this.Pxx = Pxx;
        }
    }

    /**
     * Compute Power Spectral Density using Welch's method.
     * Uses default Hanning window and 50% overlap.
     *
     * @param x       Input signal.
     * @param fs      Sampling frequency.
     * @param nperseg Length of each segment.
     * @return WelchResult containing frequency array (f) and PSD array (Pxx).
     */
    public WelchResult welch(double[] x, double fs, int nperseg) {
        int noverlap = nperseg / 2;
        // SciPy uses periodic window for spectral analysis
        double[] window = Windows.hanning(nperseg, false);
        return welch(x, fs, window, nperseg, noverlap);
    }

    /**
     * Compute Power Spectral Density using Welch's method with custom parameters.
     *
     * @param x        Input signal.
     * @param fs       Sampling frequency.
     * @param window   Window function array (length must match nperseg).
     * @param nperseg  Length of each segment.
     * @param noverlap Number of points to overlap between segments.
     * @return WelchResult containing frequency array (f) and PSD array (Pxx).
     */
    public WelchResult welch(double[] x, double fs, double[] window, int nperseg, int noverlap) {
        if (x.length < nperseg) {
            throw new IllegalArgumentException("Signal length must be >= nperseg");
        }
        if (window.length != nperseg) {
            throw new IllegalArgumentException("Window length must match nperseg");
        }

        int nfft = nperseg;
        int step = nperseg - noverlap;
        int numSegments = (x.length - nperseg) / step + 1;

        // Calculate Window Energy/Scale
        double winSumSq = 0;
        for (double v : window) {
            winSumSq += v * v;
        }
        double scale = 1.0 / (fs * winSumSq);

        int numFreqs = nfft / 2 + 1;
        double[] f = new double[numFreqs];
        for (int i = 0; i < numFreqs; i++) {
            f[i] = i * fs / nfft;
        }

        double[] psd = new double[numFreqs];

        FFT fft = new FFT();

        for (int i = 0; i < numSegments; i++) {
            int start = i * step;
            double[] segment = new double[nperseg];

            for (int j = 0; j < nperseg; j++) {
                segment[j] = x[start + j] * window[j];
            }

            JComplex[] spectrum = fft.rfft(segment);

            for (int j = 0; j < numFreqs; j++) {
                double re = spectrum[j].real;
                double im = spectrum[j].imag;
                double magSq = re * re + im * im;

                if (j > 0) {
                    if (nfft % 2 == 0 && j == nfft / 2) {
                        // Nyquist: don't double
                    } else {
                        magSq *= 2;
                    }
                }

                psd[j] += magSq;
            }
        }

        for (int j = 0; j < numFreqs; j++) {
            psd[j] = (psd[j] / numSegments) * scale;
        }

        return new WelchResult(f, psd);
    }
}
