package com.hissain.jscipy.signal.api;

import org.apache.commons.math3.complex.Complex;

public interface IFFT {
    Complex[] fft(double[] input);
    Complex[] rfft(double[] input);
    Complex[] ifft(Complex[] input);
    double[] irfft(Complex[] input, int n);
}
