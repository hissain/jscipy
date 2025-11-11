package com.hissain.jscipy.signal;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import com.hissain.jscipy.signal.butterworth.Biquad;
import com.hissain.jscipy.signal.butterworth.Butterworth;
import com.hissain.jscipy.signal.api.IButterworthFilter;

public class ButterworthFilter implements IButterworthFilter {

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

    public double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order) {
        Butterworth butterworth = new Butterworth();
        butterworth.lowPass(order, sampleRate, cutoff);

        Biquad[] biquads = butterworth.getBiquads();
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

        // Pad the signal
        double[] paddedSignal = new double[signal.length + 2 * padlen];
        for (int i = 0; i < padlen; i++) {
            paddedSignal[i] = 2 * signal[0] - signal[padlen - i];
        }
        System.arraycopy(signal, 0, paddedSignal, padlen, signal.length);
        for (int i = 0; i < padlen; i++) {
            paddedSignal[padlen + signal.length + i] = 2 * signal[signal.length - 1] - signal[signal.length - 2 - i];
        }

        // Forward and backward filtering
        double[] forward = filter_biquad(paddedSignal, b, a);
        double[] reversed = reverse(forward);
        double[] backward = filter_biquad(reversed, b, a);
        double[] reversedBackward = reverse(backward);

        // Unpad the signal
        double[] output = new double[signal.length];
        System.arraycopy(reversedBackward, padlen, output, 0, signal.length);

        return output;
    }

    private double[] filter_biquad(double[] signal, double[] b, double[] a) {
        double[] output = new double[signal.length];
        double[] z = lfilter_zi(b, a);
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

    public double[] filter(double[] signal, double sampleRate, double cutoff, int order) {
        Butterworth butterworth = new Butterworth();
        butterworth.lowPass(order, sampleRate, cutoff);

        double[] output = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            output[i] = butterworth.filter(signal[i]);
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

    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 4) {
            System.err.println("Usage: ButterworthFilter <input_file> <order> <cutoff> <sample_rate>");
            System.exit(1);
        }
        String inputFile = args[0];
        int order = Integer.parseInt(args[1]);
        double cutoff = Double.parseDouble(args[2]);
        double sampleRate = Double.parseDouble(args[3]);

        java.util.List<Double> data = new java.util.ArrayList<>();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    data.add(Double.parseDouble(line));
                }
            }
        }
        double[] signal = data.stream().mapToDouble(Double::doubleValue).toArray();

        ButterworthFilter filterInstance = new ButterworthFilter();
        double[] output = filterInstance.filtfilt(signal, sampleRate, cutoff, order);

        for (double v : output) {
            System.out.println(v);
        }
    }
}
