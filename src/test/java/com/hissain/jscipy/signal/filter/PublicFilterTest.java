package com.hissain.jscipy.signal.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PublicFilterTest {

    @Test
    public void testButterworth() {
        double[] signal = new double[100];
        for (int i = 0; i < 100; i++)
            signal[i] = Math.sin(2 * Math.PI * i / 10.0);
        double fs = 1000;
        int order = 4;

        Butterworth bw = new Butterworth();

        // LowPass
        assertNotNull(bw.filter(signal, fs, 100, order));
        assertNotNull(bw.filtfilt(signal, fs, 100, order));

        // HighPass
        assertNotNull(bw.filtfilt_highpass(signal, fs, 100, order));

        // BandPass
        assertNotNull(bw.filtfilt_bandpass(signal, fs, 100, 20, order));

        // BandStop
        assertNotNull(bw.filtfilt_bandstop(signal, fs, 100, 20, order));
    }

    @Test
    public void testChebyshev1() {
        double[] signal = new double[100];
        for (int i = 0; i < 100; i++)
            signal[i] = Math.sin(2 * Math.PI * i / 10.0);
        double fs = 1000;
        int order = 4;
        double ripple = 0.5;

        // Static methods
        assertNotNull(Chebyshev1.lowPass(signal, fs, 100, order, ripple, false)); // causal
        assertNotNull(Chebyshev1.lowPass(signal, fs, 100, order, ripple, true)); // zero-phase

        assertNotNull(Chebyshev1.highPass(signal, fs, 100, order, ripple, false));
        assertNotNull(Chebyshev1.highPass(signal, fs, 100, order, ripple, true));

        assertNotNull(Chebyshev1.bandPass(signal, fs, 100, 20, order, ripple, false));
        assertNotNull(Chebyshev1.bandPass(signal, fs, 100, 20, order, ripple, true));

        assertNotNull(Chebyshev1.bandStop(signal, fs, 100, 20, order, ripple, false));
        assertNotNull(Chebyshev1.bandStop(signal, fs, 100, 20, order, ripple, true));

        // Aliases
        assertNotNull(Chebyshev1.filter(signal, fs, 100, order, ripple));
        assertNotNull(Chebyshev1.filtfilt(signal, fs, 100, order, ripple));
    }

    @Test
    public void testChebyshev2() {
        double[] signal = new double[100];
        for (int i = 0; i < 100; i++)
            signal[i] = Math.sin(2 * Math.PI * i / 10.0);
        double fs = 1000;
        int order = 4;
        double ripple = 20;

        assertNotNull(Chebyshev2.lowPass(signal, fs, 100, order, ripple, false));
        assertNotNull(Chebyshev2.lowPass(signal, fs, 100, order, ripple, true));

        assertNotNull(Chebyshev2.highPass(signal, fs, 100, order, ripple, false));
        assertNotNull(Chebyshev2.highPass(signal, fs, 100, order, ripple, true));

        assertNotNull(Chebyshev2.bandPass(signal, fs, 100, 20, order, ripple, false));
        assertNotNull(Chebyshev2.bandPass(signal, fs, 100, 20, order, ripple, true));

        assertNotNull(Chebyshev2.bandStop(signal, fs, 100, 20, order, ripple, false));
        assertNotNull(Chebyshev2.bandStop(signal, fs, 100, 20, order, ripple, true));

        assertNotNull(Chebyshev2.filter(signal, fs, 100, order, ripple));
        assertNotNull(Chebyshev2.filtfilt(signal, fs, 100, order, ripple));
    }

    @Test
    public void testBessel() {
        double[] signal = new double[100];
        for (int i = 0; i < 100; i++)
            signal[i] = Math.sin(2 * Math.PI * i / 10.0);
        double fs = 1000;
        int order = 4;

        // Design methods
        assertNotNull(Bessel.lowPass(fs, 100, order));
        assertNotNull(Bessel.highPass(fs, 100, order));
        assertNotNull(Bessel.bandPass(fs, 100, 200, order));
        assertNotNull(Bessel.bandStop(fs, 100, 200, order));

        // Filtering methods (filtfilt only?)
        assertNotNull(Bessel.filtfilt(signal, fs, 100, order));
        assertNotNull(Bessel.filtfilt_highpass(signal, fs, 100, order));
        assertNotNull(Bessel.filtfilt_bandpass(signal, fs, 100, 200, order));
        assertNotNull(Bessel.filtfilt_bandstop(signal, fs, 100, 200, order));
    }

    @Test
    public void testElliptic() {
        double[] signal = new double[100];
        for (int i = 0; i < 100; i++)
            signal[i] = Math.sin(2 * Math.PI * i / 10.0);
        double fs = 1000;
        int order = 4;
        double ripple = 0.5;
        double stopBand = 40;

        // Static methods
        assertNotNull(Elliptic.lowPass(signal, fs, 100, order, ripple, stopBand, false));
        assertNotNull(Elliptic.lowPass(signal, fs, 100, order, ripple, stopBand, true));

        assertNotNull(Elliptic.highPass(signal, fs, 100, order, ripple, stopBand, false));
        assertNotNull(Elliptic.highPass(signal, fs, 100, order, ripple, stopBand, true));

        assertNotNull(Elliptic.bandPass(signal, fs, 100, 20, order, ripple, stopBand, false));
        assertNotNull(Elliptic.bandPass(signal, fs, 100, 20, order, ripple, stopBand, true));

        assertNotNull(Elliptic.bandStop(signal, fs, 100, 20, order, ripple, stopBand, false));
        assertNotNull(Elliptic.bandStop(signal, fs, 100, 20, order, ripple, stopBand, true));

        // Aliases
        assertNotNull(Elliptic.filter(signal, fs, 100, order, ripple, stopBand));
        assertNotNull(Elliptic.filtfilt(signal, fs, 100, order, ripple, stopBand));
    }
}
