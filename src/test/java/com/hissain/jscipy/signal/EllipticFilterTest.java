package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hissain.jscipy.signal.filter.Elliptic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.hissain.jscipy.TestMetrics;

public class EllipticFilterTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/elliptic/";
    private static final double TOLERANCE = 0.1;

    private double[] readDataFile(String filename) throws IOException {
        List<Double> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_DATA_DIR + filename))) {
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

    private void runTest(String inputFilename, String expectedOutputFilename, int order, double cutoff,
            double sampleRate, double rippleDb, double stopBandDb) throws IOException {
        double[] signal = readDataFile(inputFilename);
        double[] expectedOutput = readDataFile(expectedOutputFilename);
        double[] output = Elliptic.filtfilt(signal, sampleRate, cutoff, order, rippleDb, stopBandDb);

        // Save the Java output
        String outputFilename = expectedOutputFilename.replace(".txt", "_java.txt");
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + outputFilename)) {
            for (double v : output) {
                writer.println(v);
            }
        }

        double rmse = 0;
        for (int i = 0; i < output.length; i++) {
            rmse += Math.pow(output[i] - expectedOutput[i], 2);
        }
        rmse = Math.sqrt(rmse / output.length);
        System.out.println("RMSE for " + inputFilename + ": " + rmse);
        TestMetrics.log("Filters", "Elliptic " + inputFilename, rmse);
        assertTrue(rmse < TOLERANCE, "RMSE " + rmse + " is too high");
    }

    @Test
    public void testEllipticOrder4() throws IOException {
        // Based on python generation script: order 4, rp 1dB, rs 20dB, cutoff 20, sr
        // 250
        runTest("ellip_input1.txt", "ellip_output1.txt", 4, 20, 250, 1.0, 20.0);
    }
}
