package com.hissain.jscipy.signal.api;

/**
 * {@code IDetrend} defines the public API for detrending functionality within the jSciPy library.
 *
 * <p>Implementations of this interface provide methods to remove linear or constant trends from
 * 1D signals.</p>
 */
public interface IDetrend {

    /**
     * Removes a trend from the signal.
     *
     * @param signal The input 1D signal as a {@code double} array. Must not be {@code null} or empty.
     * @param type The type of detrending. Can be "linear" or "constant".
     * @return A new {@code double} array containing the detrended signal.
     * @throws IllegalArgumentException if {@code type} is invalid.
     * @throws NullPointerException if {@code signal} is {@code null}.
     */
    double[] detrend(double[] signal, String type);

    /**
     * Removes a linear trend from the signal (default behavior).
     *
     * @param signal The input 1D signal as a {@code double} array. Must not be {@code null} or empty.
     * @return A new {@code double} array containing the detrended signal.
     * @throws NullPointerException if {@code signal} is {@code null}.
     */
    double[] detrend(double[] signal);
}
