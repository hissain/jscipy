package com.hissain.jscipy.signal.api;

public interface IConvolve {
    /**
     * Convolves two signals.
     * @param signal The first signal.
     * @param window The second signal (window).
     * @param mode The convolution mode (e.g., ConvolutionMode.SAME).
     * @return The convolved signal.
     */
    double[] convolve(double[] signal, double[] window, ConvolutionMode mode);
}
