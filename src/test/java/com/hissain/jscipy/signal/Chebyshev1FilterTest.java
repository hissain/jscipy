package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hissain.jscipy.signal.filter.Chebyshev1;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Chebyshev1FilterTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 1.0; 

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

    private void runTest(String inputFilename, String expectedOutputFilename, int order, double cutoff, double sampleRate, double rippleDb) throws IOException {
        double[] signal = readDataFile(inputFilename);
        double[] expectedOutput = readDataFile(expectedOutputFilename);
        double[] output = Chebyshev1.filtfilt(signal, sampleRate, cutoff, order, rippleDb);

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
        assertTrue(rmse < TOLERANCE, "RMSE " + rmse + " is too high");
    }

    @Test
    public void testChebyshev1Order4() throws IOException {
        // Based on python generation script: order 4, rp 1dB, cutoff 20, sr 250
        runTest("cheby1_input1.txt", "cheby1_output1.txt", 4, 20, 250, 1.0);
    }
}
