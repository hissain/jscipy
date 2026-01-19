package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.Signal;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SpectrogramTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/spectrogram/";
    // Spectrogram values can be large, so relative error might be more appropriate,
    // but for now using a reasonable absolute tolerance given the values.
    private static final double TOLERANCE = 1e-15;

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
                    String[] tokens = line.split("\\s+");
                    for (String token : tokens) {
                        data.add(Double.parseDouble(token));
                    }
                }
            }
        }
        return data.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double[][] readMatrixFile(String fileName, String shapeFileName) throws IOException {
        // Read shape
        double[] shapeData = readDataFile(shapeFileName);
        int rows = (int) shapeData[0];
        int cols = (int) shapeData[1];

        // Read flattened data
        double[] flatData = readDataFile(fileName);

        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = flatData[i * cols + j];
            }
        }
        return matrix;
    }

    private void runSpectrogramTest(String testName, double fs) throws IOException {
        double[] input = readDataFile(testName + "_input.txt");
        double[] expectedFreqs = readDataFile(testName + "_freqs.txt");
        double[] expectedTimes = readDataFile(testName + "_times.txt");
        double[][] expectedSxx = readMatrixFile(testName + "_Sxx.txt", testName + "_Sxx_shape.txt");

        Spectrogram.SpectrogramResult result = Signal.spectrogram(input, fs);

        // Verify Frequencies
        assertEquals(expectedFreqs.length, result.frequencies.length, "Frequency array length mismatch");
        for (int i = 0; i < expectedFreqs.length; i++) {
            assertEquals(expectedFreqs[i], result.frequencies[i], TOLERANCE, "Frequency mismatch at index " + i);
        }

        // Verify Times
        assertEquals(expectedTimes.length, result.times.length, "Time array length mismatch");
        for (int i = 0; i < expectedTimes.length; i++) {
            assertEquals(expectedTimes[i], result.times[i], TOLERANCE, "Time mismatch at index " + i);
        }

        // Verify Spectrogram (Sxx)
        assertEquals(expectedSxx.length, result.Sxx.length, "Spectrogram rows mismatch");
        assertEquals(expectedSxx[0].length, result.Sxx[0].length, "Spectrogram cols mismatch");

        double sumSqErr = 0.0;
        int totalPoints = expectedSxx.length * expectedSxx[0].length;

        for (int i = 0; i < expectedSxx.length; i++) {
            for (int j = 0; j < expectedSxx[0].length; j++) {
                double diff = expectedSxx[i][j] - result.Sxx[i][j];
                sumSqErr += diff * diff;
                assertEquals(expectedSxx[i][j], result.Sxx[i][j], TOLERANCE, "Sxx mismatch at [" + i + "][" + j + "]");
            }
        }

        double rmse = Math.sqrt(sumSqErr / totalPoints);
        System.out.println("Testing " + testName + ":");
        System.out.println("RMSE Sxx: " + rmse);

        // Save Java Output for Comparison
        saveSpectrogramResult(testName + "_java", result);
    }

    private void saveSpectrogramResult(String baseName, Spectrogram.SpectrogramResult result) throws IOException {
        // Save frequencies
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + baseName + "_freqs.txt")) {
            for (double v : result.frequencies) {
                writer.println(v);
            }
        }
        // Save times
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + baseName + "_times.txt")) {
            for (double v : result.times) {
                writer.println(v);
            }
        }

        // Save Sxx
        // First save shape
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + baseName + "_Sxx_shape.txt")) {
            writer.println(result.Sxx.length);
            writer.println(result.Sxx[0].length);
        }

        // Save flattened data
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + baseName + "_Sxx.txt")) {
            for (int i = 0; i < result.Sxx.length; i++) {
                for (int j = 0; j < result.Sxx[0].length; j++) {
                    writer.println(result.Sxx[i][j]);
                }
            }
        }
    }

    @Test
    void testSpectrogramChirp() throws IOException {
        runSpectrogramTest("spectrogram_chirp", 1000.0);
    }

    @Test
    void testSpectrogramSine() throws IOException {
        runSpectrogramTest("spectrogram_sine", 1000.0);
    }
}
