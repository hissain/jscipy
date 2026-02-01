package com.hissain.jscipy.signal.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EllipticFunctions internal math.
 */
public class EllipticInternalTest {

    @Test
    public void testEllipK() {
        // m=0 -> pi/2
        assertEquals(Math.PI / 2.0, EllipticFunctions.ellipk(0.0), 1e-6);

        // m=1 -> Infinity
        assertEquals(Double.POSITIVE_INFINITY, EllipticFunctions.ellipk(1.0), 1e-6);

        // m=0.5 (arbitrary)
        // WolframAlpha: K(0.5) approx 1.85407
        assertEquals(1.854074677, EllipticFunctions.ellipk(0.5), 1e-6);

        // Error
        assertThrows(IllegalArgumentException.class, () -> EllipticFunctions.ellipk(-0.1));
        assertThrows(IllegalArgumentException.class, () -> EllipticFunctions.ellipk(1.1));
    }

    @Test
    public void testEllipJ() {
        // m=0 -> sn=sin(u), cn=cos(u), dn=1
        double u = 0.5;
        double[] res0 = EllipticFunctions.ellipj(u, 0.0);
        assertEquals(Math.sin(u), res0[0], 1e-6); // sn
        assertEquals(Math.cos(u), res0[1], 1e-6); // cn
        assertEquals(1.0, res0[2], 1e-6); // dn

        // m=1 -> sn=tanh(u), cn=sech(u), dn=sech(u)
        double[] res1 = EllipticFunctions.ellipj(u, 1.0);
        assertEquals(Math.tanh(u), res1[0], 1e-6);
        double sech = 1.0 / Math.cosh(u);
        assertEquals(sech, res1[1], 1e-6);
        assertEquals(sech, res1[2], 1e-6);

        // Error
        assertThrows(IllegalArgumentException.class, () -> EllipticFunctions.ellipj(u, -0.1));
    }

    @Test
    public void testEllipCD() {
        // cd = cn/dn
        // m=0 -> cos(u)/1 = cos(u)
        assertEquals(Math.cos(0.5), EllipticFunctions.ellipcd(0.5, 0.0), 1e-6);
    }

    @Test
    public void testEllipJAtK() {
        // u=1.0 -> u*K = K.
        // sn(K) = 1, cn(K) = 0, dn(K) = sqrt(1-m)
        double m = 0.5;
        double[] res = EllipticFunctions.ellipjAtK(1.0, m);
        assertEquals(1.0, res[0], 1e-6); // sn
        assertEquals(0.0, res[1], 1e-6); // cn
        assertEquals(Math.sqrt(1.0 - m), res[2], 1e-6); // dn
    }
}
