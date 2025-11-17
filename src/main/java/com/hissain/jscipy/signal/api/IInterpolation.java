package com.hissain.jscipy.signal.api;

public interface IInterpolation {
    double[] linear(double[] x, double[] y, double[] newX);
    double[] cubic(double[] x, double[] y, double[] newX);
}
