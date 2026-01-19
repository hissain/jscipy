package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import java.io.IOException;
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

    // --- New Window Functions (Bartlett, Triang, Flattop, Parzen, Bohman) ---

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/windows/";

    @Test
    void testBartlett() throws IOException {
        runTest("windows_bartlett_M10_sym.txt", "bartlett", 10, true);
        runTest("windows_bartlett_M10_periodic.txt", "bartlett", 10, false);
        runTest("windows_bartlett_M51_sym.txt", "bartlett", 51, true);
        runTest("windows_bartlett_M51_periodic.txt", "bartlett", 51, false);
    }

    @Test
    void testTriang() throws IOException {
        runTest("windows_triang_M10_sym.txt", "triang", 10, true);
        runTest("windows_triang_M10_periodic.txt", "triang", 10, false);
        runTest("windows_triang_M51_sym.txt", "triang", 51, true);
        runTest("windows_triang_M51_periodic.txt", "triang", 51, false);
    }

    @Test
    void testFlattop() throws IOException {
        runTest("windows_flattop_M10_sym.txt", "flattop", 10, true);
        runTest("windows_flattop_M10_periodic.txt", "flattop", 10, false);
        runTest("windows_flattop_M51_sym.txt", "flattop", 51, true);
        runTest("windows_flattop_M51_periodic.txt", "flattop", 51, false);
    }

    @Test
    void testParzen() throws IOException {
        runTest("windows_parzen_M10_sym.txt", "parzen", 10, true);
        runTest("windows_parzen_M10_periodic.txt", "parzen", 10, false);
        runTest("windows_parzen_M51_sym.txt", "parzen", 51, true);
        runTest("windows_parzen_M51_periodic.txt", "parzen", 51, false);
    }

    @Test
    void testBohman() throws IOException {
        runTest("windows_bohman_M10_sym.txt", "bohman", 10, true);
        runTest("windows_bohman_M10_periodic.txt", "bohman", 10, false);
        runTest("windows_bohman_M51_sym.txt", "bohman", 51, true);
        runTest("windows_bohman_M51_periodic.txt", "bohman", 51, false);
    }

    private void runTest(String expectedFilename, String method, int m, boolean symmetric) throws IOException {
        double[] expected = readDataFile(expectedFilename);
        double[] actual;
        switch (method) {
            case "bartlett":
                actual = Windows.bartlett(m, symmetric);
                break;
            case "triang":
                actual = Windows.triang(m, symmetric);
                break;
            case "flattop":
                actual = Windows.flattop(m, symmetric);
                break;
            case "parzen":
                actual = Windows.parzen(m, symmetric);
                break;
            case "bohman":
                actual = Windows.bohman(m, symmetric);
                break;
            default:
                throw new IllegalArgumentException("Unknown window: " + method);
        }

        // Save output for plotting
        String outName = expectedFilename.replace(".txt", "_java.txt");
        saveDataFile(outName, actual);

        // Assert matches
        assertArrayEquals(expected, actual, 1e-12, "Failed for " + method + " M=" + m + " sym=" + symmetric);
    }

    private double[] readDataFile(String filename) throws IOException {
        java.util.List<Double> data = new java.util.ArrayList<>();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(TEST_DATA_DIR + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    data.add(Double.parseDouble(line));
                }
            }
        }
        return data.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private void saveDataFile(String filename, double[] data) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + filename)) {
            for (double v : data) {
                writer.println(v);
            }
        }
    }
}
