package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Implements Butterworth filter operations.
 * <p>
 * Butterworth filters are designed to have a frequency response that is as flat
 * as possible
 * in the passband (maximally flat magnitude filter).
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/butterworth/butterworth_multitone_o4_input.txt_light.png"
 * alt="Butterworth Comparison" style="width: 600px; max-width: 90%; display:
 * block; margin: 0 auto;">
 * <p>
 * This class provides methods to apply Butterworth low-pass filters to signals
 * using
 * both causal ({@link #filter(double[], double, double, int)}) and
 * zero-phase ({@link #filtfilt(double[], double, double, int)}) filtering.
 */
public class Butterworth {

    /**
     * Calculates the initial conditions for `lfilter` (linear filter).
     * This is a helper function for `filtfilt`.
     * 
     * @param b The numerator coefficients of the filter.
     * @param a The denominator coefficients of the filter.
     * @return The initial conditions `zi`.
     */
    private double[] lfilter_zi(double[] b, double[] a) {
        int n = Math.max(a.length, b.length);
        if (n <= 1) {
            return new double[0];
        }

        // The state space matrices for the transposed direct form II implementation
        // are A = scipy.linalg.companion(a).T and B = b[1:] - a[1:]*b[0]
        // The lfilter_zi solves (I-A)zi = B
        RealMatrix A = new Array2DRowRealMatrix(n - 1, n - 1);
        if (n > 1) {
            for (int i = 0; i < n - 2; i++) {
                A.setEntry(i, i + 1, 1.0);
            }
            for (int i = 0; i < n - 1; i++) {
                A.setEntry(n - 2, i, -a[i + 1]);
            }
        }

        RealMatrix IminusA = new Array2DRowRealMatrix(n - 1, n - 1);
        for (int i = 0; i < n - 1; i++) {
            IminusA.setEntry(i, i, 1.0);
        }
        IminusA = IminusA.subtract(A);

        RealVector B = new ArrayRealVector(n - 1);
        for (int i = 0; i < n - 1; i++) {
            double val = 0;
            if (i + 1 < b.length) {
                val = b[i + 1];
            }
            if (i + 1 < a.length) {
                val -= a[i + 1] * b[0];
            }
            B.setEntry(i, val);
        }

        double[] zi = new double[n - 1];
        if (n > 1) {
            double asum = 0;
            for (double v : a) {
                asum += v;
            }
            double csum = 0;
            for (int i = 1; i < b.length; i++) {
                csum += b[i] - a[i] * b[0];
            }
            zi[0] = csum / asum;
            for (int i = 1; i < n - 1; i++) {
                asum = 1.0;
                csum = 0.0;
                for (int j = 1; j < i + 1; j++) {
                    asum += a[j];
                    csum += b[j] - a[j] * b[0];
                }
                zi[i] = asum * zi[0] - csum;
            }
        }
        return zi;
    }

    /**
     * Applies a zero-phase digital filter forward and backward to a signal
     * (filtfilt).
     * This function applies a Butterworth low-pass filter.
     * <p>
     * /**
     * Applies a zero-phase Butterworth low-pass filter (filtfilt).
     * <p>
     * Zero-phase filtering avoids phase distortion by filtering forward and then
     * backward. This doubles the effective order of the filter.
     *
     * @param signal     The input signal to filter.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency of the filter in Hz.
     * @param order      The order of the Butterworth filter.
     * @return The filtered signal.
     */
    public double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order) {
        ButterworthDesign butterworth = new ButterworthDesign();
        butterworth.lowPass(order, sampleRate, cutoff);
        return runSosFiltFilt(signal, butterworth.getBiquads());
    }

    /**
     * Applies a zero-phase Butterworth high-pass filter (filtfilt).
     * <p>
     * High-pass filtering removes low-frequency components below the cutoff
     * frequency.
     * Zero-phase filtering avoids phase distortion.
     *
     * @param signal     The input signal to filter.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency of the filter in Hz.
     * @param order      The order of the Butterworth filter.
     * @return The filtered signal.
     */
    public double[] filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order) {
        ButterworthDesign butterworth = new ButterworthDesign();
        butterworth.highPass(order, sampleRate, cutoff);
        return runSosFiltFilt(signal, butterworth.getBiquads());
    }

    /**
     * Applies a zero-phase Butterworth band-pass filter (filtfilt).
     * <p>
     * Band-pass filtering allows frequencies within a specified range to pass
     * through
     * while attenuating frequencies outside that range.
     * Zero-phase filtering avoids phase distortion.
     *
     * @param signal          The input signal to filter.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the passband in Hz.
     * @param bandwidth       The width of the passband in Hz.
     * @param order           The order of the Butterworth filter.
     * @return The filtered signal.
     */
    public double[] filtfilt_bandpass(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order) {
        ButterworthDesign butterworth = new ButterworthDesign();
        butterworth.bandPass(order, sampleRate, centerFrequency, bandwidth);
        return runSosFiltFilt(signal, butterworth.getBiquads());
    }

    /**
     * Applies a zero-phase Butterworth band-stop (notch) filter (filtfilt).
     * <p>
     * Band-stop filtering attenuates frequencies within a specified range
     * while allowing frequencies outside that range to pass through.
     * Zero-phase filtering avoids phase distortion.
     *
     * @param signal          The input signal to filter.
     * @param sampleRate      The sample rate of the signal in Hz.
     * @param centerFrequency The center frequency of the stopband in Hz.
     * @param bandwidth       The width of the stopband in Hz.
     * @param order           The order of the Butterworth filter.
     * @return The filtered signal.
     */
    public double[] filtfilt_bandstop(double[] signal, double sampleRate, double centerFrequency,
            double bandwidth, int order) {
        ButterworthDesign butterworth = new ButterworthDesign();
        butterworth.bandStop(order, sampleRate, centerFrequency, bandwidth);
        return runSosFiltFilt(signal, butterworth.getBiquads());
    }

    /**
     * Internal helper: applies zero-phase SOS filtering with global padding.
     */
    private double[] runSosFiltFilt(double[] signal, Biquad[] biquads) {
        // Calculate pad length based on the total order/sections
        // SciPy uses 3 * (2 * n_sections + 1). Here n_sections = biquads.length
        int padlen = 3 * (2 * biquads.length + 1);

        if (signal.length <= padlen) {
            padlen = signal.length - 1;
        }

        // Pad the signal (odd extension)
        double[] paddedSignal = new double[signal.length + 2 * padlen];
        // Left reflection
        for (int i = 0; i < padlen; i++) {
            paddedSignal[i] = 2 * signal[0] - signal[padlen - i];
        }
        // Center
        System.arraycopy(signal, 0, paddedSignal, padlen, signal.length);
        // Right reflection
        for (int i = 0; i < padlen; i++) {
            paddedSignal[padlen + signal.length + i] = 2 * signal[signal.length - 1] - signal[signal.length - 2 - i];
        }

        // Forward filter (through all biquads)
        double[] forward = paddedSignal;
        for (Biquad biquad : biquads) {
            forward = filter_biquad(forward, biquad.getBCoefficients(), biquad.getACoefficients());
        }

        // Reverse
        double[] reversed = reverse(forward);

        // Backward filter (through all biquads)
        double[] backward = reversed;
        for (Biquad biquad : biquads) {
            backward = filter_biquad(backward, biquad.getBCoefficients(), biquad.getACoefficients());
        }

        // Reverse again
        double[] reversedBackward = reverse(backward);

        // Unpad
        double[] output = new double[signal.length];
        System.arraycopy(reversedBackward, padlen, output, 0, signal.length);

        return output;
    }

    /**
     * Applies a biquad filter section to a signal.
     * <p>
     * <img src=
     * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/butterworth/butterworth_multitone_o4_input.txt_light.png"
     * alt="Butterworth Multitone" style="width: 600px; max-width: 90%; display:
     * block; margin: 0 auto;">
     *
     * @param signal The input signal.
     * @param b      The numerator coefficients of the filter.
     * @param a      The denominator coefficients of the filter.
     * @return The filtered signal.
     */
    private double[] filter_biquad(double[] signal, double[] b, double[] a) {
        double[] output = new double[signal.length];
        double[] z = lfilter_zi(b, a);
        // Scale initial conditions by the first signal value
        for (int k = 0; k < z.length; k++) {
            z[k] *= signal[0];
        }

        // Direct Form II Transposed implementation
        for (int i = 0; i < signal.length; i++) {
            // y[n] = b[0]*x[n] + z[0]
            double y = b[0] * signal[i] + z[0];

            // Update states
            // z[j] = b[j+1]*x[n] + z[j+1] - a[j+1]*y[n]
            for (int j = 1; j < z.length; j++) {
                // z[j-1] is the current state being updated to next time step's z[j-1]
                // Wait, z vector shifts.
                // z[0](next) = z[1](curr) + b[1]*x - a[1]*y
                // z[1](next) ...
                z[j - 1] = b[j] * signal[i] + z[j] - a[j] * y;
            }
            // Last state
            if (z.length > 0) {
                z[z.length - 1] = b[z.length] * signal[i] - a[z.length] * y;
            }
            output[i] = y;
        }
        return output;
    }

    /**
     * Applies a Butterworth low-pass filter to a signal.
     * This function applies the filter in a causal manner (forward only), similar
     * to `lfilter`.
     * <p>
     * This method introduces phase distortion/delay. Use {@link #filtfilt} for
     * zero-phase filtering.
     *
     * @param signal     The input signal to filter.
     * @param sampleRate The sample rate of the signal in Hz.
     * @param cutoff     The cutoff frequency of the filter in Hz.
     * @param order      The order of the Butterworth filter.
     * @return The filtered signal.
     */
    public double[] filter(double[] signal, double sampleRate, double cutoff, int order) {
        ButterworthDesign butterworth = new ButterworthDesign();
        butterworth.lowPass(order, sampleRate, cutoff);

        Biquad[] biquads = butterworth.getBiquads();
        double[] output = signal.clone();
        for (Biquad biquad : biquads) {
            output = filter_biquad(output, biquad.getBCoefficients(), biquad.getACoefficients());
        }
        return output;
    }

    /**
     * Reverses the order of elements in a double array.
     * 
     * @param array The input array.
     * @return A new array with elements in reversed order.
     */
    private double[] reverse(double[] array) {
        double[] reversed = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

}
