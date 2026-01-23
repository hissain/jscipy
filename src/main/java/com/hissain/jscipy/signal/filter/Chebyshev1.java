package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Chebyshev Type I Filter implementation.
 * <p>
 * Chebyshev Type I filters are characterized by a steep roll-off and ripples in
 * the passband.
 * They minimize the error between the idealized and the actual filter
 * characteristic
 * over the range of the filter, but with ripples in the passband.
 * <p>
 * This class provides static methods for designing and applying Chebyshev Type
 * I filters.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/chebyshev/cheby1_input1.txt_light.png"
 * alt="Chebyshev Type I Comparison" width="100%">
 */
public class Chebyshev1 {

    private Chebyshev1() {
        // Prevent instantiation
    }

    /**
     * Applies a low-pass Chebyshev Type I filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param zeroPhase  If true, applies zero-phase filtering (filtfilt); otherwise
     *                   causal (lfilter).
     * @return The filtered signal.
     */
    public static double[] lowPass(double[] signal, double sampleRate, double cutoff, int order, double rippleDb,
            boolean zeroPhase) {
        Chebyshev1Design design = new Chebyshev1Design();
        design.lowPass(order, rippleDb, sampleRate, cutoff);
        if (zeroPhase) {
            return runFiltFilt(signal, design);
        } else {
            return runFilter(signal, design);
        }
    }

    /**
     * Applies a high-pass Chebyshev Type I filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param zeroPhase  If true, applies zero-phase filtering (filtfilt); otherwise
     *                   causal (lfilter).
     * @return The filtered signal.
     */
    public static double[] highPass(double[] signal, double sampleRate, double cutoff, int order, double rippleDb,
            boolean zeroPhase) {
        Chebyshev1Design design = new Chebyshev1Design();
        design.highPass(order, rippleDb, sampleRate, cutoff);
        if (zeroPhase) {
            return runFiltFilt(signal, design);
        } else {
            return runFilter(signal, design);
        }
    }

    /**
     * Applies a band-pass Chebyshev Type I filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate.
     * @param centerFreq The center frequency.
     * @param widthFreq  The bandwidth.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param zeroPhase  If true, applies zero-phase filtering (filtfilt); otherwise
     *                   causal (lfilter).
     * @return The filtered signal.
     */
    public static double[] bandPass(double[] signal, double sampleRate, double centerFreq, double widthFreq, int order,
            double rippleDb, boolean zeroPhase) {
        Chebyshev1Design design = new Chebyshev1Design();
        design.bandPass(order, rippleDb, sampleRate, centerFreq, widthFreq);
        if (zeroPhase) {
            return runFiltFilt(signal, design);
        } else {
            return runFilter(signal, design);
        }
    }

    /**
     * Applies a band-stop Chebyshev Type I filter.
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate.
     * @param centerFreq The center frequency.
     * @param widthFreq  The notch bandwidth.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @param zeroPhase  If true, applies zero-phase filtering (filtfilt); otherwise
     *                   causal (lfilter).
     * @return The filtered signal.
     */
    public static double[] bandStop(double[] signal, double sampleRate, double centerFreq, double widthFreq, int order,
            double rippleDb, boolean zeroPhase) {
        Chebyshev1Design design = new Chebyshev1Design();
        design.bandStop(order, rippleDb, sampleRate, centerFreq, widthFreq);
        if (zeroPhase) {
            return runFiltFilt(signal, design);
        } else {
            return runFilter(signal, design);
        }
    }

    /**
     * Applies a zero-phase Chebyshev Type I low-pass filter (filtfilt).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order, double rippleDb) {
        return lowPass(signal, sampleRate, cutoff, order, rippleDb, true);
    }

    /**
     * Applies a causal Chebyshev Type I low-pass filter (lfilter).
     *
     * @param signal     The input signal.
     * @param sampleRate The sample rate.
     * @param cutoff     The cutoff frequency.
     * @param order      The filter order.
     * @param rippleDb   The passband ripple in decibels.
     * @return The filtered signal.
     */
    public static double[] filter(double[] signal, double sampleRate, double cutoff, int order, double rippleDb) {
        return lowPass(signal, sampleRate, cutoff, order, rippleDb, false);
    }

    // --- Private Implementation ---

    private static double[] runFilter(double[] signal, Chebyshev1Design design) {
        double[] output = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            output[i] = design.filter(signal[i]);
        }
        return output;
    }

    private static double[] runFiltFilt(double[] signal, Chebyshev1Design design) {
        Biquad[] biquads = design.getBiquads();

        // Calculate pad length based on total sections (matching SciPy sosfiltfilt)
        int padlen = 3 * (2 * biquads.length + 1);

        if (signal.length <= padlen) {
            padlen = signal.length - 1;
        }

        // Pad signal (odd extension)
        double[] paddedSignal = new double[signal.length + 2 * padlen];
        for (int i = 0; i < padlen; i++) {
            paddedSignal[i] = 2 * signal[0] - signal[padlen - i];
        }
        System.arraycopy(signal, 0, paddedSignal, padlen, signal.length);
        for (int i = 0; i < padlen; i++) {
            paddedSignal[padlen + signal.length + i] = 2 * signal[signal.length - 1] - signal[signal.length - 2 - i];
        }

        // Forward pass through ALL biquads
        double[] forward = paddedSignal;
        for (Biquad biquad : biquads) {
            forward = filter_biquad(forward, biquad.getBCoefficients(), biquad.getACoefficients());
        }

        // Reverse
        double[] reversed = reverse(forward);

        // Backward pass through ALL biquads
        double[] backward = reversed;
        for (Biquad biquad : biquads) {
            backward = filter_biquad(backward, biquad.getBCoefficients(), biquad.getACoefficients());
        }

        // Reverse back
        double[] reversedBackward = reverse(backward);

        // Unpad
        double[] output = new double[signal.length];
        System.arraycopy(reversedBackward, padlen, output, 0, signal.length);

        return output;
    }

    private static double[] lfilter_zi(double[] b, double[] a) {
        int n = Math.max(a.length, b.length);
        if (n <= 1) {
            return new double[0];
        }

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

    private static double[] filter_biquad(double[] signal, double[] b, double[] a) {
        double[] output = new double[signal.length];
        double[] z = lfilter_zi(b, a);
        for (int k = 0; k < z.length; k++) {
            z[k] *= signal[0];
        }

        for (int i = 0; i < signal.length; i++) {
            double y = b[0] * signal[i] + z[0];
            for (int j = 1; j < z.length; j++) {
                z[j - 1] = b[j] * signal[i] + z[j] - a[j] * y;
            }
            if (z.length > 0) {
                z[z.length - 1] = b[z.length] * signal[i] - a[z.length] * y;
            }
            output[i] = y;
        }
        return output;
    }

    private static double[] reverse(double[] array) {
        double[] reversed = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
}
