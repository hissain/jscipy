package com.hissain.jscipy.signal;

import com.hissain.jscipy.signal.filter.Butterworth;
import com.hissain.jscipy.signal.filter.Chebyshev1;
import com.hissain.jscipy.signal.filter.Chebyshev2;
import com.hissain.jscipy.signal.filter.Chebyshev2;
import com.hissain.jscipy.signal.filter.Elliptic;
import com.hissain.jscipy.signal.filter.Bessel;
import com.hissain.jscipy.signal.fft.FFT;

import java.util.Map;

/**
 * A facade class providing static utility methods for signal processing,
 * similar to {@code scipy.signal}.
 * This class delegates to specific implementations within the library.
 */
public class Signal {

    // --- Butterworth Filter ---

    /**
     * Applies a zero-phase Butterworth low-pass filter (forward and backward).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order) {
        return new Butterworth().filtfilt(signal, sampleRate, cutoff, order);
    }

    /**
     * Applies a standard (causal) Butterworth low-pass filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] lfilter(double[] signal, double sampleRate, double cutoff, int order) {
        return new Butterworth().filter(signal, sampleRate, cutoff, order);
    }

    // --- Chebyshev Type I Filter ---

    /**
     * Applies a zero-phase Chebyshev Type I low-pass filter (forward and backward).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby1_filtfilt(double[] signal, double sampleRate, double cutoff, int order,
            double rippleDb) {
        return Chebyshev1.filtfilt(signal, sampleRate, cutoff, order, rippleDb);
    }

    /**
     * Applies a standard (causal) Chebyshev Type I low-pass filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby1_lfilter(double[] signal, double sampleRate, double cutoff, int order,
            double rippleDb) {
        return Chebyshev1.filter(signal, sampleRate, cutoff, order, rippleDb);
    }

    // --- Chebyshev Type II Filter ---

    /**
     * Applies a zero-phase Chebyshev Type II low-pass filter (forward and
     * backward).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby2_filtfilt(double[] signal, double sampleRate, double cutoff, int order,
            double stopBandDb) {
        return Chebyshev2.filtfilt(signal, sampleRate, cutoff, order, stopBandDb);
    }

    /**
     * Applies a standard (causal) Chebyshev Type II low-pass filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby2_lfilter(double[] signal, double sampleRate, double cutoff, int order,
            double stopBandDb) {
        return Chebyshev2.filter(signal, sampleRate, cutoff, order, stopBandDb);
    }

    // --- Elliptic Filter ---

    /**
     * Applies a zero-phase Elliptic low-pass filter (forward and backward).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] ellip_filtfilt(double[] signal, double sampleRate, double cutoff, int order, double rippleDb,
            double stopBandDb) {
        return Elliptic.filtfilt(signal, sampleRate, cutoff, order, rippleDb, stopBandDb);
    }

    /**
     * Applies a standard (causal) Elliptic low-pass filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] ellip_lfilter(double[] signal, double sampleRate, double cutoff, int order, double rippleDb,
            double stopBandDb) {
        return Elliptic.filter(signal, sampleRate, cutoff, order, rippleDb, stopBandDb);
    }

    // --- Bessel Filter ---

    /**
     * Applies a zero-phase Bessel low-pass filter (forward and backward).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] bessel_filtfilt(double[] signal, double sampleRate, double cutoff, int order) {
        return Bessel.filtfilt(signal, sampleRate, cutoff, order);
    }

    // --- Utilities ---

    /**
     * Helper to pad signal with odd extension.
     */
    public static double[] padSignal(double[] x, int padlen) {
        double[] padded = new double[x.length + 2 * padlen];
        // Left pad: 2*x[0] - x[padlen..1]
        for (int i = 0; i < padlen; i++) {
            padded[i] = 2 * x[0] - x[padlen - i];
        }
        // Middle: x
        System.arraycopy(x, 0, padded, padlen, x.length);
        // Right pad: 2*x[end] - x[end-1..end-padlen]
        for (int i = 0; i < padlen; i++) {
            padded[padlen + x.length + i] = 2 * x[x.length - 1] - x[x.length - 2 - i];
        }
        return padded;
    }

    // --- Detrend ---

    /**
     * Removes a linear or constant trend from the signal.
     *
     * @param signal The input signal.
     * @param type   The type of detrending (LINEAR or CONSTANT).
     * @return The detrended signal.
     */
    public static double[] detrend(double[] signal, DetrendType type) {
        return new Detrend().detrend(signal, type);
    }

    // --- Find Peaks ---

    /**
     * Finds peaks in a signal with optional parameters.
     *
     * @param x          The input signal.
     * @param height     Minimum height of peaks (optional, null to ignore).
     * @param distance   Minimum distance between peaks (optional, null to ignore).
     * @param prominence Minimum prominence of peaks (optional, null to ignore).
     * @return The indices of found peaks.
     */
    public static int[] find_peaks(double[] x, Double height, Integer distance, Double prominence) {
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.height = height;
        params.distance = distance;
        params.prominence = prominence;
        return new FindPeaks().findPeaks(x, params).peaks;
    }

    /**
     * Finds peaks in a signal with default parameters.
     *
     * @param x The input signal.
     * @return The indices of found peaks.
     */
    public static int[] find_peaks(double[] x) {
        return find_peaks(x, null, null, null);
    }

    // --- Hilbert Transform ---

    // --- Window Functions ---

    /**
     * Returns a Hanning window of length M.
     *
     * @param m The length of the window.
     * @return The Hanning window.
     */
    public static double[] hanning(int m) {
        return Windows.hanning(m);
    }

    /**
     * Returns a Hanning window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Hanning window.
     */
    public static double[] hanning(int m, boolean symmetric) {
        return Windows.hanning(m, symmetric);
    }

    /**
     * Returns a Hamming window of length M.
     *
     * @param m The length of the window.
     * @return The Hamming window.
     */
    public static double[] hamming(int m) {
        return Windows.hamming(m);
    }

    /**
     * Returns a Hamming window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Hamming window.
     */
    public static double[] hamming(int m, boolean symmetric) {
        return Windows.hamming(m, symmetric);
    }
    // --- FFT ---

    /**
     * Computes the forward FFT of a real-valued signal.
     *
     * @param x The input signal.
     * @return The complex-valued FFT of the signal.
     */
    public static JComplex[] fft(double[] x) {
        return new FFT().fft(x);
    }

    /**
     * Computes the inverse FFT of a complex-valued signal.
     *
     * @param x The complex-valued input signal.
     * @return The complex-valued inverse FFT of the signal.
     */
    public static JComplex[] ifft(JComplex[] x) {
        return new FFT().ifft(x);
    }

    /**
     * Computes the forward FFT of a real-valued signal and returns the positive
     * frequency components (RFFT).
     *
     * @param x The input signal.
     * @return The positive frequency components of the FFT.
     */
    public static JComplex[] rfft(double[] x) {
        return new FFT().rfft(x);
    }

    /**
     * Computes the inverse FFT of a real-valued signal (IRFFT).
     *
     * @param x The complex-valued input signal (positive frequencies).
     * @param n The length of the original signal.
     * @return The real-valued inverse FFT of the signal.
     */
    public static double[] irfft(JComplex[] x, int n) {
        return new FFT().irfft(x, n);
    }

    // --- Spectral (Welch) ---

    // --- Convolution ---

    /**
     * Convolves two signals using the specified mode.
     *
     * @param signal The first signal.
     * @param window The second signal (window).
     * @param mode   The convolution mode (e.g., ConvolutionMode.SAME).
     * @return The convolved signal.
     */
    public static double[] convolve(double[] signal, double[] window, ConvolutionMode mode) {
        return new Convolve().convolve(signal, window, mode);
    }

    // --- Resample ---

    /**
     * Resamples x to num samples using Fourier method along the given axis.
     *
     * @param x   The input signal.
     * @param num The number of samples in the resampled signal.
     * @return The resampled signal.
     */
    public static double[] resample(double[] x, int num) {
        return new Resample().resample(x, num);
    }

    // --- Interpolation ---

    /**
     * Performs linear interpolation on the given data points.
     *
     * @param x    The x-coordinates of the data points.
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates at which to evaluate the interpolated values.
     * @return The interpolated values.
     */
    public static double[] interp1d_linear(double[] x, double[] y, double[] newX) {
        return new Interpolation().linear(x, y, newX);
    }

    /**
     * Performs cubic spline interpolation on the given data points.
     *
     * @param x    The x-coordinates of the data points.
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates at which to evaluate the interpolated values.
     * @return The interpolated values.
     */
    public static double[] interp1d_cubic(double[] x, double[] y, double[] newX) {
        return new Interpolation().cubic(x, y, newX);
    }
}
