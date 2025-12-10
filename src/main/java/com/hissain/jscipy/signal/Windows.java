package com.hissain.jscipy.signal;

/**
 * Utility class for window functions.
 */
public class Windows {

    /**
     * Returns a symmetric Hanning window of length M.
     *
     * @param m The length of the window.
     * @return The Hanning window.
     */
    public static double[] hanning(int m) {
        return hanning(m, true);
    }

    /**
     * Returns a Hanning window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Hanning window.
     */
    public static double[] hanning(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        for (int n = 0; n < m; n++) {
            w[n] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * n / div);
        }
        return w;
    }

    /**
     * Returns a symmetric Hamming window of length M.
     *
     * @param m The length of the window.
     * @return The Hamming window.
     */
    public static double[] hamming(int m) {
        return hamming(m, true);
    }

    /**
     * Returns a Hamming window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Hamming window.
     */
    public static double[] hamming(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        for (int n = 0; n < m; n++) {
            w[n] = 0.54 - 0.46 * Math.cos(2.0 * Math.PI * n / div);
        }
        return w;
    }

    /**
     * Returns a symmetric Blackman window of length M.
     *
     * @param m The length of the window.
     * @return The Blackman window.
     */
    public static double[] blackman(int m) {
        return blackman(m, true);
    }

    /**
     * Returns a Blackman window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Blackman window.
     */
    public static double[] blackman(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        for (int n = 0; n < m; n++) {
            w[n] = 0.42 - 0.5 * Math.cos(2.0 * Math.PI * n / div) + 0.08 * Math.cos(4.0 * Math.PI * n / div);
        }
        return w;
    }

    /**
     * Returns a symmetric Kaiser window of length M with shape parameter beta.
     *
     * @param m    The length of the window.
     * @param beta The shape parameter.
     * @return The Kaiser window.
     */
    public static double[] kaiser(int m, double beta) {
        return kaiser(m, beta, true);
    }

    /**
     * Returns a Kaiser window of length M with shape parameter beta.
     *
     * @param m         The length of the window.
     * @param beta      The shape parameter.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Kaiser window.
     */
    public static double[] kaiser(int m, double beta, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        double denom = i0(beta);

        for (int n = 0; n < m; n++) {
            double k = 2.0 * n / div - 1.0;
            double arg = Math.sqrt(Math.max(0, 1 - k * k));
            w[n] = i0(beta * arg) / denom;
        }
        return w;
    }

    /**
     * Computes the modified Bessel function of the first kind, zeroth order I0(x).
     *
     * @param x The input value.
     * @return The value of I0(x).
     */
    private static double i0(double x) {
        double sum = 1.0;
        double term = 1.0;
        double x2 = x * x / 4.0;
        int k = 1;

        // Series expansion: sum_{k=0}^{infinity} ( (x/2)^k / k! )^2
        // We stop when the term becomes negligible compared to the sum.
        while (true) {
            term *= x2 / (k * k);
            if (term < 1e-12 * sum) {
                break;
            }
            sum += term;
            k++;
        }
        return sum;
    }
}
