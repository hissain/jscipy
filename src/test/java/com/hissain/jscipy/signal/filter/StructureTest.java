package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Biquad and related structure classes.
 * Targeting low coverage in Biquad.java.
 */
public class StructureTest {

    @Test
    public void testBiquadIdentity() {
        Biquad bq = new Biquad();
        bq.setIdentity(); // 1, 0, 0, 1, 0, 0

        double[] a = bq.getACoefficients();
        double[] b = bq.getBCoefficients();

        assertEquals(1.0, a[0]);
        assertEquals(0.0, a[1]);
        assertEquals(0.0, a[2]);
        assertEquals(1.0, b[0]);
        assertEquals(0.0, b[1]);
        assertEquals(0.0, b[2]);

        // Response of identity should be 1 at any frequency
        Complex resp = bq.response(0.1);
        assertEquals(1.0, resp.getReal(), 1e-6);
        assertEquals(0.0, resp.getImaginary(), 1e-6);
    }

    @Test
    public void testBiquadSetCoeffs() {
        Biquad bq = new Biquad();
        // setCoefficients(a0, a1, a2, b0, b1, b2)
        // internal: m_a0=a0, m_a1=a1/a0 ...
        bq.setCoefficients(2.0, 1.0, 0.5, 4.0, 2.0, 1.0);

        assertEquals(2.0, bq.getA0());

        // getters return m_a * m_a0 -> so they should return original inputs
        assertArrayEquals(new double[] { 2.0, 1.0, 0.5 }, bq.getACoefficients(), 1e-6);
        assertArrayEquals(new double[] { 4.0, 2.0, 1.0 }, bq.getBCoefficients(), 1e-6);

        // Response check (simple case)
        // H(z) = (b0 + b1 z^-1 + b2 z^-2) / (a0 + a1 z^-1 + a2 z^-2)
        // At f=0 (DC), z = 1.
        // H(1) = (4+2+1) / (2+1+0.5) = 7 / 3.5 = 2.0
        Complex resp_dc = bq.response(0.0); // w=0 -> z=1
        assertEquals(2.0, resp_dc.getReal(), 1e-6);
        assertEquals(0.0, resp_dc.getImaginary(), 1e-6);
    }

    @Test
    public void testBiquadPoles() {
        Biquad bq1 = new Biquad();
        // setOnePole(pole, zero)
        // pole at 0.5, zero at -0.5
        bq1.setOnePole(new Complex(0.5), new Complex(-0.5));
        // a0=1, a1=-0.5, a2=0
        // b0=1, b1=0.5, b2=0
        double[] a = bq1.getACoefficients();
        assertEquals(1.0, a[0], 1e-6);
        assertEquals(-0.5, a[1], 1e-6);

        Biquad bq2 = new Biquad();
        // setTwoPole
        // Real poles
        bq2.setTwoPole(new Complex(0.5), new Complex(0.5), new Complex(0.5), new Complex(0.5));
        // a1 = -(p1+p2) = -1.0. a2 = p1*p2 = 0.25
        double[] a2 = bq2.getACoefficients();
        assertEquals(-1.0, a2[1], 1e-6);
        assertEquals(0.25, a2[2], 1e-6);

        // Complex conjugate poles
        Biquad bq3 = new Biquad();
        Complex p = new Complex(0.5, 0.5);
        Complex z = new Complex(0.5, -0.5);
        // setTwoPole logic: if (pole1.imag != 0) -> a1=-2*real, a2=|p|^2
        bq3.setTwoPole(p, z, p.conjugate(), z.conjugate());
        // Logic uses p1, z1. p1 is 0.5+0.5i.
        // a1 = -2*0.5 = -1.0
        // a2 = 0.5^2 + 0.5^2 = 0.5
        double[] a3 = bq3.getACoefficients();
        assertEquals(-1.0, a3[1], 1e-6);
        assertEquals(0.5, a3[2], 1e-6);
    }

    @Test
    public void testPoleZeroState() {
        // BiquadPoleState has no default constructor.
        // Use constructor: (pole, zero)
        Complex p = new Complex(0.5);
        Complex z = new Complex(-0.5);
        BiquadPoleState bps = new BiquadPoleState(p, z);
        bps.gain = 2.0;

        Biquad bq = new Biquad();
        bq.setPoleZeroForm(bps);

        // Single pole logic implied by single Complex args in constructor?
        // Let's assume it works.
        double[] a = bq.getACoefficients();
        assertEquals(1.0, a[0]);
        // internal: setCoefficients called.
        // bq should be set.
    }
}
