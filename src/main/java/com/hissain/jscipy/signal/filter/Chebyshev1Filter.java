package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Implements Chebyshev Type I filter operations, including `filtfilt` for zero-phase filtering.
 */
public class Chebyshev1Filter {

    private double[] lfilter_zi(double[] b, double[] a) {
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

    public double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order, double rippleDb) {
        Chebyshev1 cheby1 = new Chebyshev1();
        cheby1.lowPass(order, rippleDb, sampleRate, cutoff);

        Biquad[] biquads = cheby1.getBiquads();
        double[] output = signal.clone();

        for (Biquad biquad : biquads) {
            output = filtfilt_biquad(output, biquad);
        }

        return output;
    }

    private double[] filtfilt_biquad(double[] signal, Biquad biquad) {
        double[] b = biquad.getBCoefficients();
        double[] a = biquad.getACoefficients();

        int padlen = 3 * (Math.max(a.length, b.length) - 1);

        if (signal.length <= padlen) {
            return signal; // Not enough data to pad
        }

        double[] paddedSignal = new double[signal.length + 2 * padlen];
        for (int i = 0; i < padlen; i++) {
            paddedSignal[i] = 2 * signal[0] - signal[padlen - i];
        }
        System.arraycopy(signal, 0, paddedSignal, padlen, signal.length);
        for (int i = 0; i < padlen; i++) {
            paddedSignal[padlen + signal.length + i] = 2 * signal[signal.length - 1] - signal[signal.length - 2 - i];
        }

        double[] forward = filter_biquad(paddedSignal, b, a);
        double[] reversed = reverse(forward);
        double[] backward = filter_biquad(reversed, b, a);
        double[] reversedBackward = reverse(backward);

        double[] output = new double[signal.length];
        System.arraycopy(reversedBackward, padlen, output, 0, signal.length);

        return output;
    }

    private double[] filter_biquad(double[] signal, double[] b, double[] a) {
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

    public double[] filter(double[] signal, double sampleRate, double cutoff, int order, double rippleDb) {
        Chebyshev1 cheby1 = new Chebyshev1();
        cheby1.lowPass(order, rippleDb, sampleRate, cutoff);

        double[] output = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            output[i] = cheby1.filter(signal[i]);
        }

        return output;
    }

    private double[] reverse(double[] array) {
        double[] reversed = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
}
