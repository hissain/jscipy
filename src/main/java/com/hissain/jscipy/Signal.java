package com.hissain.jscipy;

import com.hissain.jscipy.signal.ConvolutionMode;
import com.hissain.jscipy.signal.Convolve;
import com.hissain.jscipy.signal.Detrend;
import com.hissain.jscipy.signal.DetrendType;
import com.hissain.jscipy.signal.FindPeaks;
import com.hissain.jscipy.signal.filter.MedFilt;
import com.hissain.jscipy.signal.filter.SavitzkyGolayFilter;
import com.hissain.jscipy.signal.JComplex;
import com.hissain.jscipy.signal.Windows;
import com.hissain.jscipy.signal.filter.Bessel;
import com.hissain.jscipy.signal.filter.Butterworth;
import com.hissain.jscipy.signal.filter.Chebyshev1;
import com.hissain.jscipy.signal.filter.Chebyshev2;
import com.hissain.jscipy.signal.filter.Elliptic;
import com.hissain.jscipy.signal.fft.FFT;
import com.hissain.jscipy.signal.fft.Spectrogram;
import com.hissain.jscipy.signal.fft.Welch;
import com.hissain.jscipy.signal.filter.SOSCascade;

/**
 * A facade class providing static utility methods for signal processing,
 * similar to {@code scipy.signal}.
 * This class delegates to specific implementations within the library.
 * <p>
 * For general mathematical operations (interpolation, resampling), see
 * {@link com.hissain.jscipy.Math}.
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

    /**
     * Applies a zero-phase Butterworth high-pass filter (forward and backward).
     * <p>
     * High-pass filtering removes low-frequency components below the cutoff
     * frequency.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order) {
        return new Butterworth().filtfilt_highpass(signal, sampleRate, cutoff, order);
    }

    /**
     * Applies a zero-phase Butterworth band-pass filter (forward and backward).
     * <p>
     * Band-pass filtering allows frequencies within a specified range to pass
     * through
     * while attenuating frequencies outside that range.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the passband in Hz.
     * @param bandwidth       The width of the passband in Hz.
     * @param order           The filter order.
     * @return The filtered signal.
     */
    public static double[] filtfilt_bandpass(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order) {
        return new Butterworth().filtfilt_bandpass(signal, sampleRate, centerFrequency, bandwidth, order);
    }

    /**
     * Applies a zero-phase Butterworth band-stop (notch) filter (forward and
     * backward).
     * <p>
     * Band-stop filtering attenuates frequencies within a specified range
     * while allowing frequencies outside that range to pass through.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the stopband in Hz.
     * @param bandwidth       The width of the stopband in Hz.
     * @param order           The filter order.
     * @return The filtered signal.
     */
    public static double[] filtfilt_bandstop(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order) {
        return new Butterworth().filtfilt_bandstop(signal, sampleRate, centerFrequency, bandwidth, order);
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

    /**
     * Applies a zero-phase Chebyshev Type I high-pass filter (forward and
     * backward).
     * <p>
     * High-pass filtering removes low-frequency components below the cutoff
     * frequency.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby1_filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order,
            double rippleDb) {
        return Chebyshev1.highPass(signal, sampleRate, cutoff, order, rippleDb, true);
    }

    /**
     * Applies a zero-phase Chebyshev Type I band-pass filter (forward and
     * backward).
     * <p>
     * Band-pass filtering allows frequencies within a specified range to pass
     * through.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the passband in Hz.
     * @param bandwidth       The width of the passband in Hz.
     * @param order           The filter order.
     * @param rippleDb        The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby1_filtfilt_bandpass(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order, double rippleDb) {
        return Chebyshev1.bandPass(signal, sampleRate, centerFrequency, bandwidth, order, rippleDb, true);
    }

    /**
     * Applies a zero-phase Chebyshev Type I band-stop (notch) filter (forward and
     * backward).
     * <p>
     * Band-stop filtering attenuates frequencies within a specified range.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the stopband in Hz.
     * @param bandwidth       The width of the stopband in Hz.
     * @param order           The filter order.
     * @param rippleDb        The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby1_filtfilt_bandstop(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order, double rippleDb) {
        return Chebyshev1.bandStop(signal, sampleRate, centerFrequency, bandwidth, order, rippleDb, true);
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

    /**
     * Applies a zero-phase Chebyshev Type II high-pass filter (forward and
     * backward).
     * <p>
     * High-pass filtering removes low-frequency components below the cutoff
     * frequency.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The filter order.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby2_filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order,
            double stopBandDb) {
        return Chebyshev2.highPass(signal, sampleRate, cutoff, order, stopBandDb, true);
    }

    /**
     * Applies a zero-phase Chebyshev Type II band-pass filter (forward and
     * backward).
     * <p>
     * Band-pass filtering allows frequencies within a specified range to pass
     * through.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the passband in Hz.
     * @param bandwidth       The width of the passband in Hz.
     * @param order           The filter order.
     * @param stopBandDb      The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby2_filtfilt_bandpass(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order, double stopBandDb) {
        return Chebyshev2.bandPass(signal, sampleRate, centerFrequency, bandwidth, order, stopBandDb, true);
    }

    /**
     * Applies a zero-phase Chebyshev Type II band-stop (notch) filter (forward and
     * backward).
     * <p>
     * Band-stop filtering attenuates frequencies within a specified range.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the stopband in Hz.
     * @param bandwidth       The width of the stopband in Hz.
     * @param order           The filter order.
     * @param stopBandDb      The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] cheby2_filtfilt_bandstop(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order, double stopBandDb) {
        return Chebyshev2.bandStop(signal, sampleRate, centerFrequency, bandwidth, order, stopBandDb, true);
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

    /**
     * Applies a zero-phase Elliptic high-pass filter (forward and backward).
     * <p>
     * High-pass filtering removes low-frequency components below the cutoff
     * frequency.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param stopBandDb The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] ellip_filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order,
            double rippleDb, double stopBandDb) {
        return Elliptic.highPass(signal, sampleRate, cutoff, order, rippleDb, stopBandDb, true);
    }

    /**
     * Applies a zero-phase Elliptic band-pass filter (forward and backward).
     * <p>
     * Band-pass filtering allows frequencies within a specified range to pass
     * through.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the passband in Hz.
     * @param bandwidth       The width of the passband in Hz.
     * @param order           The filter order.
     * @param rippleDb        The passband ripple in decibels.
     * @param stopBandDb      The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] ellip_filtfilt_bandpass(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order, double rippleDb, double stopBandDb) {
        return Elliptic.bandPass(signal, sampleRate, centerFrequency, bandwidth, order, rippleDb, stopBandDb, true);
    }

    /**
     * Applies a zero-phase Elliptic band-stop (notch) filter (forward and
     * backward).
     * <p>
     * Band-stop filtering attenuates frequencies within a specified range.
     *
     * @param signal          The input signal.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the stopband in Hz.
     * @param bandwidth       The width of the stopband in Hz.
     * @param order           The filter order.
     * @param rippleDb        The passband ripple in decibels.
     * @param stopBandDb      The stopband attenuation in decibels.
     * @return The filtered signal.
     */
    public static double[] ellip_filtfilt_bandstop(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order, double rippleDb, double stopBandDb) {
        return Elliptic.bandStop(signal, sampleRate, centerFrequency, bandwidth, order, rippleDb, stopBandDb, true);
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

    /**
     * Applies a zero-phase Bessel high-pass filter (forward and backward).
     * <p>
     * High-pass filtering removes low-frequency components below the cutoff
     * frequency.
     * Bessel filters are optimized for linear phase response.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] bessel_filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order) {
        return Bessel.filtfilt_highpass(signal, sampleRate, cutoff, order);
    }

    /**
     * Applies a zero-phase Bessel band-pass filter (forward and backward).
     * <p>
     * Band-pass filtering allows frequencies within a specified range to pass
     * through.
     * Bessel filters are optimized for linear phase response.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param lowCutoff  The lower cutoff frequency in Hz.
     * @param highCutoff The upper cutoff frequency in Hz.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] bessel_filtfilt_bandpass(double[] signal, double sampleRate, double lowCutoff,
            double highCutoff, int order) {
        return Bessel.filtfilt_bandpass(signal, sampleRate, lowCutoff, highCutoff, order);
    }

    /**
     * Applies a zero-phase Bessel band-stop (notch) filter (forward and backward).
     * <p>
     * Band-stop filtering attenuates frequencies within a specified range.
     * Bessel filters are optimized for linear phase response.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param lowCutoff  The lower cutoff frequency in Hz.
     * @param highCutoff The upper cutoff frequency in Hz.
     * @param order      The filter order.
     * @return The filtered signal.
     */
    public static double[] bessel_filtfilt_bandstop(double[] signal, double sampleRate, double lowCutoff,
            double highCutoff, int order) {
        return Bessel.filtfilt_bandstop(signal, sampleRate, lowCutoff, highCutoff, order);
    }

    // --- SOS Filtering ---

    /**
     * Filter data using a series of second-order sections (SOS).
     *
     * @param signal The input signal.
     * @param sos    Array of second-order sections of shape [n_sections][6].
     *               Each section includes [b0, b1, b2, a0, a1, a2].
     * @return The filtered signal.
     */
    public static double[] sosfilt(double[] signal, double[][] sos) {
        SOSCascade filter = new SOSCascade();
        filter.setup(sos);
        double[] output = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            output[i] = filter.filter(signal[i]);
        }
        return output;
    }

    // --- Utilities ---

    /**
     * Pads a signal using odd extension at both ends.
     * <p>
     * This extension mode (also known as "reflective" or "antisymmetric" padding)
     * creates a smooth transition at the boundaries, which is useful for filtering
     * operations to reduce edge artifacts.
     *
     * @param x      The input signal.
     * @param padlen The number of samples to pad on both sides.
     * @return The padded signal with length {@code x.length + 2 * padlen}.
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
     * @param height     Minimum height of peaks (optional, {@code Double.NaN} to
     *                   ignore).
     * @param distance   Minimum distance between peaks (optional, -1 to ignore).
     * @param prominence Minimum prominence of peaks (optional, {@code Double.NaN}
     *                   to ignore).
     * @return The indices of found peaks.
     */
    public static int[] find_peaks(double[] x, double height, int distance, double prominence) {
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
        return find_peaks(x, Double.NaN, -1, Double.NaN);
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

    /**
     * Returns a symmetric Blackman window of length M.
     *
     * @param m The length of the window.
     * @return The Blackman window.
     */
    public static double[] blackman(int m) {
        return Windows.blackman(m);
    }

    /**
     * Returns a Blackman window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Blackman window.
     */
    public static double[] blackman(int m, boolean symmetric) {
        return Windows.blackman(m, symmetric);
    }

    /**
     * Returns a symmetric Kaiser window of length M with shape parameter beta.
     *
     * @param m    The length of the window.
     * @param beta The shape parameter.
     * @return The Kaiser window.
     */
    public static double[] kaiser(int m, double beta) {
        return Windows.kaiser(m, beta);
    }

    /**
     * Returns a Kaiser window of length M with shape parameter beta.
     *
     * @param m         The length of the window.
     * @param beta      The shape parameter.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Kaiser window.
     */
    public static double[] kaiser(int m, double beta, boolean symmetric) {
        return Windows.kaiser(m, beta, symmetric);
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

    /**
     * Computes the 2D FFT of a real-valued 2D array.
     *
     * @param input The 2D input array [rows][cols].
     * @return The 2D complex-valued FFT.
     */
    public static JComplex[][] fft2(double[][] input) {
        return new FFT().fft2(input);
    }

    /**
     * Computes the inverse 2D FFT.
     *
     * @param input The 2D complex spectrum [rows][cols].
     * @return The 2D complex spatial domain signal.
     */
    public static JComplex[][] ifft2(JComplex[][] input) {
        return new FFT().ifft2(input);
    }

    // --- Spectral (Welch) ---

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

    /**
     * Applies a Savitzky-Golay filter to an array.
     *
     * @param x            The data to be filtered.
     * @param windowLength The length of the filter window (i.e., the number of
     *                     coefficients). window_length must be a positive odd
     *                     integer.
     * @param polyOrder    The order of the polynomial used to fit the samples.
     *                     polyorder must be less than window_length.
     * @param deriv        The order of the derivative to compute. This must be a
     *                     non-negative integer. The default is 0, which means to
     *                     filter the data.
     * @param delta        The spacing of the samples to which the filter will be
     *                     applied. This is only used if deriv > 0. Default is 1.0.
     * @return The filtered data.
     */
    public static double[] savgol_filter(double[] x, int windowLength, int polyOrder, int deriv, double delta) {
        return new SavitzkyGolayFilter().savgol_filter(x, windowLength, polyOrder, deriv, delta);
    }

    /**
     * Applies a Savitzky-Golay filter to an array (smoothing, derivative=0).
     *
     * @param x            The data to be filtered.
     * @param windowLength The length of the filter window.
     * @param polyOrder    The order of the polynomial.
     * @return The filtered (smoothed) data.
     */
    public static double[] savgol_filter(double[] x, int windowLength, int polyOrder) {
        return new SavitzkyGolayFilter().savgol_filter(x, windowLength, polyOrder);
    }

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

    /**
     * Convolves two 2D signals using the specified mode.
     *
     * @param in1  The first input array.
     * @param in2  The second input array.
     * @param mode The convolution mode (FULL, VALID, SAME).
     * @return The convolved 2D array.
     */
    public static double[][] convolve2d(double[][] in1, double[][] in2, ConvolutionMode mode) {
        return new Convolve().convolve2d(in1, in2, mode);
    }

    /**
     * Computes the spectrogram of a signal.
     * <p>
     * Spectrograms can be used as a way of visualizing the change of a
     * nonstationary
     * signal's frequency content over time.
     *
     * @param signal The input signal (time series).
     * @param fs     The sampling frequency of the signal in Hz.
     * @return A {@link SpectrogramResult} containing sample frequencies,
     *         segment times,
     *         and the spectrogram (power spectral density).
     */
    public static Spectrogram.SpectrogramResult spectrogram(double[] signal, double fs) {
        return new Spectrogram().spectrogram(signal, fs);
    }

    /**
     * Compute the Short-Time Fourier Transform (STFT).
     * <p>
     * Uses default parameters: nperseg=256, noverlap=128, Hann window.
     * </p>
     *
     * @param x The input signal.
     * @return 2D complex array [frequency bins][time frames].
     */
    public static JComplex[][] stft(double[] x) {
        return new FFT().stft(x);
    }

    /**
     * Compute the Inverse Short-Time Fourier Transform (ISTFT).
     *
     * @param stftMatrix 2D complex array [frequency bins][time frames].
     * @return Reconstructed time-domain signal.
     */
    public static double[] istft(JComplex[][] stftMatrix) {
        return new FFT().istft(stftMatrix);
    }
}
