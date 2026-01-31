package com.hissain.jscipy;

import com.hissain.jscipy.math.Interpolation;
import com.hissain.jscipy.math.Resample;
import com.hissain.jscipy.math.Poly;

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

    /**
     * Performs quadratic spline interpolation on the given data points.
     * Matches SciPy's interp1d(kind='quadratic') using B-splines.
     *
     * @param x    The x-coordinates of the data points.
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates at which to evaluate the interpolated values.
     * @return The interpolated values.
     */
    public static double[] interp1d_quadratic(double[] x, double[] y, double[] newX) {
        return new Interpolation().quadratic(x, y, newX);
    }

    /**
     * Performs B-spline interpolation of arbitrary degree.
     *
     * @param x    The x-coordinates of the data points.
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates at which to evaluate the interpolated values.
     * @param k    The degree of the spline.
     * @return The interpolated values.
     */
    public static double[] interp1d_bspline(double[] x, double[] y, double[] newX, int k) {
        return new Interpolation().bspline(x, y, newX, k);
    }

    // --- Polynomials ---

    /**
     * Least squares polynomial fit.
     * Fits a polynomial p(x) = p[0] * x**deg + ... + p[deg] of degree deg to points
     * (x, y).
     *
     * @param x      x-coordinates of the sample points.
     * @param y      y-coordinates of the sample points.
     * @param degree Degree of the fitting polynomial.
     * @return Polynomial coefficients, highest power first.
     */
    public static double[] polyfit(double[] x, double[] y, int degree) {
        return Poly.polyfit(x, y, degree);
    }

    /**
     * Evaluate a polynomial at specific values.
     *
     * @param p Polynomial coefficients (highest degree first).
     * @param x Points at which to evaluate.
     * @return Evaluated values.
     */
    public static double[] polyval(double[] p, double[] x) {
        return Poly.polyval(p, x);
    }

    /**
     * Evaluate a polynomial at a specific value.
     *
     * @param p Polynomial coefficients (highest degree first).
     * @param x Point at which to evaluate.
     * @return Evaluated value.
     */
    public static double polyval(double[] p, double x) {
        return Poly.polyval(p, x);
    }

    /**
     * Compute the derivative of a polynomial.
     *
     * @param p Polynomial coefficients (highest degree first).
     * @return Derivative polynomial coefficients.
     */
    public static double[] polyder(double[] p) {
        return Poly.polyder(p);
    }
}
