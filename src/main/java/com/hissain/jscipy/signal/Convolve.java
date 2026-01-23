package com.hissain.jscipy.signal;

/**
 * Utility class for signal convolution.
 * Provides 1D and 2D convolution operations with various boundary modes.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/convolve/convolve_comparison_light.png"
 * alt="Convolve Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 * <br>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/convolve2d/convolve2d_comparison_full_light.png"
 * alt="Convolve2D Comparison Full" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 * <br>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/convolve2d/convolve2d_comparison_same_light.png"
 * alt="Convolve2D Comparison Same" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 * <br>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/convolve2d/convolve2d_comparison_valid_light.png"
 * alt="Convolve2D Comparison Valid" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 */
public class Convolve {

    /**
     * Convolves two 1D signals.
     *
     * @param signal The input signal.
     * @param window The kernel/window to convolve with.
     * @param mode   The convolution mode (FULL, SAME, VALID).
     * @return The convolved signal.
     */
    public double[] convolve(double[] signal, double[] window, ConvolutionMode mode) {
        int n = signal.length;
        int m = window.length;
        int resultLen;

        switch (mode) {
            case FULL:
                resultLen = n + m - 1;
                break;
            case VALID:
                resultLen = Math.max(n, m) - Math.min(n, m) + 1;
                break;
            case SAME:
            default:
                resultLen = Math.max(n, m);
                break;
        }

        double[] result = new double[resultLen];

        // For 'same' mode, we need to determine the starting offset in the full
        // convolution
        // effectively centering the result.
        // Full convolution ranges from k=0 to n+m-2.
        // Same mode centers around the original signal center.

        // Let's implement full convolution first, then slice? No, let's do direct
        // indexing.
        // y[k] = sum_j (signal[j] * window[k-j])

        // Offset shift for result index k to mapped full-convolution index
        int kShift = 0;
        if (mode == ConvolutionMode.SAME) {
            kShift = (m - 1) / 2;
        } else if (mode == ConvolutionMode.VALID) {
            kShift = m - 1;
        }

        for (int i = 0; i < resultLen; i++) {
            // k is the index in the "full" convolution that corresponds to result[i]
            int k = i + kShift;

            double sum = 0;
            // We want j such that 0 <= j < n AND 0 <= k-j < m
            // => j >= 0
            // => j < n
            // => k-j >= 0 => j <= k
            // => k-j < m => j > k - m

            int startJ = Math.max(0, k - m + 1);
            int endJ = Math.min(n - 1, k);

            for (int j = startJ; j <= endJ; j++) {
                sum += signal[j] * window[k - j];
            }
            result[i] = sum;
        }
        return result;
    }

    /**
     * Convolves two 2D signals (matrices).
     *
     * @param in1  The first input matrix.
     * @param in2  The second input matrix (kernel).
     * @param mode The convolution mode (FULL, SAME, VALID).
     * @return The convolved matrix.
     */
    public double[][] convolve2d(double[][] in1, double[][] in2, ConvolutionMode mode) {
        int r1 = in1.length;
        int c1 = in1[0].length;
        int r2 = in2.length;
        int c2 = in2[0].length;

        int rows, cols;
        switch (mode) {
            case VALID:
                rows = Math.abs(r1 - r2) + 1;
                cols = Math.abs(c1 - c2) + 1;
                break;
            case SAME:
                rows = r1;
                cols = c1;
                break;
            case FULL:
            default:
                rows = r1 + r2 - 1;
                cols = c1 + c2 - 1;
                break;
        }

        double[][] output = new double[rows][cols];

        // Compute convolution y[i,j] = sum_m sum_n x[m,n] * h[i-m, j-n]
        // This implicitly assumes 0-padding beyond boundaries.

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;

                int yr = i;
                int yc = j;

                // Map output coordinate (i,j) to convolution indices.
                if (mode == ConvolutionMode.SAME) {
                    yr += (r2 - 1) / 2;
                    yc += (c2 - 1) / 2;
                } else if (mode == ConvolutionMode.VALID) {
                    yr += r2 - 1;
                    yc += c2 - 1;
                }
                // Compute convolution using:
                // y[yr, yc] = sum_m sum_n in1[m, n] * in2[yr - m, yc - n]

                for (int m = 0; m < r1; m++) {
                    int h_r = yr - m; // Index in in2
                    if (h_r >= 0 && h_r < r2) {
                        for (int n = 0; n < c1; n++) {
                            int h_c = yc - n; // Index in in2
                            if (h_c >= 0 && h_c < c2) {
                                sum += in1[m][n] * in2[h_r][h_c];
                            }
                        }
                    }
                }
                output[i][j] = sum;
            }
        }
        return output;
    }
}
