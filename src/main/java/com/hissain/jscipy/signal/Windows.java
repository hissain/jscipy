package com.hissain.jscipy.signal;

/**
 * Collection of window functions.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/windows/windows_comparison_light.png"
 * alt="Windows Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
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

    /**
     * Returns a symmetric Bartlett window of length M.
     *
     * @param m The length of the window.
     * @return The Bartlett window.
     */
    public static double[] bartlett(int m) {
        return bartlett(m, true);
    }

    /**
     * Returns a Bartlett window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Bartlett window.
     */
    public static double[] bartlett(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        for (int n = 0; n < m; n++) {
            w[n] = 1.0 - Math.abs((2.0 * n - div) / div);
        }
        return w;
    }

    /**
     * Returns a symmetric Triangular window of length M.
     *
     * @param m The length of the window.
     * @return The Triangular window.
     */
    public static double[] triang(int m) {
        return triang(m, true);
    }

    /**
     * Returns a Triangular window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window (for filter design).
     *                  If false, generates a periodic window (for spectral
     *                  analysis).
     * @return The Triangular window.
     */
    public static double[] triang(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }

        // Let's use the explicit geometric construct which is robust
        if (symmetric) {
            double lens = m % 2 == 0 ? m : m + 1;
            for (int n = 0; n < m; n++) {
                w[n] = 1.0 - Math.abs((n - (m - 1) / 2.0) / (lens / 2.0));
            }
        } else {
            // Periodic: same as symmetric(M+1) without last point.
            double lens = (m + 1) % 2 == 0 ? (m + 1) : (m + 1) + 1;
            for (int n = 0; n < m; n++) {
                w[n] = 1.0 - Math.abs((n - ((m + 1) - 1) / 2.0) / (lens / 2.0));
            }
        }
        return w;
    }

    /**
     * Returns a symmetric Flat top window of length M.
     *
     * @param m The length of the window.
     * @return The Flat top window.
     */
    public static double[] flattop(int m) {
        return flattop(m, true);
    }

    /**
     * Returns a Flat top window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window.
     * @return The Flat top window.
     */
    public static double[] flattop(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        // Coefficients from SciPy/literature
        double a0 = 0.21557895;
        double a1 = 0.41663158;
        double a2 = 0.277263158;
        double a3 = 0.083578947;
        double a4 = 0.006947368;

        for (int n = 0; n < m; n++) {
            double z = 2.0 * Math.PI * n / div;
            w[n] = a0 - a1 * Math.cos(z) + a2 * Math.cos(2 * z) - a3 * Math.cos(3 * z) + a4 * Math.cos(4 * z);
        }
        return w;
    }

    /**
     * Returns a symmetric Parzen window of length M.
     *
     * @param m The length of the window.
     * @return The Parzen window.
     */
    public static double[] parzen(int m) {
        return parzen(m, true);
    }

    /**
     * Returns a Parzen window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window.
     * @return The Parzen window.
     */
    public static double[] parzen(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }

        // Let's assume standard formula verified by tests:
        double N = symmetric ? m : m + 1; // Effective length for periodicity

        for (int n = 0; n < m; n++) {
            double k = n - (N - 1) / 2.0;
            double abs_k = Math.abs(k);
            double val = 0;
            double L_half = N / 2.0;

            if (abs_k <= L_half / 2.0) {
                double t = abs_k / L_half;
                val = 1.0 - 6.0 * t * t + 6.0 * t * t * t;
            } else if (abs_k <= L_half) {
                double t = abs_k / L_half;
                val = 2.0 * Math.pow(1.0 - t, 3);
            } else {
                val = 0.0;
            }
            w[n] = val;
        }
        return w;
    }

    /**
     * Returns a symmetric Bohman window of length M.
     *
     * @param m The length of the window.
     * @return The Bohman window.
     */
    public static double[] bohman(int m) {
        return bohman(m, true);
    }

    /**
     * Returns a Bohman window of length M.
     *
     * @param m         The length of the window.
     * @param symmetric If true, generates a symmetric window.
     * @return The Bohman window.
     */
    public static double[] bohman(int m, boolean symmetric) {
        double[] w = new double[m];
        if (m == 1) {
            w[0] = 1.0;
            return w;
        }
        double div = symmetric ? (m - 1) : m;
        for (int n = 0; n < m; n++) {
            double x = Math.abs(2.0 * n / div - 1.0);
            // (1 - x) * cos(pi * x) + (1/pi) * sin(pi * x)
            // Defined on [-1, 1], so x is in [0, 1] because of abs.
            // Boundary conditions: at x=1 (edges), value is 0. at x=0 (center), value is 1.
            if (x >= 1.0) {
                w[n] = 0.0;
            } else {
                w[n] = (1.0 - x) * Math.cos(Math.PI * x) + (1.0 / Math.PI) * Math.sin(Math.PI * x);
            }
        }
        return w;
    }
}
