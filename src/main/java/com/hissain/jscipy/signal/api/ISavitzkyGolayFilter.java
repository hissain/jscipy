package com.hissain.jscipy.signal.api;

public interface ISavitzkyGolayFilter {
    /**
     * Smooth the data using a Savitzky-Golay filter.
     * @param data The input data array.
     * @param windowLength The length of the filter window (must be a positive odd integer).
     * @param polyOrder The order of the polynomial used to fit the samples (must be less than windowLength).
     * @return The smoothed data.
     */
    double[] smooth(double[] data, int windowLength, int polyOrder);

    /**
     * Calculate the derivative of the data using a Savitzky-Golay filter.
     * @param data The input data array.
     * @param windowLength The length of the filter window (must be a positive odd integer).
     * @param polyOrder The order of the polynomial used to fit the samples (must be less than windowLength).
     * @param deriv The order of the derivative to compute (default is 0, which means smoothing).
     * @param delta The spacing of the samples to which the data will be applied (default is 1.0).
     * @return The derivative of the data.
     */
    double[] differentiate(double[] data, int windowLength, int polyOrder, int deriv, double delta);
}
