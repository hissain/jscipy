package com.hissain.jscipy.signal;

import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;

class Resample {

    private final FFT fft;

    public Resample() {
        this.fft = new FFT();
    }

    public double[] resample(double[] signal, int num) {
        int len = signal.length;
        if (len == 0) {
            return new double[0];
        }

        // 1. Get the RFFT spectrum
        // FFT.rfft returns a Complex array of length N/2 + 1, where N is input signal length.
        Complex[] rfftSpectrum = fft.rfft(signal);

        // 2. Create new RFFT spectrum of target length, initialized to zeros
        // The new RFFT spectrum will have length num/2 + 1.
        int newRfftLength = num / 2 + 1;
        Complex[] resampledRfftSpectrum = new Complex[newRfftLength];
        Arrays.fill(resampledRfftSpectrum, Complex.ZERO);

        // 3. Spectral Manipulation (copying DC, positive freqs, and handling Nyquist)
        // The simplest RFFT spectral manipulation: copy DC component and all positive frequencies
        // up to the minimum length. Any remaining part of newRfftSpectrum is zero-padded.

        int originalRfftLength = rfftSpectrum.length; // len/2 + 1
        int copyLength = Math.min(originalRfftLength, newRfftLength);
        System.arraycopy(rfftSpectrum, 0, resampledRfftSpectrum, 0, copyLength);

        // 4. Perform Inverse RFFT
        double[] realResult = fft.irfft(resampledRfftSpectrum, num);

        // 5. Apply final scaling
        // SciPy's signal.resample documentation states it scales by num / len so energy is conserved.
        // My FFT.irfft (from user-provided code) returns values scaled by 1/num.
        // SciPy's numpy.fft.irfft (which SciPy uses) returns values scaled by num.
        // So Python irfft output is num*num times larger than Java irfft output.
        // Therefore, Java output is (1/num) * (num/len) = 1/len scaled.
        // Python output is num * (num/len) = num*num/len scaled.
        // So Java output is num*num times smaller than Python.
        double scale = (double) num / len;
        // scale *= num; // Removed: FFT.irfft is now standard normalized (1/N), so simple scaling is sufficient.

        for (int i = 0; i < num; i++) {
            realResult[i] = realResult[i] * scale;
        }

        return realResult;
    }
}
