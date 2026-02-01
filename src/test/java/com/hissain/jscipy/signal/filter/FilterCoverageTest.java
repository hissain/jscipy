package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FilterCoverageTest {

    @Test
    public void testBandPassTransform() {
        // Setup analog layout with some dummy poles/zeros
        LayoutBase analog = new LayoutBase(2); // 2 poles
        Complex pole = new Complex(-0.5, 0.5);
        Complex zero = new Complex(0, 1);
        analog.addPoleZeroConjugatePairs(pole, zero);

        LayoutBase digital = new LayoutBase(4); // Bandpass doubles order

        // Execute transform
        // fc = 0.25 (center), fw = 0.1 (width)
        new BandPassTransform(0.25, 0.1, digital, analog);

        // Verification
        assertEquals(4, digital.getNumPoles());
        assertNotNull(digital.getPair(0));
        assertNotNull(digital.getPair(1));
    }

    @Test
    public void testBandStopTransform() {
        // Setup analog layout
        LayoutBase analog = new LayoutBase(2);
        Complex pole = new Complex(-0.5, 0.5);
        Complex zero = new Complex(0, 1);
        analog.addPoleZeroConjugatePairs(pole, zero);

        LayoutBase digital = new LayoutBase(4);

        // Execute transform
        // fc = 0.25 (center), fw = 0.1 (width)
        new BandStopTransform(0.25, 0.1, digital, analog);

        // Verification
        assertEquals(4, digital.getNumPoles());
        assertNotNull(digital.getPair(0));
    }

    @Test
    public void testHighPassTransform() {
        // Setup analog layout
        LayoutBase analog = new LayoutBase(2);
        Complex pole = new Complex(-0.5, 0.5);
        Complex zero = new Complex(0, 1);
        analog.addPoleZeroConjugatePairs(pole, zero);

        LayoutBase digital = new LayoutBase(2);

        // Execute transform
        // fc = 0.25
        new HighPassTransform(0.25, digital, analog);

        // Verification
        assertEquals(2, digital.getNumPoles());
        assertNotNull(digital.getPair(0));
    }

    @Test
    public void testBandPassTransformExceptions() {
        LayoutBase analog = new LayoutBase(1);
        LayoutBase digital = new LayoutBase(1);

        // Negative cutoff
        assertThrows(ArithmeticException.class, () -> {
            new BandPassTransform(-0.1, 0.1, digital, analog);
        });

        // Cutoff >= 0.5 (Nyquist)
        assertThrows(ArithmeticException.class, () -> {
            new BandPassTransform(0.6, 0.1, digital, analog);
        });
    }

    @Test
    public void testDirectFormI() {
        DirectFormI df = new DirectFormI();
        df.reset();

        // Coefficients for identity filter (out = in)
        // y[n] = b0*x[n] + b1*x[n-1] + b2*x[n-2] - a1*y[n-1] - a2*y[n-2]
        // Identity: b0=1, all others 0.
        Biquad s = new Biquad();
        s.setCoefficients(1, 0, 0, 1, 0, 0);

        double input = 10.0;
        double out = df.process1(input, s);

        assertEquals(input, out, 1e-6);

        // Test state (next input 0, output should depend on history if coeffs were
        // different,
        // but for identity it's just 0)
        out = df.process1(0, s);
        assertEquals(0, out, 1e-6);
    }

    @Test
    public void testBiquadPoleState() {
        Complex p = new Complex(-1, 0);
        Complex z = new Complex(1, 0);

        BiquadPoleState bps1 = new BiquadPoleState(p, z);
        assertNotNull(bps1.poles);
        assertNotNull(bps1.zeros);
        assertEquals(-1, bps1.poles.first.getReal(), 1e-6);

        Complex p2 = new Complex(-2, 0);
        Complex z2 = new Complex(2, 0);
        BiquadPoleState bps2 = new BiquadPoleState(p, z, p2, z2);
        assertEquals(-2, bps2.poles.second.getReal(), 1e-6);

        bps2.gain = 5.0;
        assertEquals(5.0, bps2.gain, 1e-6);
    }

}
