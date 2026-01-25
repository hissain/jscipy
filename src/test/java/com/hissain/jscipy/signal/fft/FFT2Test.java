package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;
import com.hissain.jscipy.Signal;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.hissain.jscipy.TestMetrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FFT2Test {

    private double[][] loadMatrix(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file = new File("datasets/fft2/" + filename);
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

    private JComplex[][] loadComplexMatrix(String realFile, String imagFile) throws IOException {
        double[][] real = loadMatrix(realFile);
        double[][] imag = loadMatrix(imagFile);
        int rows = real.length;
        int cols = real[0].length;
        JComplex[][] loaded = new JComplex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                loaded[i][j] = new JComplex(real[i][j], imag[i][j]);
            }
        }
        return loaded;
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

    private void assertComplexMatrixEquals(JComplex[][] expected, JComplex[][] actual, double delta) {
        double[][] expectedReal = new double[expected.length][expected[0].length];
        double[][] expectedImag = new double[expected.length][expected[0].length];
        double[][] actualReal = new double[actual.length][actual[0].length];
        double[][] actualImag = new double[actual.length][actual[0].length];

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[0].length; j++) {
                expectedReal[i][j] = expected[i][j].getReal();
                expectedImag[i][j] = expected[i][j].getImaginary();
                actualReal[i][j] = actual[i][j].getReal();
                actualImag[i][j] = actual[i][j].getImaginary();
            }
        }

        double rmseReal = calculateRMSE(expectedReal, actualReal);
        double rmseImag = calculateRMSE(expectedImag, actualImag);

        System.out.println("RMSE Real: " + rmseReal);
        System.out.println("RMSE Imag: " + rmseImag); // Print explicit newline
        TestMetrics.log("2D Ops", "FFT2 Real", rmseReal);
        TestMetrics.log("2D Ops", "FFT2 Imag", rmseImag);

        assertEquals(0, rmseReal, delta, "Real part RMSE too high");
        assertEquals(0, rmseImag, delta, "Imag part RMSE too high");
    }

    @Test
    public void testFFT2() throws IOException {
        System.out.println("Testing FFT2:");
        double[][] input = loadMatrix("datasets/fft2/fft2_in.txt");
        JComplex[][] expected = loadComplexMatrix("datasets/fft2/fft2_out_real.txt", "datasets/fft2/fft2_out_imag.txt");

        JComplex[][] actual = Signal.fft2(input);

        saveComplexMatrix("datasets/fft2/java_fft2_out_real.txt", "datasets/fft2/java_fft2_out_imag.txt", actual);

        assertComplexMatrixEquals(expected, actual, 1e-8);
    }

    @Test
    public void testIFFT2() throws IOException {
        System.out.println("Testing IFFT2:");
        JComplex[][] input = loadComplexMatrix("datasets/fft2/ifft2_in_real.txt", "datasets/fft2/ifft2_in_imag.txt");
        JComplex[][] expected = loadComplexMatrix("datasets/fft2/ifft2_out_real.txt",
                "datasets/fft2/ifft2_out_imag.txt");

        JComplex[][] actual = Signal.ifft2(input);

        assertComplexMatrixEquals(expected, actual, 1e-8);
    }

    private void saveComplexMatrix(String realFile, String imagFile, JComplex[][] matrix) throws IOException {
        java.io.PrintWriter pwReal = new java.io.PrintWriter(new java.io.FileWriter(realFile));
        java.io.PrintWriter pwImag = new java.io.PrintWriter(new java.io.FileWriter(imagFile));

        pwReal.println(matrix.length + " " + matrix[0].length);
        pwImag.println(matrix.length + " " + matrix[0].length);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                pwReal.print(matrix[i][j].getReal() + (j < matrix[0].length - 1 ? " " : ""));
                pwImag.print(matrix[i][j].getImaginary() + (j < matrix[0].length - 1 ? " " : ""));
            }
            pwReal.println();
            pwImag.println();
        }
        pwReal.close();
        pwImag.close();
    }
}
