package com.hissain.jscipy.signal;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.Arrays;

class Interpolation {

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
}
