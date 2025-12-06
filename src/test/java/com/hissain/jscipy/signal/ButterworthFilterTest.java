package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hissain.jscipy.signal.butterworth.ButterworthFilter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ButterworthFilterTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 2.0;

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

    private void runTest(String inputFilename, String expectedOutputFilename, int order, double cutoff, double sampleRate) throws IOException {
        double[] signal = readDataFile(inputFilename);
        double[] expectedOutput = readDataFile(expectedOutputFilename);
        ButterworthFilter filter = new ButterworthFilter();
        double[] output = filter.filtfilt(signal, sampleRate, cutoff, order);

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
        assertTrue(rmse < TOLERANCE);
    }

    @Test
    public void testButterworthOrder2() throws IOException {
        runTest("butterworth_input1.txt", "butterworth_output1.txt", 2, 20, 250);
    }

    @Test
    public void testButterworthOrder3() throws IOException {
        runTest("butterworth_input2.txt", "butterworth_output2.txt", 3, 20, 250);
    }

    @Test
    public void testButterworthOrder4() throws IOException {
        runTest("butterworth_input3.txt", "butterworth_output3.txt", 4, 20, 250);
    }
}
