package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;

/**
 * Discrete Cosine Transform (DCT) implementation.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/dct/dct_comparison_light.png"
 * alt="DCT Comparison" style="width: 600px; max-width: 90%; display: block;
 * margin: 0 auto;">
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

    /**
     * Compute the 1D Inverse Discrete Cosine Transform (Type-II).
     * This calculates the inverse of the DCT-II (which is DCT-III).
     * Matches scipy.fft.idct(x, type=2, norm=None).
     *
     * @param x The input DCT coefficients.
     * @return The reconstructed signal.
     */
    public double[] idct(double[] x) {
        return idct(x, false);
    }

    /**
     * Compute the 1D Inverse Discrete Cosine Transform (Type-II) with optional
     * orthogonality normalization.
     *
     * @param x     The input DCT coefficients.
     * @param ortho If true, applies orthogonality normalization (matches scipy
     *              norm='ortho').
     * @return The reconstructed signal.
     */
    public double[] idct(double[] x, boolean ortho) {
        int N = x.length;
        if (N == 0)
            return new double[0];

        // Algorithm for IDCT-II (which is DCT-III).
        // Reverse of the Forward DCT-II steps:
        // 1. Pre-process coefficients
        // 2. Compute IFFT
        // 3. Reorder output

        // Algorithm for IDCT-II based on inverting the Makhoul Forward DCT-II.
        // Forward:
        // 1. v[n] = x[2n], v[N-1-n] = x[2n+1]
        // 2. V = FFT(v)
        // 3. X[k] = 2 * Re( V[k] * exp(-j*theta_k) ) with theta_k = k*pi/(2N)

        // Inverse:
        // 1. Reconstruct V[k] from X[k].
        // Using property that V is Hermitian symmetric (v is real).
        // V[k] = a + jb.
        // We derived:
        // a = 0.5 * ( cos(theta)*X[k] + sin(theta)*X[N-k] )
        // b = 0.5 * ( sin(theta)*X[k] - cos(theta)*X[N-k] )
        // (Note: verify sign of theta used in derivation.
        // Forward uses exp(-j*theta). My derivation used W = exp(-j*theta) = c - js.
        // Wait, derivation used W = c - js?
        // Let's re-verify:
        // exp(-j theta) = cos(theta) - j sin(theta).
        // So W = c - js.
        // V*W = (a+jb)(c-js) = (ac+bs) + j(bc-as).
        // X[k] = 2*Re = 2(ac+bs). Matches derivation.
        // So derivation holds.

        JComplex[] V = new JComplex[N];

        // If ortho, first undo the forward ortho scaling
        double[] X_in = x.clone();
        if (ortho) {
            double s0 = Math.sqrt(4.0 * N); // Inverse of sqrt(1/4N)
            double sk = Math.sqrt(2.0 * N); // Inverse of sqrt(1/2N)
            X_in[0] *= s0;
            for (int k = 1; k < N; k++) {
                X_in[k] *= sk;
            }
        }

        // Handle k=0 separately
        // theta=0 => c=1, s=0.
        // a = 0.5 * ( X[0] )
        // b = 0.5 * ( 0 - X[N] ) ?? X[N] doesn't exist.
        // But for k=0, V[0] is real sum. Imag part is 0.
        // X[0] = 2 * Re(V[0] * 1) = 2*V[0].
        // So V[0] = X[0] / 2.
        V[0] = new JComplex(X_in[0] / 2.0, 0);

        // Handle loop 1 to N/2
        for (int k = 1; k <= N / 2; k++) {
            // We pair k with N-k.
            // If N is even, N/2 is paired with itself? No, N - N/2 = N/2.
            // So for N/2, we need X[N/2].
            // use generic formula. X[N-k] is the same index.

            // Just use the formula.
            // Be careful with index N-k. If k goes up to N-1, we need X[N-k].
            // X is array of size N. valid indices 0..N-1.
            // X[N] is out of bounds.
            // Ideally we need X to be logically defined?
            // Wait, DCT coefficients X are defined for k=0..N-1.
            // But mathematically X[N] or X[N-k] relations?
            // The formula derived relied on V[N-k].
            // In DCT, X is defined up to N-1.
            // But does X[N-k] exist in input?
            // Yes, user provides N coeffs.
            // So for k=1, we use X[1] and X[N-1].

            int k_conj = N - k;

            if (k > k_conj)
                break; // Optimization, don't double compute

            double theta = Math.PI * k / (2.0 * N);
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            double val_k = X_in[k];
            double val_nk = (k_conj == N) ? 0 : X_in[k_conj]; // k_conj logic check

            // Calculate a and b for V[k]
            double a = 0.5 * (c * val_k + s * val_nk);
            double b = 0.5 * (s * val_k - c * val_nk);

            V[k] = new JComplex(a, b);

            // Set V[N-k] = V[k]* for Hermitian symmetry
            if (k_conj < N) {
                V[k_conj] = new JComplex(a, -b);
            }
        }

        // 2. Compute IFFT
        // Our IFFT implementation expects input array.
        // Note: Our FFT.ifft returns unnormalized sum? No, it usually divides by N.
        // Let's verify our FFT.java ifft implementation or convention.
        // SciPy ifft divides by N.
        FFT fft = new FFT();
        JComplex[] v_complex = fft.ifft(V);

        // 3. Extract real part and reorder to x
        double[] output = new double[N];

        // Scale issue:
        // Forward: V = FFT(v). X ~ V.
        // Inverse: V_rec = recovered V. v_rec = IFFT(V_rec).
        // Since V_rec matches V, and IFFT is inverse of FFT, v_rec should match v
        // exactly.
        // No extra scaling needed if IFFT handles 1/N.

        for (int k = 0; k < N; k++) {
            // Check if our IFFT result is real (should be within precision)
            // v[k] corresponds to v_complex[k].real
            // Reordering: v[n] = x[2n], v[N-1-n] = x[2n+1]
            // We iterate n to fill x.

            // We can iterate n from 0 to N/2 and fill x pairs
            if (k < (N + 1) / 2) {
                // k corresponds to n
                // v[n] is v_complex[n]
                // x[2n] = v[n]
                output[2 * k] = v_complex[k].real;

                // second part
                if (2 * k + 1 < N) {
                    // v[N-1-n] maps to x[2n+1]
                    // index in v is N - 1 - k
                    output[2 * k + 1] = v_complex[N - 1 - k].real;
                }
            }
        }

        return output;
    }
}
