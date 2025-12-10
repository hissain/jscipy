package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class WindowsTest {

    private static final double DELTA = 1e-9; // Tolerance for double comparisons

    @Test
    void testHanningSymmetric() {
        double[] expected = { 0.0, 0.5, 1.0, 0.5, 0.0 };
        double[] actual = Windows.hanning(5, true);
        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testHanningPeriodic() {
        double[] expected = { 0.0, 0.3454915028125263, 0.9045084971874737, 0.9045084971874737, 0.3454915028125263 };
        double[] actual = Windows.hanning(5, false);
        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testHammingSymmetric() {
        double[] expected = { 0.08, 0.54, 1.0, 0.54, 0.08 };
        double[] actual = Windows.hamming(5, true);
        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testHammingPeriodic() {
        double[] expected = { 0.08, 0.3978521825875242, 0.9121478174124758, 0.9121478174124758, 0.3978521825875242 };
        double[] actual = Windows.hamming(5, false);
        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testBlackmanSymmetric() {
        double[] expected = { 0.0, 0.63, 0.63, 0.0 };
        double[] actual = Windows.blackman(4, true);
        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testBlackmanLengthOne() {
        double[] expected = { 1.0 };
        double[] actual = Windows.blackman(1);
        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testKaiserSymmetric() {
        // Scipy: signal.windows.kaiser(5, 14, sym=True)
        // Values verified via implementation formula I0(beta * sqrt(1 - k^2)) /
        // I0(beta)
        double[] expected = { 7.726866835276539E-6, 0.1649321875480714, 1.0, 0.1649321875480714, 7.726866835276539E-6 };
        double[] actual = Windows.kaiser(5, 14, true);
        assertArrayEquals(expected, actual, 1e-7);
    }

    @Test
    void testKaiserPeriodic() {
        // Scipy: signal.windows.kaiser(5, 14, sym=False)
        double[] expected = { 7.726866835276539E-6, 0.06815374319400959, 0.7615093990877031, 0.7615093990877031,
                0.06815374319400944 };
        double[] actual = Windows.kaiser(5, 14, false);
        assertArrayEquals(expected, actual, 1e-7);
    }

    @Test
    void testKaiserBetaZero() {
        // Beta=0 makes it a rectangular window (all ones)
        double[] expected = { 1.0, 1.0, 1.0, 1.0, 1.0 };
        double[] actual = Windows.kaiser(5, 0.0);
        assertArrayEquals(expected, actual, DELTA);
    }
}
