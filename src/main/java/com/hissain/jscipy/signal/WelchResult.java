package com.hissain.jscipy.signal;

/**
 * Result of Welch's method.
 */
public class WelchResult {
    public final double[] f;
    public final double[] Pxx;
    
    public WelchResult(double[] f, double[] Pxx) {
        this.f = f;
        this.Pxx = Pxx;
    }
}
