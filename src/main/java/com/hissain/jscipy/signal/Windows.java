package com.hissain.jscipy.signal;

/**
 * Utility class for window functions.
 */
class Windows {
    
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
     * @param m The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral analysis).
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
}
