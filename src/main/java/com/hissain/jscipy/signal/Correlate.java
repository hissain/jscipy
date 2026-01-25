package com.hissain.jscipy.signal;

/**
 * Utility class for 1D and 2D cross-correlation.
 * <p>
 * Cross-correlation is commonly used in signal processing, image processing,
 * and
 * pattern matching.
 * </p>
 * <h3>1D Cross-Correlation</h3>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/correlate/correlate_comparison_light.png"
 * alt="1D Correlation Comparison" style="width: 600px; max-width: 90%; display:
 * block; margin: 0 auto;">
 * <p>
 * <h3>2D Cross-Correlation</h3>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/correlate2d/correlate2d_comparison_light.png"
 * alt="2D Correlation Comparison" style="width: 600px; max-width: 90%; display:
 * block; margin: 0 auto;">
 *
 * @see Convolve
 */
public class Correlate {

    /**
     * Cross-correlate two 1-dimensional sequences.
     * <p>
     * This function computes the correlation as generally defined in signal
     * processing texts:
     * z[k] = (x * y)[k] = sum_l x[l] * y[l+k]
     * <p>
     * This is equivalent to {@code convolve(in1, reverse(in2), mode)}.
     *
     * @param in1  First input signal.
     * @param in2  Second input signal.
     * @param mode The convolution mode (FULL, SAME, VALID).
     * @return Discrete cross-correlation of in1 and in2.
     */
    public double[] correlate(double[] in1, double[] in2, ConvolutionMode mode) {
        // Reverse in2
        double[] in2Reversed = new double[in2.length];
        for (int i = 0; i < in2.length; i++) {
            in2Reversed[i] = in2[in2.length - 1 - i];
        }
        return new Convolve().convolve(in1, in2Reversed, mode);
    }

    /**
     * Cross-correlate two 2-dimensional arrays.
     * <p>
     * Correlation is mathematically equivalent to convolution with the second array
     * reversed along both axes. This function implements:
     * 
     * <pre>
     * correlate2d(in1, in2) = convolve2d(in1, flip(flip(in2, axis = 0), axis = 1))
     * </pre>
     * <p>
     * The output size depends on the mode parameter:
     * <ul>
     * <li><b>FULL</b>: Output size is (r1+r2-1, c1+c2-1) where r1,c1 are dimensions
     * of in1 and r2,c2 are dimensions of in2. This is the full discrete linear
     * cross-correlation.</li>
     * <li><b>SAME</b>: Output size matches in1 (r1, c1). Returns a centered portion
     * of the full correlation.</li>
     * <li><b>VALID</b>: Output size is (|r1-r2|+1, |c1-c2|+1). Returns only
     * elements
     * where in1 and in2 completely overlap without zero-padding.</li>
     * </ul>
     *
     * @param in1  First input array (image/signal). Must be a 2D array.
     * @param in2  Second input array (kernel/template). Must be a 2D array.
     * @param mode The convolution mode (FULL, SAME, VALID).
     * @return The 2D cross-correlation of in1 and in2.
     * @throws IllegalArgumentException if inputs are null or empty
     */
    public double[][] correlate2d(double[][] in1, double[][] in2, ConvolutionMode mode) {
        if (in1 == null || in1.length == 0 || in1[0].length == 0) {
            throw new IllegalArgumentException("in1 must be a non-empty 2D array");
        }
        if (in2 == null || in2.length == 0 || in2[0].length == 0) {
            throw new IllegalArgumentException("in2 must be a non-empty 2D array");
        }

        // For correlation, we reverse in2 and then use convolution math
        // BUT we need to use correlation-specific offset calculation for SAME mode
        int r1 = in1.length;
        int c1 = in1[0].length;
        int r2 = in2.length;
        int c2 = in2[0].length;

        // Reverse in2 along both axes (flip vertically and horizontally)
        double[][] in2Reversed = new double[r2][c2];
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c2; j++) {
                in2Reversed[i][j] = in2[r2 - 1 - i][c2 - 1 - j];
            }
        }

        // Compute output dimensions
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

        // Compute correlation: y[i,j] = sum_m sum_n in1[m,n] * in2Reversed[i-m, j-n]
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;

                int yr = i;
                int yc = j;

                // For correlation, SAME mode uses different offset than convolution
                // scipy's correlate2d centers at (r2//2, c2//2)
                if (mode == ConvolutionMode.SAME) {
                    // For correlation SAME mode, we shift by r2//2, c2//2
                    yr += r2 / 2;
                    yc += c2 / 2;
                } else if (mode == ConvolutionMode.VALID) {
                    yr += r2 - 1;
                    yc += c2 - 1;
                }

                // Compute: y[yr, yc] = sum_m sum_n in1[m, n] * in2Reversed[yr - m, yc - n]
                for (int m = 0; m < r1; m++) {
                    int h_r = yr - m;
                    if (h_r >= 0 && h_r < r2) {
                        for (int n = 0; n < c1; n++) {
                            int h_c = yc - n;
                            if (h_c >= 0 && h_c < c2) {
                                sum += in1[m][n] * in2Reversed[h_r][h_c];
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
