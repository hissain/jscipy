package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;

/**
 * Discrete Cosine Transform (DCT) implementation.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/dct/dct_comparison_light.png"
 * alt="DCT Comparison" width="600">
 */
public class DCT {

    /**
     * Compute the 1D Discrete Cosine Transform (Type-II).
     * Matches scipy.fft.dct(x, type=2, norm=None).
     *
     * @param x The input signal.
     * @return The transform coefficients (unnormalized).
     */
    public double[] dct(double[] x) {
        return dct(x, false);
    }

    /**
     * Compute the 1D Discrete Cosine Transform (Type-II) with optional
     * orthogonality normalization.
     * <p>
     * If {@code ortho} is true, the result is scaled by:
     * <ul>
     * <li>sqrt(1 / (4N)) for k = 0</li>
     * <li>sqrt(1 / (2N)) for k > 0</li>
     * </ul>
     * This makes the DCT orthonormal.
     *
     * @param x     The input signal array.
     * @param ortho If true, applies orthogonality normalization (matches scipy
     *              norm='ortho').
     * @return The transformed coefficients array of length N.
     */
    public double[] dct(double[] x, boolean ortho) {
        int N = x.length;
        if (N == 0)
            return new double[0];

        // 1. Reorder signal to length N for FFT
        // v[n] = x[2n]
        // v[N-1-n] = x[2n+1]
        double[] v = new double[N];
        for (int n = 0; n < (N + 1) / 2; n++) {
            v[n] = x[2 * n];
            if (2 * n + 1 < N) {
                v[N - 1 - n] = x[2 * n + 1];
            }
        }

        // 2. Compute FFT of v
        // Note: FFT class is in the same package
        JComplex[] V = new FFT().fft(v);

        // 3. Compute DCT coefficients
        // X[k] = 2 * Re( V[k] * exp(-j * pi * k / (2N)) )
        double[] X = new double[N];
        double scale = 2.0;

        for (int k = 0; k < N; k++) {
            double theta = -Math.PI * k / (2.0 * N);
            // V[k] = a + jb
            // exp = cos(theta) + j*sin(theta)
            // Re( (a+jb)(c+jd) ) = ac - bd
            double a = V[k].real;
            double b = V[k].imag;
            double c = Math.cos(theta);
            double d = Math.sin(theta);

            X[k] = scale * (a * c - b * d);
        }

        // 4. Apply Ortho Normalization if requested
        if (ortho) {
            double s0 = Math.sqrt(1.0 / (4.0 * N));
            double sk = Math.sqrt(1.0 / (2.0 * N));
            X[0] *= s0; // Remove factor of 2 implicitly by scaling
            for (int k = 1; k < N; k++) {
                X[k] *= sk;
            }
        }

        return X;
    }
}
