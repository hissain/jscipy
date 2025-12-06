package com.hissain.jscipy.signal;

/**
 * Utility class for window functions.
 */
public class Windows {
    
    /**
     * Returns a Hanning window of length M.
     * The window is symmetric.
     *
     * @param m The length of the window.
     * @return The Hanning window.
     */
    public static double[] hanning(int m) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        for (int n = 0; n < m; n++) {
            w[n] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * n / (m - 1));
        }
        return w;
    }
}
