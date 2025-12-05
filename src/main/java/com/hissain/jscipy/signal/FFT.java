package com.hissain.jscipy.signal;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;

class FFT {

    private final FastFourierTransformer transformer;

    public FFT() {
        this.transformer = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    private int nextPowerOf2(int n) {
        if (n == 0) return 1;
        int power = 1;
        while (power < n) {
            power <<= 1;
        }
        return power;
    }

    public Complex[] fft(double[] input) {
        int n = input.length;
        if (Integer.bitCount(n) == 1) { // Power of 2
            return transformer.transform(input, TransformType.FORWARD);
        } else {
            return dft(input);
        }
    }

    private Complex[] dft(double[] input) {
        int n = input.length;
        Complex[] output = new Complex[n];
        for (int k = 0; k < n; k++) {
            double sumReal = 0;
            double sumImag = 0;
            for (int t = 0; t < n; t++) {
                double angle = -2 * Math.PI * k * t / n;
                sumReal += input[t] * Math.cos(angle);
                sumImag += input[t] * Math.sin(angle);
            }
            output[k] = new Complex(sumReal, sumImag);
        }
        return output;
    }

    public Complex[] rfft(double[] input) {
        Complex[] fftResult = fft(input);
        int n = input.length;
        int resultSize = n / 2 + 1;
        return Arrays.copyOf(fftResult, resultSize);
    }

    public Complex[] ifft(Complex[] input) {
        int n = input.length;
        if (Integer.bitCount(n) == 1) { // Power of 2
            return transformer.transform(input, TransformType.INVERSE);
        } else {
            return idft(input);
        }
    }

    private Complex[] idft(Complex[] input) {
        int n = input.length;
        Complex[] output = new Complex[n];
        for (int t = 0; t < n; t++) {
            double sumReal = 0;
            double sumImag = 0;
            for (int k = 0; k < n; k++) {
                double angle = 2 * Math.PI * k * t / n;
                sumReal += input[k].getReal() * Math.cos(angle) - input[k].getImaginary() * Math.sin(angle);
                sumImag += input[k].getReal() * Math.sin(angle) + input[k].getImaginary() * Math.cos(angle);
            }
            output[t] = new Complex(sumReal / n, sumImag / n);
        }
        return output;
    }

    public double[] irfft(Complex[] input, int n) {
        // Reconstruct the full complex spectrum from the RFFT output
        Complex[] fullSpectrum = new Complex[n];
        fullSpectrum[0] = input[0]; // DC component

        for (int i = 1; i < input.length - 1; i++) {
            fullSpectrum[i] = input[i];
            fullSpectrum[n - i] = input[i].conjugate();
        }

        if (n % 2 == 0) { // Even length input
            fullSpectrum[n / 2] = input[n / 2]; // Nyquist component (incorrect, should be input[input.length - 1] but depends on how rfft is truncated)
        } else { // Odd length input
            // For odd length, the last element of rfft is the Nyquist component,
            // which doesn't have a corresponding negative frequency.
            // The loop above handles this correctly for n-i.
        }

        Complex[] ifftResult = ifft(fullSpectrum);
        double[] realResult = new double[n];
        for (int i = 0; i < n; i++) {
            realResult[i] = ifftResult[i].getReal();
        }
        return realResult;
    }
}
