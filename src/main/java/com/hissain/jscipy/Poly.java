package com.hissain.jscipy;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 * Polynomial functions matching NumPy/SciPy behavior.
 */
public class Poly {

    /**
     * Least squares polynomial fit.
     * Fits a polynomial p(x) = p[0] * x**deg + ... + p[deg] of degree deg to points
     * (x, y).
     * Returns a vector of coefficients p that minimises the squared error.
     *
     * @param x      x-coordinates of the sample points (M sample points).
     * @param y      y-coordinates of the sample points (M sample points).
     * @param degree Degree of the fitting polynomial.
     * @return Polynomial coefficients, highest power first.
     */
    public static double[] polyfit(double[] x, double[] y, int degree) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y must have the same length");
        }
        if (x.length == 0) {
            throw new IllegalArgumentException("Input arrays cannot be empty");
        }

        // Prepare points for the fitter
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < x.length; i++) {
            obs.add(x[i], y[i]);
        }

        // Use standard polynomial fitter
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
        double[] coeffsLowToHigh = fitter.fit(obs.toList());

        // Reverse coefficients because numpy.polyfit returns highest power first,
        // but commons-math returns lowest power first (a0 + a1*x + ...).
        return reverse(coeffsLowToHigh);
    }

    /**
     * Evaluate a polynomial at specific values.
     *
     * @param p Polynomial coefficients (highest degree first).
     * @param x Points at which to evaluate.
     * @return Evaluated values.
     */
    public static double[] polyval(double[] p, double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = polyval(p, x[i]);
        }
        return y;
    }

    /**
     * Compute the derivative of a polynomial.
     *
     * @param p Polynomial coefficients (highest degree first).
     * @return Derivative polynomial coefficients.
     */
    public static double[] polyder(double[] p) {
        if (p.length < 2) {
            return new double[] { 0.0 };
        }
        double[] deriv = new double[p.length - 1];
        int n = p.length - 1;
        for (int i = 0; i < n; i++) {
            deriv[i] = p[i] * (n - i);
        }
        return deriv;
    }

    /**
     * Evaluate a polynomial at a specific value using Horner's method.
     *
     * @param p Polynomial coefficients (highest degree first).
     * @param x Point at which to evaluate.
     * @return Evaluated value.
     */
    public static double polyval(double[] p, double x) {
        double res = 0;
        for (double c : p) {
            res = res * x + c;
        }
        return res;
    }

    // Helper to reverse array
    private static double[] reverse(double[] arr) {
        double[] reversed = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            reversed[i] = arr[arr.length - 1 - i];
        }
        return reversed;
    }
}
