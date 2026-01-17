package com.hissain.jscipy.math;

import com.hissain.jscipy.signal.fft.FFT;
import com.hissain.jscipy.signal.JComplex;

/**
 * Helper class for signal resampling using FFT.
 */
public class Resample {

    private final FFT fft;

    /**
     * Constructs a Resample instance.
     */
    public Resample() {
        this.fft = new FFT();
    }

    /**
     * Resamples a signal to a specific number of samples using the Fourier method.
     *
     * @param signal The input signal.
     * @param num    The desired number of samples in the output.
     * @return The resampled signal.
     */
    public double[] resample(double[] signal, int num) {
        int len = signal.length;
        if (len == 0) {
            return new double[0];
        }
        if (num == len) {
            return signal.clone();
        }

        // 1. Compute full FFT
        JComplex[] X = fft.fft(signal);

        // 2. Create new spectrum of target length
        JComplex[] Y = new JComplex[num];
        for (int i = 0; i < num; i++) {
            Y[i] = JComplex.ZERO;
        }

        // 3. Spectral manipulation (matching SciPy's approach)
        if (num > len) {
            // Upsampling: insert zeros in the middle
            int halfLen = len / 2;

            // Copy DC and positive frequencies up to Nyquist
            for (int i = 0; i < halfLen; i++) {
                Y[i] = X[i];
            }

            // Handle Nyquist for even-length signals
            if (len % 2 == 0) {
                // Split the Nyquist component between positive and negative
                JComplex nyquist = X[halfLen].multiply(0.5);
                Y[halfLen] = nyquist;
                Y[num - halfLen] = nyquist;
            }

            // Copy negative frequencies to end of new spectrum
            for (int i = halfLen + 1; i < len; i++) {
                Y[num - len + i] = X[i];
            }
        } else {
            // Downsampling: truncate high frequencies
            int halfNum = num / 2;

            // Copy DC and lower positive frequencies
            for (int i = 0; i < halfNum; i++) {
                Y[i] = X[i];
            }

            // Handle Nyquist for even-length output
            if (num % 2 == 0) {
                // Combine contributions from both sides
                Y[halfNum] = X[halfNum].add(X[len - halfNum]);
            }

            // Copy lower negative frequencies
            for (int i = 1; i < halfNum + (num % 2 == 0 ? 0 : 1); i++) {
                Y[num - i] = X[len - i];
            }
        }

        // 4. Compute inverse FFT
        JComplex[] result = fft.ifft(Y);

        // 5. Extract real part and scale
        double scale = (double) num / len;
        double[] output = new double[num];
        for (int i = 0; i < num; i++) {
            output[i] = result[i].getReal() * scale;
        }

        return output;
    }
}
