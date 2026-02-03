package com.hissain.jscipy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.hissain.jscipy.signal.DetrendType;

public class SignalCoverageTest {

    @Test
    public void testFilterFacades() {
        double[] signal = new double[100];
        for (int i = 0; i < 100; i++)
            signal[i] = java.lang.Math.sin(2 * java.lang.Math.PI * i / 20.0);
        double fs = 1000;

        // Butterworth
        assertNotNull(Signal.filtfilt(signal, fs, 100, 4));
        assertNotNull(Signal.lfilter(signal, fs, 100, 4));
        assertNotNull(Signal.filtfilt_highpass(signal, fs, 100, 4));
        assertNotNull(Signal.filtfilt_bandpass(signal, fs, 100, 200, 4));
        assertNotNull(Signal.filtfilt_bandstop(signal, fs, 100, 200, 4));

        // Chebyshev1
        assertNotNull(Signal.cheby1_filtfilt(signal, fs, 100, 4, 0.5));
        assertNotNull(Signal.cheby1_lfilter(signal, fs, 100, 4, 0.5));
        assertNotNull(Signal.cheby1_filtfilt_highpass(signal, fs, 100, 4, 0.5));
        assertNotNull(Signal.cheby1_filtfilt_bandpass(signal, fs, 100, 200, 4, 0.5));
        assertNotNull(Signal.cheby1_filtfilt_bandstop(signal, fs, 100, 200, 4, 0.5));

        // Chebyshev2
        assertNotNull(Signal.cheby2_filtfilt(signal, fs, 100, 4, 40));
        assertNotNull(Signal.cheby2_lfilter(signal, fs, 100, 4, 40));
        assertNotNull(Signal.cheby2_filtfilt_highpass(signal, fs, 100, 4, 40));
        assertNotNull(Signal.cheby2_filtfilt_bandpass(signal, fs, 100, 200, 4, 40));
        assertNotNull(Signal.cheby2_filtfilt_bandstop(signal, fs, 100, 200, 4, 40));

        // Elliptic
        assertNotNull(Signal.ellip_filtfilt(signal, fs, 100, 4, 0.5, 40));
        assertNotNull(Signal.ellip_lfilter(signal, fs, 100, 4, 0.5, 40));
        assertNotNull(Signal.ellip_filtfilt_highpass(signal, fs, 100, 4, 0.5, 40));
        assertNotNull(Signal.ellip_filtfilt_bandpass(signal, fs, 100, 200, 4, 0.5, 40));
        assertNotNull(Signal.ellip_filtfilt_bandstop(signal, fs, 100, 200, 4, 0.5, 40));

        // Bessel
        assertNotNull(Signal.bessel_filtfilt(signal, fs, 100, 4));
        assertNotNull(Signal.bessel_filtfilt_highpass(signal, fs, 100, 4));
        assertNotNull(Signal.bessel_filtfilt_bandpass(signal, fs, 100, 200, 4));
        assertNotNull(Signal.bessel_filtfilt_bandstop(signal, fs, 100, 200, 4));
    }

    @Test
    public void testOtherUtilities() {
        double[] signal = { 1, 2, 3, 4, 5 };

        // Detrend
        assertNotNull(Signal.detrend(signal, DetrendType.LINEAR));

        // Peaks
        assertNotNull(Signal.find_peaks(signal));
        assertNotNull(Signal.find_peaks(signal, 1.0, 1, 0.1));

        // All Window functions that exist
        assertNotNull(Signal.hanning(10));
        assertNotNull(Signal.hamming(10));
        assertNotNull(Signal.blackman(10));
        assertNotNull(Signal.kaiser(10, 2.0));
    }

    @Test
    public void testFFTMethods() {
        double[] signal = new double[64];
        for (int i = 0; i < signal.length; i++) {
            signal[i] = java.lang.Math.sin(2 * java.lang.Math.PI * i / 16.0);
        }

        // FFT and IFFT
        assertNotNull(Signal.fft(signal));
        assertNotNull(Signal.rfft(signal));

        // IFFT with complex input
        com.hissain.jscipy.signal.JComplex[] fftResult = Signal.fft(signal);
        assertNotNull(Signal.ifft(fftResult));

        // IRFFT
        com.hissain.jscipy.signal.JComplex[] rfftResult = Signal.rfft(signal);
        assertNotNull(Signal.irfft(rfftResult, signal.length));

        // 2D FFT
        double[][] signal2d = { { 1, 2 }, { 3, 4 } };
        assertNotNull(Signal.fft2(signal2d));

        com.hissain.jscipy.signal.JComplex[][] fft2Result = Signal.fft2(signal2d);
        assertNotNull(Signal.ifft2(fft2Result));

        // STFT and ISTFT
        double[] longSignal = new double[512];
        for (int i = 0; i < longSignal.length; i++) {
            longSignal[i] = java.lang.Math.sin(2 * java.lang.Math.PI * i / 32.0);
        }
        assertNotNull(Signal.stft(longSignal));

        com.hissain.jscipy.signal.JComplex[][] stftResult = Signal.stft(longSignal);
        assertNotNull(Signal.istft(stftResult));
    }

    @Test
    public void testDCTMethods() {
        double[] signal = { 1, 2, 3, 4, 5, 4, 3, 2 };

        // DCT (unnormalized)
        double[] dctResult = Signal.dct(signal);
        assertNotNull(dctResult);
        assertEquals(signal.length, dctResult.length);

        // DCT (orthonormal)
        double[] dctOrtho = Signal.dct(signal, true);
        assertNotNull(dctOrtho);
        assertEquals(signal.length, dctOrtho.length);

        // IDCT
        double[] idctResult = Signal.idct(dctResult);
        assertNotNull(idctResult);
        assertEquals(signal.length, idctResult.length);
    }

    @Test
    public void testSpectralMethods() {
        double[] signal = new double[256];
        for (int i = 0; i < signal.length; i++) {
            signal[i] = java.lang.Math.sin(2 * java.lang.Math.PI * i / 16.0);
        }
        double fs = 100.0;

        // Welch
        assertNotNull(Signal.welch(signal, fs, 128));

        // Periodogram
        assertNotNull(Signal.periodogram(signal, fs));

        // Spectrogram
        assertNotNull(Signal.spectrogram(signal, fs));
    }

    @Test
    public void testConvolutionMethods() {
        double[] signal = { 1, 2, 3, 4, 5 };
        double[] kernel = { 1, 1, 1 };

        // 1D Convolve
        assertNotNull(Signal.convolve(signal, kernel, com.hissain.jscipy.signal.ConvolutionMode.FULL));
        assertNotNull(Signal.convolve(signal, kernel, com.hissain.jscipy.signal.ConvolutionMode.SAME));
        assertNotNull(Signal.convolve(signal, kernel, com.hissain.jscipy.signal.ConvolutionMode.VALID));

        // 1D Correlate
        assertNotNull(Signal.correlate(signal, kernel, com.hissain.jscipy.signal.ConvolutionMode.FULL));
        assertNotNull(Signal.correlate(signal, kernel, com.hissain.jscipy.signal.ConvolutionMode.SAME));
        assertNotNull(Signal.correlate(signal, kernel, com.hissain.jscipy.signal.ConvolutionMode.VALID));

        // 2D Convolve
        double[][] signal2d = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        double[][] kernel2d = { { 1, 0 }, { 0, 1 } };
        assertNotNull(Signal.convolve2d(signal2d, kernel2d, com.hissain.jscipy.signal.ConvolutionMode.FULL));
        assertNotNull(Signal.convolve2d(signal2d, kernel2d, com.hissain.jscipy.signal.ConvolutionMode.SAME));
        assertNotNull(Signal.convolve2d(signal2d, kernel2d, com.hissain.jscipy.signal.ConvolutionMode.VALID));

        // 2D Correlate
        assertNotNull(Signal.correlate2d(signal2d, kernel2d, com.hissain.jscipy.signal.ConvolutionMode.FULL));
        assertNotNull(Signal.correlate2d(signal2d, kernel2d, com.hissain.jscipy.signal.ConvolutionMode.SAME));
        assertNotNull(Signal.correlate2d(signal2d, kernel2d, com.hissain.jscipy.signal.ConvolutionMode.VALID));
    }

    @Test
    public void testFilteringMethods() {
        double[] signal = { 1, 5, 2, 8, 3, 7, 4, 6, 5 };

        // Savitzky-Golay filter
        assertNotNull(Signal.savgol_filter(signal, 5, 2));
        assertNotNull(Signal.savgol_filter(signal, 5, 2, 0, 1.0));
        assertNotNull(Signal.savgol_filter(signal, 5, 2, 1, 1.0)); // First derivative

        // Median filter
        assertNotNull(Signal.medfilt(signal, 3));
        assertNotNull(Signal.medfilt(signal, 5));
    }

    @Test
    public void testPeakAnalysisMethods() {
        double[] signal = { 0, 1, 0, 2, 0, 3, 0, 2, 0, 1, 0 };

        // Find peaks
        int[] peaks = Signal.find_peaks(signal);
        assertNotNull(peaks);

        // Peak prominences
        if (peaks.length > 0) {
            assertNotNull(Signal.peakProminences(signal, peaks));
            assertNotNull(Signal.peakProminences(signal, peaks, -1));
        }

        // Peak widths
        if (peaks.length > 0) {
            assertNotNull(Signal.peakWidths(signal, peaks));
            assertNotNull(Signal.peakWidths(signal, peaks, 0.5));
        }
    }

    @Test
    public void testFIRDesignMethods() {
        double fs = 100.0;

        // FIR filter design
        assertNotNull(Signal.firwin(31, new double[] { 10.0 }, fs, true)); // Lowpass
        assertNotNull(Signal.firwin_lowpass(31, 10.0, fs));
        assertNotNull(Signal.firwin_highpass(31, 10.0, fs));
        assertNotNull(Signal.firwin_bandpass(51, 10.0, 20.0, fs));
        assertNotNull(Signal.firwin_bandstop(51, 10.0, 20.0, fs));
    }

    @Test
    public void testSOSFiltMethod() {
        double[] signal = { 1, 2, 3, 4, 5 };
        double[][] sos = {
                { 1, 0, 0, 1, 0, 0 }, // Simple pass-through section
                { 1, 0, 0, 1, 0, 0 }
        };

        assertNotNull(Signal.sosfilt(signal, sos));
    }

    @Test
    public void testPadSignalMethod() {
        double[] signal = { 1, 2, 3, 4, 5 };

        double[] padded = Signal.padSignal(signal, 3);
        assertNotNull(padded);
        assertEquals(signal.length + 2 * 3, padded.length);
    }
}
