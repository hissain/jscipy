package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SavitzkyGolayFilterTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 1e-14; // Strict tolerance

    private double[] readDataFile(String fileName) throws IOException {
        Path path = Paths.get(TEST_DATA_DIR + fileName);
        if (!Files.exists(path)) {
            fail("Test data file not found: " + path.toString());
        }
        List<Double> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
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

    private void writeDataFile(String fileName, double[] data) throws IOException {
        Path path = Paths.get(TEST_DATA_DIR + fileName);
        try (PrintWriter writer = new PrintWriter(path.toFile())) {
            for (double v : data) {
                writer.println(v);
            }
        }
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException("Expected and actual arrays must have the same length.");
        }
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    @Test
    void testSmoothing() throws IOException {
        double[] input = readDataFile("savitzky_golay_smoothing_input.txt");
        double[] expectedOutput = readDataFile("savitzky_golay_smoothing_output.txt");

        SavitzkyGolayFilter filter = new SavitzkyGolayFilter();
        double[] actualOutput = filter.smooth(input, 11, 3);

        writeDataFile("savitzky_golay_smoothing_output_java.txt", actualOutput);

        double rmse = calculateRMSE(expectedOutput, actualOutput);
        System.out.println("RMSE for Smoothing: " + rmse);
        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
    }

    @Test
    void testDifferentiation() throws IOException {
        double[] input = readDataFile("savitzky_golay_differentiation_input.txt");
        double[] expectedOutput = readDataFile("savitzky_golay_differentiation_output.txt");

        SavitzkyGolayFilter filter = new SavitzkyGolayFilter();
        double[] actualOutput = filter.differentiate(input, 11, 3, 1, 1.0);

        writeDataFile("savitzky_golay_differentiation_output_java.txt", actualOutput);

        double rmse = calculateRMSE(expectedOutput, actualOutput);
        System.out.println("RMSE for Differentiation: " + rmse);
        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
    }
}
