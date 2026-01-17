package com.hissain.jscipy;

import com.hissain.jscipy.math.Interpolation;
import com.hissain.jscipy.math.Resample;

/**
 * A facade class providing static utility methods for mathematical operations.
 * This class delegates to specific implementations within the library.
 */
public class Math {

    // --- Resample ---

    /**
     * Resamples x to num samples using Fourier method along the given axis.
     *
     * @param x   The input signal.
     * @param num The number of samples in the resampled signal.
     * @return The resampled signal.
     */
    public static double[] resample(double[] x, int num) {
        return new Resample().resample(x, num);
    }

    // --- Interpolation ---

    /**
     * Performs linear interpolation on the given data points.
     *
     * @param x    The x-coordinates of the data points.
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates at which to evaluate the interpolated values.
     * @return The interpolated values.
     */
    public static double[] interp1d_linear(double[] x, double[] y, double[] newX) {
        return new Interpolation().linear(x, y, newX);
    }

    /**
     * Performs cubic spline interpolation on the given data points.
     *
     * @param x    The x-coordinates of the data points.
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates at which to evaluate the interpolated values.
     * @return The interpolated values.
     */
    public static double[] interp1d_cubic(double[] x, double[] y, double[] newX) {
        return new Interpolation().cubic(x, y, newX);
    }
}
