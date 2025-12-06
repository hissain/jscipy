package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WelchTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 1e-4;

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

    private void runTest(String inputFilename, String expectedFreqFilename, String expectedPsdFilename, double sampleRate, int nperseg) throws IOException {
        double[] signal = readDataFile(inputFilename);
        double[] expectedFreq = readDataFile(expectedFreqFilename);
        double[] expectedPsd = readDataFile(expectedPsdFilename);

        WelchResult result = Signal.welch(signal, sampleRate, nperseg);

        // Save output for plotting
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + "welch_output_psd1_java.txt")) {
            for (double v : result.Pxx) {
                writer.println(v);
            }
        }

        // Check Frequencies
        double freqRmse = 0;
        for (int i = 0; i < result.f.length; i++) {
            freqRmse += Math.pow(result.f[i] - expectedFreq[i], 2);
        }
        freqRmse = Math.sqrt(freqRmse / result.f.length);
        System.out.println("Frequency RMSE: " + freqRmse);
        assertTrue(freqRmse < TOLERANCE, "Frequency RMSE too high");

        // Check PSD
        double psdRmse = 0;
        for (int i = 0; i < result.Pxx.length; i++) {
            psdRmse += Math.pow(result.Pxx[i] - expectedPsd[i], 2);
        }
        psdRmse = Math.sqrt(psdRmse / result.Pxx.length);
        System.out.println("PSD RMSE: " + psdRmse);
        assertTrue(psdRmse < TOLERANCE, "PSD RMSE too high");
    }

    @Test
    public void testWelch1() throws IOException {
        runTest("welch_input1.txt", "welch_output_freq1.txt", "welch_output_psd1.txt", 1000.0, 256);
    }
}
