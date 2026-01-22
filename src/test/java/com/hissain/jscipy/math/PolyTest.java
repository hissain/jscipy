package com.hissain.jscipy.math;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class PolyTest {

    private static final String DATASETS_DIR = "datasets/poly";
    private static final double TOLERANCE = 1e-12;

    private double[] loadData(String filename) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(DATASETS_DIR, filename))) {
            return lines.mapToDouble(Double::parseDouble).toArray();
        }
    }

    private void saveData(String filename, double[] data) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(
                new java.io.FileWriter(Paths.get(DATASETS_DIR, filename).toFile()))) {
            for (double value : data) {
                writer.println(String.format("%.18e", value));
            }
        }
    }

    @Test
    void testPolyfitExact() throws IOException {
        double[] x = loadData("polyfit_exact_x.txt");
        double[] y = loadData("polyfit_exact_y.txt");
        double[] expectedCoeffs = loadData("polyfit_exact_coeffs.txt");

        double[] actualCoeffs = Poly.polyfit(x, y, 2);

        // Save Java output for comparison plotting (Golden Master rule)
        saveData("polyfit_exact_coeffs_java.txt", actualCoeffs);

        assertArrayEquals(expectedCoeffs, actualCoeffs, TOLERANCE, "Polyfit Exact coefficients mismatch");
    }

    @Test
    void testPolyvalExact() throws IOException {
        double[] x = loadData("polyval_exact_x.txt");
        double[] expectedY = loadData("polyval_exact_y.txt");
        double[] coeffs = loadData("polyfit_exact_coeffs.txt");

        double[] actualY = Poly.polyval(coeffs, x);

        assertArrayEquals(expectedY, actualY, TOLERANCE, "Polyval Exact values mismatch");
    }

    @Test
    void testPolyfitLstsq() throws IOException {
        double[] x = loadData("polyfit_lstsq_x.txt");
        double[] y = loadData("polyfit_lstsq_y.txt");
        double[] expectedCoeffs = loadData("polyfit_lstsq_coeffs.txt");

        double[] actualCoeffs = Poly.polyfit(x, y, 2);

        // Save Java output for comparison plotting (Golden Master rule)
        saveData("polyfit_lstsq_coeffs_java.txt", actualCoeffs);

        assertArrayEquals(expectedCoeffs, actualCoeffs, TOLERANCE, "Polyfit Least Squares coefficients mismatch");
    }

    @Test
    void testPolyvalLstsq() throws IOException {
        double[] x = loadData("polyval_lstsq_x.txt");
        double[] expectedY = loadData("polyval_lstsq_y.txt");
        double[] coeffs = loadData("polyfit_lstsq_coeffs.txt");

        double[] actualY = Poly.polyval(coeffs, x);

        assertArrayEquals(expectedY, actualY, TOLERANCE, "Polyval Least Squares values mismatch");
    }

    @Test
    void testPolyder() {
        // p(x) = 3x^2 + 2x + 1 => p'(x) = 6x + 2
        double[] p = { 3.0, 2.0, 1.0 };
        double[] expected = { 6.0, 2.0 };

        double[] deriv = Poly.polyder(p);

        assertArrayEquals(expected, deriv, TOLERANCE, "Polyder mismatch");

        // Test derivative of constant
        assertArrayEquals(new double[] { 0.0 }, Poly.polyder(new double[] { 5.0 }), TOLERANCE,
                "Polyder constant mismatch");
    }
}
