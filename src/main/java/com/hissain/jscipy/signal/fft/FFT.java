package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.api.IFFT;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;

public class FFT implements IFFT {

    private final FastFourierTransformer transformer;

    public FFT() {
        this.transformer = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    @Override
    public Complex[] fft(double[] input) {
        return transformer.transform(input, TransformType.FORWARD);
    }

    @Override
    public Complex[] rfft(double[] input) {
        Complex[] fftResult = fft(input);
        int n = input.length;
        int resultSize = n / 2 + 1;
        return Arrays.copyOf(fftResult, resultSize);
    }

    @Override
    public Complex[] ifft(Complex[] input) {
        return transformer.transform(input, TransformType.INVERSE);
    }

    @Override
    public double[] irfft(Complex[] input, int n) {
        // Reconstruct the full complex spectrum from the RFFT output
        Complex[] fullSpectrum = new Complex[n];
        fullSpectrum[0] = input[0]; // DC component

        for (int i = 1; i < input.length - 1; i++) {
            fullSpectrum[i] = input[i];
            fullSpectrum[n - i] = input[i].conjugate();
        }

        if (n % 2 == 0) { // Even length input
            fullSpectrum[n / 2] = input[n / 2]; // Nyquist component
        } else { // Odd length input
            // For odd length, the last element of rfft is the Nyquist component,
            // which doesn't have a corresponding negative frequency.
            // The loop above handles this correctly for n-i.
        }

        Complex[] ifftResult = transformer.transform(fullSpectrum, TransformType.INVERSE);
        double[] realResult = new double[n];
        for (int i = 0; i < n; i++) {
            realResult[i] = ifftResult[i].getReal();
        }
        return realResult;
    }
}
