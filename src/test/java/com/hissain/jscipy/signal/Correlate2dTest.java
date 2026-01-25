package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import com.hissain.jscipy.TestMetrics;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Correlate2dTest {

    private static final String DATA_DIR = "datasets/correlate2d/";
    private static final double TOLERANCE = 1e-14;

    private double[][] loadMatrix(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            // Try assuming running from project root
            file = new File(DATA_DIR + filename);
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

    @Test
    public void testCorrelate2dBasicFull() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR + "correlate2d_in1_1.txt");
        double[][] in2 = loadMatrix(DATA_DIR + "correlate2d_in2_1.txt");
        double[][] expected = loadMatrix(DATA_DIR + "correlate2d_out_full_1.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.FULL);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Basic Full: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Basic Full", rmse);

        saveMatrix(DATA_DIR + "correlate2d_out_full_1_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dBasicSame() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR + "correlate2d_in1_1.txt");
        double[][] in2 = loadMatrix(DATA_DIR + "correlate2d_in2_1.txt");
        double[][] expected = loadMatrix(DATA_DIR + "correlate2d_out_same_1.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.SAME);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Basic Same: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Basic Same", rmse);

        saveMatrix(DATA_DIR + "correlate2d_out_same_1_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dBasicValid() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR + "correlate2d_in1_1.txt");
        double[][] in2 = loadMatrix(DATA_DIR + "correlate2d_in2_1.txt");
        double[][] expected = loadMatrix(DATA_DIR + "correlate2d_out_valid_1.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.VALID);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Basic Valid: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Basic Valid", rmse);

        saveMatrix(DATA_DIR + "correlate2d_out_valid_1_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dRandomFull() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR + "correlate2d_in1_2.txt");
        double[][] in2 = loadMatrix(DATA_DIR + "correlate2d_in2_2.txt");
        double[][] expected = loadMatrix(DATA_DIR + "correlate2d_out_full_2.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.FULL);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Random Full: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Random Full", rmse);

        saveMatrix(DATA_DIR + "correlate2d_out_full_2_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void testCorrelate2dSameDifferentSizes() throws IOException {
        double[][] in1 = loadMatrix(DATA_DIR + "correlate2d_in1_3.txt");
        double[][] in2 = loadMatrix(DATA_DIR + "correlate2d_in2_3.txt");
        double[][] expected = loadMatrix(DATA_DIR + "correlate2d_out_same_3.txt");

        double[][] actual = Signal.correlate2d(in1, in2, ConvolutionMode.SAME);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Correlate2d Different Sizes Same: " + rmse);
        TestMetrics.log("2D Ops", "Correlate2d Different Sizes Same", rmse);

        saveMatrix(DATA_DIR + "correlate2d_out_same_3_java.txt", actual);
        assertMatrixEquals(expected, actual, TOLERANCE);
    }
}
