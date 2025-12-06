package com.hissain.jscipy.signal;

import com.hissain.jscipy.signal.filter.Butterworth;
import com.hissain.jscipy.signal.filter.Chebyshev1;
import com.hissain.jscipy.signal.filter.Chebyshev2;

import java.util.Map;

/**
 * A facade class providing static utility methods for signal processing, similar to {@code scipy.signal}.
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
    public static double[] cheby1_filtfilt(double[] signal, double sampleRate, double cutoff, int order, double rippleDb) {
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
    public static double[] cheby1_lfilter(double[] signal, double sampleRate, double cutoff, int order, double rippleDb) {
        return Chebyshev1.filter(signal, sampleRate, cutoff, order, rippleDb);
    }

    // --- Chebyshev Type II Filter ---

    /**
     * Applies a zero-phase Chebyshev Type II low-pass filter (forward and backward).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby2_filtfilt(double[] signal, double sampleRate, double cutoff, int order, double stopBandDb) {
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
    public static double[] cheby2_lfilter(double[] signal, double sampleRate, double cutoff, int order, double stopBandDb) {
        return Chebyshev2.filter(signal, sampleRate, cutoff, order, stopBandDb);
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
     * @param x      The input signal.
     * @param height Minimum height of peaks (optional, null to ignore).
     * @param distance Minimum distance between peaks (optional, null to ignore).
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

    /**
     * Computes the analytic signal using the Hilbert transform.
     *
     * @param signal The input signal (real-valued).
     * @return The analytic signal as an array of {@link JComplex} objects.
     */
    public static JComplex[] hilbert(double[] signal) {
        return new Hilbert().hilbert(signal);
    }

    // --- Spectral ---

    /**
     * Compute Power Spectral Density using Welch's method.
     * Uses default Hanning window and 50% overlap.
     *
     * @param x       Input signal.
     * @param fs      Sampling frequency.
     * @param nperseg Length of each segment.
     * @return WelchResult containing frequency array (f) and PSD array (Pxx).
     */
    public static Welch.WelchResult welch(double[] x, double fs, int nperseg) {
        return new Welch().welch(x, fs, nperseg);
    }

    // --- Median Filter ---

    /**
     * Applies a median filter to the signal.
     *
     * @param signal     The input signal.
     * @param kernelSize The size of the kernel (must be odd).
     * @return The filtered signal.
     */
    public static double[] medfilt(double[] signal, int kernelSize) {
        return new MedFilt().medfilt(signal, kernelSize);
    }

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

    // --- Savitzky-Golay Filter ---

    /**
     * Applies a Savitzky-Golay filter to an array.
     *
     * @param x           The data to be filtered.
     * @param windowLength The length of the filter window (i.e., the number of coefficients). window_length must be a positive odd integer.
     * @param polyOrder   The order of the polynomial used to fit the samples. polyorder must be less than window_length.
     * @param deriv       The order of the derivative to compute. This must be a non-negative integer. The default is 0, which means to filter the data without differentiating.
     * @param delta       The spacing of the samples to which the data will be applied. This is only used if deriv > 0. Default is 1.0.
     * @return The filtered data.
     */
    public static double[] savgol_filter(double[] x, int windowLength, int polyOrder, int deriv, double delta) {
        SavitzkyGolayFilter filter = new SavitzkyGolayFilter();
        if (deriv == 0) {
            return filter.smooth(x, windowLength, polyOrder);
        } else {
            return filter.differentiate(x, windowLength, polyOrder, deriv, delta);
        }
    }

    /**
     * Applies a Savitzky-Golay filter to an array (smoothing, derivative=0).
     *
     * @param x           The data to be filtered.
     * @param windowLength The length of the filter window.
     * @param polyOrder   The order of the polynomial.
     * @return The filtered (smoothed) data.
     */
    public static double[] savgol_filter(double[] x, int windowLength, int polyOrder) {
        return savgol_filter(x, windowLength, polyOrder, 0, 1.0);
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
