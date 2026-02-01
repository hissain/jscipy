package com.hissain.jscipy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the static facade methods of com.hissain.jscipy.Math.
 * Since logic is tested in specific classes, these perform smoke tests to
 * verify delegation.
 */
public class MathCoverageTest {

    @Test
    public void testResample() {
        double[] input = { 1.0, 2.0, 3.0, 4.0 };
        double[] output = Math.resample(input, 2);
        assertNotNull(output);
        assertEquals(2, output.length);
    }

    @Test
    public void testInterpolation() {
        double[] x = { 0, 1, 2 };
        double[] y = { 0, 1, 4 }; // y = x^2
        double[] xi = { 0.5, 1.5 };

        // Linear
        double[] yi = Math.interp1d_linear(x, y, xi);
        assertNotNull(yi);
        assertEquals(2, yi.length);

        // Cubic
        yi = Math.interp1d_cubic(x, y, xi);
        assertNotNull(yi);

        // Quadratic
        yi = Math.interp1d_quadratic(x, y, xi);
        assertNotNull(yi);

        // BSpline (k=2)
        yi = Math.interp1d_bspline(x, y, xi, 2);
        assertNotNull(yi);
    }

    @Test
    public void testPolynomials() {
        double[] x = { 0, 1, 2 };
        double[] y = { 1, 2, 3 }; // y = x + 1

        // Polyfit (degree 1)
        double[] p = Math.polyfit(x, y, 1);
        assertNotNull(p);
        assertEquals(2, p.length);
        assertEquals(1.0, p[0], 1e-6); // slope
        assertEquals(1.0, p[1], 1e-6); // intercept

        // Polyval (array)
        double[] evaluated = Math.polyval(p, x);
        assertArrayEquals(y, evaluated, 1e-6);

        // Polyval (scalar)
        double val = Math.polyval(p, 3.0);
        assertEquals(4.0, val, 1e-6); // 3 + 1 = 4

        // Polyder (derivative of x + 1 is 1)
        double[] der = Math.polyder(p);
        assertNotNull(der);
        assertEquals(1, der.length);
        assertEquals(1.0, der[0], 1e-6);
    }
}
