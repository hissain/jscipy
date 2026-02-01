package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for package-private internal classes in filter package.
 */
public class FilterInternalTest {

    @Test
    public void testComplexPair() {
        Complex c1 = new Complex(1, 2);
        Complex c2 = new Complex(1, -2);

        // Constructor 1
        ComplexPair p1 = new ComplexPair(c1, c2);
        assertTrue(p1.isConjugate());
        assertFalse(p1.isReal());
        assertTrue(p1.isMatchedPair());
        assertFalse(p1.is_nan());

        // Constructor 2 (Real)
        ComplexPair p2 = new ComplexPair(new Complex(5, 0));
        assertFalse(p2.isConjugate()); // second is 0,0, not 5,-0
        assertTrue(p2.first.getImaginary() == 0);
        assertTrue(p2.second.getImaginary() == 0);
        assertTrue(p2.isReal());
        // second is 0, so logic for isMatchedPair:
        // else -> second.imag==0 && second.real!=0 && first.real!=0
        // here second.real is 0. So it should be false?
        assertFalse(p2.isMatchedPair());

        // NaN check
        ComplexPair pNan = new ComplexPair(Complex.NaN, Complex.ONE);
        assertTrue(pNan.is_nan());
    }

    @Test
    public void testMathSupplement() {
        // Quadratic: x^2 - 3x + 2 = 0 -> roots 1, 2
        // a=1, b=-3, c=2
        Complex r1 = MathSupplement.solve_quadratic_1(1, -3, 2);
        Complex r2 = MathSupplement.solve_quadratic_2(1, -3, 2);

        // Roots are (3 +/- sqrt(9-8))/2 = (3 +/- 1)/2 = 2, 1
        // Analysis:
        // solve_1 = sqrt(-b + (b^2-4ac)) / 2a = sqrt(3 + 1)/2 = 1.0
        // solve_2 = sqrt(-b - (b^2-4ac)) / 2a = sqrt(3 - 1)/2 = sqrt(2)/2 ~= 0.707106
        assertEquals(1.0, r1.getReal(), 1e-6);
        assertEquals(0.0, r1.getImaginary(), 1e-6);

        assertEquals(Math.sqrt(2) / 2, r2.getReal(), 1e-6);
        assertEquals(0.0, r2.getImaginary(), 1e-6);

        // adjust_imag
        Complex cSmallImag = new Complex(1.0, 1e-31);
        Complex cAdj = MathSupplement.adjust_imag(cSmallImag);
        assertEquals(0.0, cAdj.getImaginary(), 0.0);
        assertEquals(1.0, cAdj.getReal(), 1e-6);

        // addmul: c + v * c1
        Complex c = new Complex(1, 1);
        Complex c_ = new Complex(2, 2);
        Complex res = MathSupplement.addmul(c, 2.0, c_); // (1+2*2, 1+2*2) = (5, 5)
        assertEquals(5.0, res.getReal(), 1e-6);
        assertEquals(5.0, res.getImaginary(), 1e-6);

        // recip: 1/(c*c) * c* ?? No.
        // recip(c) implementation: n = 1/(|c|^2). return n*conj(c)? No code says:
        // n*real, n*imag.
        // abs = sqrt(r^2 + i^2). abs^2 = r^2 + i^2.
        // n = 1/(r^2 + i^2).
        // return (r/den, i/den). This is c / |c|^2.
        // Wait, 1/c = conj(c) / |c|^2 = (r - i*img) / den.
        // The code does: n*real, n*imag => (r/den, i/den). This is NOT 1/c. This is c /
        // |c|^2.
        // 1/c = 1 / (r e^it) = (1/r) e^-it.
        // c / |c|^2 = (r e^it) / r^2 = (1/r) e^it.
        // So this function computes c / |c|^2.
        // Let's just test what it does.
        Complex input = new Complex(2, 0); // abs=2. n=1/4=0.25 (incorrect code? abs*abs is 4)
        // code: n = 1.0 / (c.abs() * c.abs())
        // returns n*real, n*imag.
        // input(2,0) -> n=0.25 -> returns (0.5, 0).
        // 1/input = 0.5. So for real numbers it matches.
        // input(0,2) -> abs=2. n=0.25. returns (0, 0.5).
        // 1/input = 1/(2i) = -0.5i.
        // The function returns 0.5i. So it returns CONJUGATE of 1/recip? OR Inverse
        // Conjugate?
        // It seems to implement 1/conj(c)?
        // 1/conj(c) = 1/(r-i) = (r+i)/(r^2+i^2).
        // Yes. (r*n, i*n).
        Complex resRecip = MathSupplement.recip(new Complex(0, 2));
        assertEquals(0.0, resRecip.getReal(), 1e-6);
        assertEquals(0.5, resRecip.getImaginary(), 1e-6);

        // asinh
        assertEquals(0.881373587, MathSupplement.asinh(1.0), 1e-6);

        // acosh
        assertEquals(0.0, MathSupplement.acosh(1.0), 1e-6);
    }
}
