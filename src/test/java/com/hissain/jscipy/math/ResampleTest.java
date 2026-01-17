package com.hissain.jscipy.math;

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

public class ResampleTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 1e-14; // Tightened after accuracy fix

    private double[] readDataFile(String fileName) throws IOException {
        Path path = Paths.get(TEST_DATA_DIR + fileName);
        if (!Files.exists(path)) {
            System.err.println(
                    "Test data file not found: " + path.toString() + ". Please generate Python test data first.");
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

    private void runResampleTest(String inputFilename, String expectedOutputFilename, int numSamples)
            throws IOException {
        double[] input = readDataFile(inputFilename);
        double[] expectedOutput = readDataFile(expectedOutputFilename);

        Resample resampler = new Resample();
        double[] actualOutput = resampler.resample(input, numSamples);

        // Optional: Write actual output for comparison (for debugging purposes)
        writeDataFile(expectedOutputFilename.replace(".txt", "_java.txt"), actualOutput);

        double rmse = calculateRMSE(expectedOutput, actualOutput);
        System.out.println("RMSE for " + inputFilename + " (numSamples=" + numSamples + "): " + rmse);

        assertTrue(rmse < TOLERANCE, "RMSE " + rmse + " is not less than tolerance " + TOLERANCE);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException("Expected and actual arrays must have the same length." +
                    " Expected length: " + expected.length + ", Actual length: " + actual.length);
        }
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    @Test
    void testResample1() throws IOException {
        runResampleTest("resample_1_input.txt", "resample_1_output.txt", 50);
    }

    @Test
    void testResample2() throws IOException {
        runResampleTest("resample_2_input.txt", "resample_2_output.txt", 30);
    }
}
