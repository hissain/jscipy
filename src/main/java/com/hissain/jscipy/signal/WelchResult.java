package com.hissain.jscipy.signal;

/**
 * Holds the result of Welch's method for spectral density estimation.
 */
public class WelchResult {
    /**
     * Array of sample frequencies.
     */
    public final double[] f;
    /**
     * Power Spectral Density or power spectrum of x.
     */
    public final double[] Pxx;
    
    /**
     * Constructs a WelchResult.
     * @param f Array of sample frequencies.
     * @param Pxx Power Spectral Density or power spectrum.
     */
    public WelchResult(double[] f, double[] Pxx) {
        this.f = f;
        this.Pxx = Pxx;
    }
}
