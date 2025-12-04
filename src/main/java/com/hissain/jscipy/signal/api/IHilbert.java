package com.hissain.jscipy.signal.api;

import org.apache.commons.math3.complex.Complex;

/**
 * {@code IHilbert} defines the public API for the Hilbert transform functionality
 * within the jSciPy library.
 *
 * <p>The Hilbert transform is used to calculate the analytic signal, from which
 * instantaneous amplitude, frequency, and phase can be derived.</p>
 */
public interface IHilbert {

    /**
     * Compute the analytic signal, using the Hilbert transform.
     * <p>
     * The analytic signal x_a(t) of signal x(t) is:
     * x_a = F^{-1}(F(x) 2U) = x + i y
     * where F is the Fourier transform, U the unit step function,
     * and y the Hilbert transform of x.
     * </p>
     *
     * @param signal The input 1D signal as a {@code double} array. Must not be {@code null}.
     * @return A {@code Complex} array representing the analytic signal.
     * @throws NullPointerException if {@code signal} is {@code null}.
     */
    Complex[] hilbert(double[] signal);
}
