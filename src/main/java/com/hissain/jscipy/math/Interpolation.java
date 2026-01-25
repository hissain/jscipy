package com.hissain.jscipy.math;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.analysis.UnivariateFunction;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;

import java.util.Arrays;

/**
 * Helper class for 1D interpolation operations.
 * Supports Linear, Quadratic, Cubic and general B-Spline interpolation.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/interpolation/interpolation_comparison_1_light.png"
 * alt="Interpolation Comparison 1" style="width: 600px; max-width: 90%;
 * display: block; margin: 0 auto;">
 * <br>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/interpolation/interpolation_comparison_2_light.png"
 * alt="Interpolation Comparison 2" style="width: 600px; max-width: 90%;
 * display: block; margin: 0 auto;">
 */
public class Interpolation {

    /**
     * Performs linear interpolation.
     *
     * @param x    Known x-coordinates (must be sorted).
     * @param y    Known y-coordinates corresponding to x.
     * @param newX New x-coordinates to evaluate.
     * @return Interpolated y-values at newX.
     * @throws IllegalArgumentException if x and y lengths differ or are
     *                                  insufficient.
     */
    public double[] linear(double[] x, double[] y, double[] newX) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y must have the same length");
        }
        if (x.length < 2) {
            throw new IllegalArgumentException("x and y must have at least 2 points");
        }

        double[] newY = new double[newX.length];
        for (int i = 0; i < newX.length; i++) {
            newY[i] = linear(x, y, newX[i]);
        }
        return newY;
    }

    /**
     * Performs cubic spline interpolation.
     *
     * @param x    Known x-coordinates (must be sorted).
     * @param y    Known y-coordinates corresponding to x.
     * @param newX New x-coordinates to evaluate.
     * @return Interpolated y-values at newX.
     * @throws IllegalArgumentException if x and y lengths differ or are
     *                                  insufficient.
     */
    public double[] cubic(double[] x, double[] y, double[] newX) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y must have the same length");
        }
        if (x.length < 3) {
            throw new IllegalArgumentException("x and y must have at least 3 points for cubic interpolation");
        }

        SplineInterpolator interpolator = new SplineInterpolator();
        PolynomialSplineFunction splineFunction = interpolator.interpolate(x, y);

        double[] newY = new double[newX.length];
        for (int i = 0; i < newX.length; i++) {
            newY[i] = splineFunction.value(newX[i]);
        }
        return newY;
    }

    /**
     * Performs quadratic spline interpolation.
     * <p>
     * Replicates SciPy's `interp1d(kind='quadratic')` implementation using
     * B-splines
     * with clamped boundary conditions and internal knots at midpoints.
     * </p>
     *
     * @param x    The x-coordinates of the data points (must be sorted).
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates to evaluate.
     * @return The interpolated values.
     */
    public double[] quadratic(double[] x, double[] y, double[] newX) {
        return bspline(x, y, newX, 2);
    }

    /**
     * Performs B-spline interpolation of degree k.
     *
     * @param x    The x-coordinates of the data points (must be sorted).
     * @param y    The y-coordinates of the data points.
     * @param newX The x-coordinates to evaluate.
     * @param k    The degree of the B-spline.
     * @return The interpolated values.
     */
    public double[] bspline(double[] x, double[] y, double[] newX, int k) {
        BSplineInterpolator interpolator = new BSplineInterpolator(k);
        try {
            org.apache.commons.math3.analysis.UnivariateFunction function = interpolator.interpolate(x, y);
            double[] newY = new double[newX.length];
            for (int i = 0; i < newX.length; i++) {
                newY[i] = function.value(newX[i]);
            }
            return newY;
        } catch (org.apache.commons.math3.exception.MathIllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private double linear(double[] x, double[] y, double newX) {
        int i = findInsertionPoint(x, newX);

        if (i == 0) {
            return y[0];
        }
        if (i == x.length) {
            return y[x.length - 1];
        }

        double x1 = x[i - 1];
        double y1 = y[i - 1];
        double x2 = x[i];
        double y2 = y[i];

        return y1 + (y2 - y1) * (newX - x1) / (x2 - x1);
    }

    private int findInsertionPoint(double[] arr, double val) {
        int i = Arrays.binarySearch(arr, val);
        if (i < 0) {
            i = -i - 1;
        }
        return i;
    }

    private static class BSplineInterpolator {

        private final int k;

        public BSplineInterpolator(int k) {
            this.k = k;
        }

        public UnivariateFunction interpolate(final double[] x, final double[] y)
                throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {

            if (x.length != y.length) {
                throw new DimensionMismatchException(x.length, y.length);
            }

            if (x.length < k + 1) {
                throw new NumberIsTooSmallException(x.length, k + 1, true);
            }

            MathArrays.checkOrder(x);

            final int n = x.length - 1; // Number of segments
            // Number of points = n + 1
            // Degree k

            // 1. Knot Generation
            // Total knots = (n + 1) + k + 1 = n + k + 2

            double[] t = new double[n + k + 2];

            // Start Clamped: k+1 times x[0]
            for (int i = 0; i <= k; i++) {
                t[i] = x[0];
            }

            // End Clamped: k+1 times x[n]
            for (int i = 0; i <= k; i++) {
                t[t.length - 1 - i] = x[n];
            }

            // Internal Knots
            // We need n - k internal knots.
            // If k is even: midpoints.
            // If k is odd: data points.

            int internalKnots = n - k;
            if (internalKnots > 0) {
                if (k % 2 == 0) {
                    // Even degree: Midpoints
                    // For k=2, start=1.
                    // General start index in x logic: k/2
                    int startIdx = k / 2;
                    for (int i = 0; i < internalKnots; i++) {
                        int idx = startIdx + i;
                        t[k + 1 + i] = (x[idx] + x[idx + 1]) / 2.0;
                    }
                } else {
                    // Odd degree: Data points
                    // For k=1: internal knots x[1]...x[n-1]. (n-1 points). Start idx 1.
                    // For k=3: internal knots x[2]...x[n-2]. (n-3 points). Start idx 2.
                    // General start index: (k+1)/2.
                    int startIdx = (k + 1) / 2;
                    for (int i = 0; i < internalKnots; i++) {
                        int idx = startIdx + i;
                        t[k + 1 + i] = x[idx];
                    }
                }
            }

            // 2. Build Linear System to solve for Control Points c
            // Matrix A size (n+1) x (n+1)
            // A[i][j] = B_{j, k}(x[i])
            // We have n+1 basis functions.

            int dim = n + 1;
            double[][] matrixData = new double[dim][dim];

            for (int i = 0; i < dim; i++) {
                double xi = x[i];
                for (int j = 0; j < dim; j++) {
                    matrixData[i][j] = bSplineBasis(j, k, t, xi);
                }
            }

            RealMatrix A = new Array2DRowRealMatrix(matrixData);
            DecompositionSolver solver = new LUDecomposition(A).getSolver();
            RealVector Y = new ArrayRealVector(y);
            RealVector C = solver.solve(Y);

            final double[] c = C.toArray();
            final double[] knots = t;

            return new UnivariateFunction() {
                @Override
                public double value(double v) {
                    return evaluateBSpline(v, k, knots, c);
                }
            };
        }

        // Cox-de Boor algorithm for Basis Function Value
        private double bSplineBasis(int i, int k, double[] t, double x) {
            if (k == 0) {
                // Basis function of degree 0 is 1 if t[i] <= x < t[i+1], else 0
                // Special case for last interval to include endpoint
                if (t[i + 1] < t[t.length - 1]) {
                    return (x >= t[i] && x < t[i + 1]) ? 1.0 : 0.0;
                } else {
                    // Last interval includes the right endpoint
                    return (x >= t[i] && x <= t[i + 1]) ? 1.0 : 0.0;
                }
            }

            double term1 = 0;
            if (t[i + k] != t[i]) {
                term1 = ((x - t[i]) / (t[i + k] - t[i])) * bSplineBasis(i, k - 1, t, x);
            }

            double term2 = 0;
            if (t[i + k + 1] != t[i + 1]) {
                term2 = ((t[i + k + 1] - x) / (t[i + k + 1] - t[i + 1])) * bSplineBasis(i + 1, k - 1, t, x);
            }

            return term1 + term2;
        }

        private double evaluateBSpline(double x, int k, double[] t, double[] c) {
            double sum = 0;
            for (int i = 0; i < c.length; i++) {
                sum += c[i] * bSplineBasis(i, k, t, x);
            }
            return sum;
        }
    }
}
