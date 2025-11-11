package com.hissain.jscipy.signal.api;

/**
 * {@code IButterworthFilter} defines the public API for Butterworth filter functionality
 * within the jSciPy library.
 *
 * <p>Implementations of this interface provide methods to apply digital Butterworth filters
 * to 1D signals, supporting both forward-only and forward-backward (zero-phase) filtering.</p>
 */
public interface IButterworthFilter {

    /**
     * Applies a zero-phase digital Butterworth filter to a signal using forward and backward passes.
     * This method minimizes phase distortion by filtering the signal once forward and once backward.
     *
     * @param signal The input 1D signal as a {@code double} array. Must not be {@code null} or empty.
     * @param sampleRate The sampling rate of the signal in Hz. Must be positive.
     * @param cutoff The cutoff frequency of the filter in Hz. Must be positive and less than half the sample rate (Nyquist frequency).
     * @param order The order of the Butterworth filter. Must be a positive integer.
     * @return A new {@code double} array containing the filtered signal.
     * @throws IllegalArgumentException if {@code sampleRate}, {@code cutoff}, or {@code order} are invalid.
     * @throws NullPointerException if {@code signal} is {@code null}.
     */
    double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order);

    /**
     * Applies a digital Butterworth filter (forward pass only) to a signal.
     * This method introduces phase distortion. For zero-phase filtering, use {@link #filtfilt(double[], double, double, int)}.
     *
     * @param signal The input 1D signal as a {@code double} array. Must not be {@code null} or empty.
     * @param sampleRate The sampling rate of the signal in Hz. Must be positive.
     * @param cutoff The cutoff frequency of the filter in Hz. Must be positive and less than half the sample rate (Nyquist frequency).
     * @param order The order of the Butterworth filter. Must be a positive integer.
     * @return A new {@code double} array containing the filtered signal.
     * @throws IllegalArgumentException if {@code sampleRate}, {@code cutoff}, or {@code order} are invalid.
     * @throws NullPointerException if {@code signal} is {@code null}.
     */
    double[] filter(double[] signal, double sampleRate, double cutoff, int order);
}
