package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Convolve2dTest {

    private double[][] loadMatrix(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            // Try assuming running from project root
            file = new File("datasets/" + filename);
        }
        if (!file.exists()) {
            // Try explicit path if needed or fail
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

    private void assertMatrixEquals(double[][] expected, double[][] actual, double delta) {
        assertEquals(expected.length, actual.length, "Row count mismatch");
        assertEquals(expected[0].length, actual[0].length, "Col count mismatch");
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i], delta, "Mismatch at row " + i);
        }
    }

    @Test
    public void testConvolveFull() throws IOException {
        double[][] in1 = loadMatrix("datasets/conv2d_in1_1.txt");
        double[][] in2 = loadMatrix("datasets/conv2d_in2_1.txt");
        double[][] expected = loadMatrix("datasets/conv2d_out_full_1.txt");

        double[][] actual = Signal.convolve2d(in1, in2, ConvolutionMode.FULL);
        saveMatrix("datasets/conv2d_out_full_1_java.txt", actual);
        assertMatrixEquals(expected, actual, 1e-8);
    }

    @Test
    public void testConvolveSame() throws IOException {
        double[][] in1 = loadMatrix("datasets/conv2d_in1_1.txt");
        double[][] in2 = loadMatrix("datasets/conv2d_in2_1.txt");
        double[][] expected = loadMatrix("datasets/conv2d_out_same_1.txt");

        double[][] actual = Signal.convolve2d(in1, in2, ConvolutionMode.SAME);
        saveMatrix("datasets/conv2d_out_same_1_java.txt", actual);
        assertMatrixEquals(expected, actual, 1e-8);
    }

    @Test
    public void testConvolveValid() throws IOException {
        double[][] in1 = loadMatrix("datasets/conv2d_in1_1.txt");
        double[][] in2 = loadMatrix("datasets/conv2d_in2_1.txt");
        double[][] expected = loadMatrix("datasets/conv2d_out_valid_1.txt");

        double[][] actual = Signal.convolve2d(in1, in2, ConvolutionMode.VALID);
        saveMatrix("datasets/conv2d_out_valid_1_java.txt", actual);
        assertMatrixEquals(expected, actual, 1e-8);
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

    @Test
    public void testRandomFull() throws IOException {
        double[][] in1 = loadMatrix("datasets/conv2d_in1_2.txt");
        double[][] in2 = loadMatrix("datasets/conv2d_in2_2.txt");
        double[][] expected = loadMatrix("datasets/conv2d_out_full_2.txt");

        double[][] actual = Signal.convolve2d(in1, in2, ConvolutionMode.FULL);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Convolve2d Random Full: " + rmse);

        // Export for Python visualization
        saveMatrix("datasets/java_conv2d_result.txt", actual);

        assertMatrixEquals(expected, actual, 1e-8);
    }
}
