package com.hissain.jscipy.signal.api;

public interface IMedFilt {
    /**
     * Applies a median filter to the input signal.
     * @param data The input signal.
     * @param kernelSize The size of the median filter window (must be odd).
     * @return The filtered signal.
     */
    double[] medfilt(double[] data, int kernelSize);
}
