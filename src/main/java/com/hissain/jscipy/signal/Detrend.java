package com.hissain.jscipy.signal;

/**
 * Implementation for removing trends from signals.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/detrend/detrend_comparison_1_light.png"
 * alt="Detrend Comparison 1" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 * <br>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/detrend/detrend_comparison_2_light.png"
 * alt="Detrend Comparison 2" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 */
public class Detrend {

    public double[] detrend(double[] signal, DetrendType type) {
        if (signal == null) {
            throw new NullPointerException("Signal cannot be null");
        }
        if (signal.length == 0) {
            return new double[0];
        }

        if (type == DetrendType.CONSTANT) {
            return detrendConstant(signal);
        } else if (type == DetrendType.LINEAR) {
            return detrendLinear(signal);
        } else {
            // Should theoretically not happen if Enum is used, unless null is passed.
            if (type == null) {
                throw new IllegalArgumentException("Detrend type cannot be null.");
            }
            throw new IllegalArgumentException("Unsupported detrend type: " + type);
        }
    }

    public double[] detrend(double[] signal) {
        return detrend(signal, DetrendType.LINEAR);
    }

    private double[] detrendConstant(double[] signal) {
        double sum = 0;
        for (double v : signal) {
            sum += v;
        }
        double mean = sum / signal.length;

        double[] result = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            result[i] = signal[i] - mean;
        }
        return result;
    }

    private double[] detrendLinear(double[] signal) {
        int n = signal.length;
        if (n < 2) {
            return new double[n]; // Cannot fit a line with less than 2 points (or result is 0s)
            // Scipy behavior for 1 point? It probably returns 0.
            // Let's assume standard least squares works or we handle it.
            // If n=1, mean is the point, so constant detrend returns 0. Linear also 0.
        }

        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = signal[i];
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = signal[i] - (slope * i + intercept);
        }
        return result;
    }
}
