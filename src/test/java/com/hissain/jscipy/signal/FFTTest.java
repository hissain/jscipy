package com.hissain.jscipy.signal;

import com.hissain.jscipy.signal.JComplex;
import org.junit.jupiter.api.Test;

import com.hissain.jscipy.signal.fft.FFT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FFTTest {

    private final FFT fft = new FFT();
    private static final double FFT_RMSE_TOLLARENCE = 1e-14;
    private static final double FFT_MAX_ERROR_TOLERANCE = 1e-13;

    @Test
    public void testFFTData1() throws IOException {
        double[] input = readData("datasets/fft_input_1.txt");
        JComplex[] expected = readComplexData("datasets/fft_output_1.txt");
        JComplex[] actual = fft.fft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft_output_java_1.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for FFT Data 1: " + rmse);
        System.out.println("Max Error for FFT Data 1: " + maxError);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testFFTData2() throws IOException {
        double[] input = readData("datasets/fft_input_2.txt");
        JComplex[] expected = readComplexData("datasets/fft_output_2.txt");
        JComplex[] actual = fft.fft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft_output_java_2.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for FFT Data 2: " + rmse);
        System.out.println("Max Error for FFT Data 2: " + maxError);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testRFFTData1() throws IOException {
        double[] input = readData("datasets/fft_input_1.txt");
        JComplex[] expected = readComplexData("datasets/rfft_output_1.txt");
        JComplex[] actual = fft.rfft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/rfft_output_java_1.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for RFFT Data 1: " + rmse);
        System.out.println("Max Error for RFFT Data 1: " + maxError);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testRFFTData2() throws IOException {
        double[] input = readData("datasets/fft_input_2.txt");
        JComplex[] expected = readComplexData("datasets/rfft_output_2.txt");
        JComplex[] actual = fft.rfft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/rfft_output_java_2.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for RFFT Data 2: " + rmse);
        System.out.println("Max Error for RFFT Data 2: " + maxError);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIFFTData1() throws IOException {
        JComplex[] input = readComplexData("datasets/fft_output_1.txt");
        JComplex[] expected = readComplexData("datasets/ifft_output_1.txt");
        JComplex[] actual = fft.ifft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/ifft_output_java_1.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IFFT Data 1: " + rmse);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIFFTData2() throws IOException {
        JComplex[] input = readComplexData("datasets/fft_output_2.txt");
        JComplex[] expected = readComplexData("datasets/ifft_output_2.txt");
        JComplex[] actual = fft.ifft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/ifft_output_java_2.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IFFT Data 2: " + rmse);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIRFFTData1() throws IOException {
        JComplex[] input = readComplexData("datasets/rfft_output_1.txt");
        double[] expected = readData("datasets/irfft_output_1.txt");
        double[] actual = fft.irfft(input, expected.length);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/irfft_output_java_1.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IRFFT Data 1: " + rmse);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIRFFTData2() throws IOException {
        JComplex[] input = readComplexData("datasets/rfft_output_2.txt");
        double[] expected = readData("datasets/irfft_output_2.txt");
        double[] actual = fft.irfft(input, expected.length);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/irfft_output_java_2.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IRFFT Data 2: " + rmse);
        assertTrue(rmse < FFT_RMSE_TOLLARENCE, "RMSE is too high");
        assertArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    private double[] readData(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private JComplex[] readComplexData(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.map(line -> {
                String[] parts = line.trim().split("\\s+");
                double real = Double.parseDouble(parts[0]);
                double imag = Double.parseDouble(parts[1]);
                return new JComplex(real, imag);
            }).toArray(JComplex[]::new);
        }
    }

    private void assertComplexArrayEquals(JComplex[] expected, JComplex[] actual, double delta) {
        assertEquals(expected.length, actual.length, "Array lengths are not equal");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].getReal(), actual[i].getReal(), delta, "Real part of element " + i + " is not equal");
            assertEquals(expected[i].getImaginary(), actual[i].getImaginary(), delta, "Imaginary part of element " + i + " is not equal");
        }
    }

    private double calculateRMSE(JComplex[] expected, JComplex[] actual) {
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            double realDiff = expected[i].getReal() - actual[i].getReal();
            double imagDiff = expected[i].getImaginary() - actual[i].getImaginary();
            sumSquareError += realDiff * realDiff + imagDiff * imagDiff;
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    private double calculateMaxError(JComplex[] expected, JComplex[] actual) {
        double maxError = 0;
        for (int i = 0; i < expected.length; i++) {
            double realDiff = Math.abs(expected[i].getReal() - actual[i].getReal());
            double imagDiff = Math.abs(expected[i].getImaginary() - actual[i].getImaginary());
            double currentMax = Math.max(realDiff, imagDiff);
            if (currentMax > maxError) {
                maxError = currentMax;
            }
        }
        return maxError;
    }
}
