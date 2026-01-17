package com.hissain.jscipy.signal;

/**
 * Utility class for signal convolution.
 * Provides 1D and 2D convolution operations with various boundary modes.
 */
public class Convolve {

    /**
     * Convolves two 1D signals.
     *
     * @param signal The input signal.
     * @param window The kernel/window to convolve with.
     * @param mode   The convolution mode (only SAME is currently supported).
     * @return The convolved signal.
     * @throws UnsupportedOperationException if mode is not SAME.
     */
    public double[] convolve(double[] signal, double[] window, ConvolutionMode mode) {
        if (mode != ConvolutionMode.SAME) {
            throw new UnsupportedOperationException("Only 'same' mode is supported for now.");
        }
        int signalLen = signal.length;
        int windowLen = window.length;
        double[] result = new double[signalLen];
        int windowCenter = windowLen / 2;
        double windowSum = 0;
        for (double v : window) {
            windowSum += v;
        }

        for (int i = 0; i < signalLen; i++) {
            double sum = 0;
            for (int j = 0; j < windowLen; j++) {
                int signalIndex = i - windowCenter + j;
                if (signalIndex >= 0 && signalIndex < signalLen) {
                    sum += signal[signalIndex] * window[j];
                }
            }
            result[i] = sum / windowSum;
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
