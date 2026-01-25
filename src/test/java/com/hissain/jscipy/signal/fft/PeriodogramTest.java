package com.hissain.jscipy.signal.fft;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Periodogram implementation against SciPy reference data.
 */
public class PeriodogramTest {

    private static final String DATASETS_DIR = "datasets/periodogram";

    @Test
    public void testPeriodogramMatchSciPy() throws IOException {
        // Load input signal
        double[] input = loadTxt(DATASETS_DIR + "/periodogram_input.txt");

        // Load expected outputs
        double[] expectedFreq = loadTxt(DATASETS_DIR + "/periodogram_frequencies.txt");
        double[] expectedPsd = loadTxt(DATASETS_DIR + "/periodogram_psd.txt");

        // Compute periodogram
        double fs = 1000.0;
        Periodogram.PeriodogramResult result = new Periodogram().periodogram(input, fs);

        // Save Java output for comparison plotting (Golden Master rule)
        saveTxt(DATASETS_DIR + "/periodogram_java_frequencies.txt", result.frequencies);
        saveTxt(DATASETS_DIR + "/periodogram_java_psd.txt", result.psd);

        // Verify frequencies
        double freqRmse = calculateRMSE(expectedFreq, result.frequencies);
        System.out.println("Periodogram Frequencies RMSE: " + freqRmse);
        assertTrue(freqRmse < 1e-10, "Frequencies RMSE should be < 1e-10");

        // Verify PSD
        double psdRmse = calculateRMSE(expectedPsd, result.psd);
        System.out.println("Periodogram PSD RMSE: " + psdRmse);
        assertTrue(psdRmse < 1e-10, "PSD RMSE should be < 1e-10");
    }

    private void saveTxt(String filename, double[] data) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            for (double value : data) {
                writer.println(String.format("%.18e", value));
            }
        }
        System.out.println("Saved " + filename);
    }

    private double[] loadTxt(String filename) throws IOException {
        List<Double> values = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    values.add(Double.parseDouble(line.trim()));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            System.out.println("Length mismatch: expected " + expected.length + ", actual " + actual.length);
            return Double.MAX_VALUE;
        }
        double sum = 0.0;
        for (int i = 0; i < expected.length; i++) {
            double diff = expected[i] - actual[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum / expected.length);
    }
}
