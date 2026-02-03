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
}
