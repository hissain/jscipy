package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;

/**
 * Provides a method to compute the analytic signal using the Hilbert transform.
 * The Hilbert transform is a linear operator that takes a function, u(t), and
 * produces a function, H(u)(t),
 * with the same domain. The Hilbert transform is important in signal
 * processing, where it gives rise to
 * the analytic representation of a signal.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/hilbert/hilbert_comparison_1_light.png"
 * alt="Hilbert Transform Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 */
public class Hilbert {

    private final FFT fft;

    /**
     * Constructs a Hilbert transformer.
     */
    public Hilbert() {
        this.fft = new FFT();
    }

    /**
     * Computes the analytic signal of a real-valued input array.
     * The analytic signal of a real-valued signal `x(t)` is a complex-valued signal
     * `z(t) = x(t) + j*y(t)`,
     * where `y(t)` is the Hilbert transform of `x(t)`.
     *
     * @param signal The real-valued input signal array.
     * @return The complex-valued analytic signal.
     * @throws NullPointerException if the input signal is null.
     */
    public JComplex[] hilbert(double[] signal) {
        if (signal == null) {
            throw new NullPointerException("Signal cannot be null");
        }
        if (signal.length == 0) {
            return new JComplex[0];
        }

        int n = signal.length;
        JComplex[] spectrum = fft.fft(signal);
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

        JComplex[] weightedSpectrum = new JComplex[n];
        for (int i = 0; i < n; i++) {
            weightedSpectrum[i] = spectrum[i].multiply(h[i]);
        }

        return fft.ifft(weightedSpectrum);
    }
}
