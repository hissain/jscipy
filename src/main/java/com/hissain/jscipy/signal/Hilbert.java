package com.hissain.jscipy.signal;

import com.hissain.jscipy.signal.api.IHilbert;
import com.hissain.jscipy.signal.fft.FFT;
import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;

/**
 * Implementation of {@link IHilbert} for computing the analytic signal.
 */
public class Hilbert implements IHilbert {

    private final FFT fft;

    public Hilbert() {
        this.fft = new FFT();
    }

    @Override
    public Complex[] hilbert(double[] signal) {
        if (signal == null) {
            throw new NullPointerException("Signal cannot be null");
        }
        if (signal.length == 0) {
            return new Complex[0];
        }

        int n = signal.length;
        Complex[] spectrum = fft.fft(signal);
        double[] h = new double[n];

        if (n % 2 == 0) {
            h[0] = 1;
            for (int i = 1; i < n / 2; i++) {
                h[i] = 2;
            }
            h[n / 2] = 1;
            for (int i = n / 2 + 1; i < n; i++) {
                h[i] = 0;
            }
        } else {
            h[0] = 1;
            for (int i = 1; i < (n + 1) / 2; i++) {
                h[i] = 2;
            }
            for (int i = (n + 1) / 2; i < n; i++) {
                h[i] = 0;
            }
        }

        Complex[] weightedSpectrum = new Complex[n];
        for (int i = 0; i < n; i++) {
            weightedSpectrum[i] = spectrum[i].multiply(h[i]);
        }

        return fft.ifft(weightedSpectrum);
    }
}
