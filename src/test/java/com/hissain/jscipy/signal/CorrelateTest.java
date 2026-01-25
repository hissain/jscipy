package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import com.hissain.jscipy.TestMetrics;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorrelateTest {

    private static final String DATASETS_DIR = "datasets/correlate";
    private static final String DATA_DIR_2D = "datasets/correlate2d/";
    private static final double TOLERANCE = 1e-14;

    // --- 1D Helpers ---
    private double[] loadData(String filename) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(DATASETS_DIR, filename))) {
            return lines.mapToDouble(Double::parseDouble).toArray();
        }
    }

    private void runTest(String testId, ConvolutionMode mode) throws IOException {
        double[] in1 = loadData(testId + "_input1.txt");
        double[] in2 = loadData(testId + "_input2.txt");
        double[] expected = loadData(testId + "_output.txt");

        double[] actual = Signal.correlate(in1, in2, mode);

        com.hissain.jscipy.signal.util.LoadTxt.write(DATASETS_DIR + "/" + testId + "_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for " + testId + ": " + rmse);

        assertArrayEquals(expected, actual, TOLERANCE, "Failed for test: " + testId);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException(
                    "Array lengths differ: expected=" + expected.length + ", actual=" + actual.length);
        }
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    // --- 2D Helpers ---
    private double[][] loadMatrix(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file = new File(DATA_DIR_2D + filename);
        }
        if (!file.exists()) {
            throw new IOException("File not found: " + filename);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String[] dims = br.readLine().trim().split("\\s+");
            int rows = Integer.parseInt(dims[0]);
            int cols = Integer.parseInt(dims[1]);
            double[][] matrix = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = Double.parseDouble(parts[j]);
                }
            }
            return matrix;
        }
    }

    private void saveMatrix(String filename, double[][] matrix) throws IOException {
        java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filename));
        pw.println(matrix.length + " " + matrix[0].length);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                pw.print(matrix[i][j] + (j < matrix[0].length - 1 ? " " : ""));
            }
            pw.println();
        }
        pw.close();
    }

    private double calculateRMSE(double[][] expected, double[][] actual) {
        double sumSq = 0;
        int count = 0;
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[0].length; j++) {
                double diff = expected[i][j] - actual[i][j];
                sumSq += diff * diff;
                count++;
            }
        }
        return Math.sqrt(sumSq / count);
    }

    private void assertMatrixEquals(double[][] expected, double[][] actual, double delta) {
        assertEquals(expected.length, actual.length, "Row count mismatch");
        assertEquals(expected[0].length, actual[0].length, "Col count mismatch");
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i], delta, "Mismatch at row " + i);
        }
    }

    // --- 1D Tests ---

    @Test
    void testBasicFull() throws IOException {
        runTest("correlate_basic_full", ConvolutionMode.FULL);
    }

    @Test
    void testBasicSame() throws IOException {
        runTest("correlate_basic_same", ConvolutionMode.SAME);
    }

    @Test
    void testBasicValid() throws IOException {
        runTest("correlate_basic_valid", ConvolutionMode.VALID);
    }

    @Test
    void testRandomFull() throws IOException {
        runTest("correlate_random_full", ConvolutionMode.FULL);
    }

    @Test
    void testRandomSame() throws IOException {
        runTest("correlate_random_same", ConvolutionMode.SAME);
    }

    @Test
    void testRandomValid() throws IOException {
        runTest("correlate_random_valid", ConvolutionMode.VALID);
    }

    // --- 2D Tests ---

    @Test
    public void testCorrelate2dBasicFull() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR_2D + "correlate2d_in1_1.txt");
        double[][] in2 = loadMatrix(DATA_DIR_2D + "correlate2d_in2_1.txt");
        double[][] expected = loadMatrix(DATA_DIR_2D + "correlate2d_out_full_1.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.FULL);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Basic Full: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Basic Full", rmse);

        saveMatrix(DATA_DIR_2D + "correlate2d_out_full_1_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dBasicSame() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR_2D + "correlate2d_in1_1.txt");
        double[][] in2 = loadMatrix(DATA_DIR_2D + "correlate2d_in2_1.txt");
        double[][] expected = loadMatrix(DATA_DIR_2D + "correlate2d_out_same_1.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.SAME);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Basic Same: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Basic Same", rmse);

        saveMatrix(DATA_DIR_2D + "correlate2d_out_same_1_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dBasicValid() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR_2D + "correlate2d_in1_1.txt");
        double[][] in2 = loadMatrix(DATA_DIR_2D + "correlate2d_in2_1.txt");
        double[][] expected = loadMatrix(DATA_DIR_2D + "correlate2d_out_valid_1.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.VALID);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Basic Valid: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Basic Valid", rmse);

        saveMatrix(DATA_DIR_2D + "correlate2d_out_valid_1_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dRandomFull() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR_2D + "correlate2d_in1_2.txt");
        double[][] in2 = loadMatrix(DATA_DIR_2D + "correlate2d_in2_2.txt");
        double[][] expected = loadMatrix(DATA_DIR_2D + "correlate2d_out_full_2.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.FULL);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Random Full: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Random Full", rmse);

        saveMatrix(DATA_DIR_2D + "correlate2d_out_full_2_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dSameDifferentSizes() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR_2D + "correlate2d_in1_3.txt");
        double[][] in2 = loadMatrix(DATA_DIR_2D + "correlate2d_in2_3.txt");
        double[][] expected = loadMatrix(DATA_DIR_2D + "correlate2d_out_same_3.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.SAME);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Different Sizes Same: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Different Sizes Same", rmse);

        saveMatrix(DATA_DIR_2D + "correlate2d_out_same_3_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }
}
