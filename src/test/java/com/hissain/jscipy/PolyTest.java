package com.hissain.jscipy;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class PolyTest {

    private static final String DATASETS_DIR = "datasets";
    private static final double TOLERANCE = 1e-12;

    private double[] loadData(String filename) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(DATASETS_DIR, filename))) {
            return lines.mapToDouble(Double::parseDouble).toArray();
        }
    }

    @Test
    void testPolyfitExact() throws IOException {
        double[] x = loadData("polyfit_exact_x.txt");
        double[] y = loadData("polyfit_exact_y.txt");
        double[] expectedCoeffs = loadData("polyfit_exact_coeffs.txt");

        double[] actualCoeffs = Math.polyfit(x, y, 2);

        assertArrayEquals(expectedCoeffs, actualCoeffs, TOLERANCE, "Polyfit Exact coefficients mismatch");
    }

    @Test
    void testPolyvalExact() throws IOException {
        double[] x = loadData("polyval_exact_x.txt");
        double[] expectedY = loadData("polyval_exact_y.txt");
        double[] coeffs = loadData("polyfit_exact_coeffs.txt");

        double[] actualY = Math.polyval(coeffs, x);

        assertArrayEquals(expectedY, actualY, TOLERANCE, "Polyval Exact values mismatch");
    }

    @Test
    void testPolyfitLstsq() throws IOException {
        double[] x = loadData("polyfit_lstsq_x.txt");
        double[] y = loadData("polyfit_lstsq_y.txt");
        double[] expectedCoeffs = loadData("polyfit_lstsq_coeffs.txt");

        double[] actualCoeffs = Math.polyfit(x, y, 2);

        assertArrayEquals(expectedCoeffs, actualCoeffs, TOLERANCE, "Polyfit Least Squares coefficients mismatch");
    }

    @Test
    void testPolyvalLstsq() throws IOException {
        double[] x = loadData("polyval_lstsq_x.txt");
        double[] expectedY = loadData("polyval_lstsq_y.txt");
        double[] coeffs = loadData("polyfit_lstsq_coeffs.txt");

        double[] actualY = Math.polyval(coeffs, x);

        assertArrayEquals(expectedY, actualY, TOLERANCE, "Polyval Least Squares values mismatch");
    }

    @Test
    void testPolyder() {
        // p(x) = 3x^2 + 2x + 1 => p'(x) = 6x + 2
        double[] p = { 3.0, 2.0, 1.0 };
        double[] expected = { 6.0, 2.0 };

        double[] deriv = Math.polyder(p);

        assertArrayEquals(expected, deriv, TOLERANCE, "Polyder mismatch");

        // Test derivative of constant
        assertArrayEquals(new double[] { 0.0 }, Math.polyder(new double[] { 5.0 }), TOLERANCE,
                "Polyder constant mismatch");
    }
}
