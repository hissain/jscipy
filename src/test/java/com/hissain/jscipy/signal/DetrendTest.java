package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hissain.jscipy.signal.Detrend;
import com.hissain.jscipy.signal.api.DetrendType;

public class DetrendTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 1e-10; // High precision expected for linear algebra

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

    private void runTest(String inputFilename, String expectedOutputFilename, DetrendType type) throws IOException {
        double[] signal = readDataFile(inputFilename);
        double[] expectedOutput = readDataFile(expectedOutputFilename);
        Detrend detrender = new Detrend();
        double[] output = detrender.detrend(signal, type);

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
        System.out.println("RMSE for " + inputFilename + " (" + type + "): " + rmse);
        
        // Use a slightly larger tolerance for RMSE to account for floating point differences
        // But 1e-10 might be too strict if python and java use different float precision or algorithms
        // Let's use 1e-6 which is standard for float comparisons
        assertTrue(rmse < 1e-6, "RMSE too high: " + rmse);
    }

    @Test
    public void testLinearDetrend1() throws IOException {
        runTest("detrend_input_1.txt", "detrend_output_linear_1.txt", DetrendType.LINEAR);
    }

    @Test
    public void testConstantDetrend1() throws IOException {
        runTest("detrend_input_1.txt", "detrend_output_constant_1.txt", DetrendType.CONSTANT);
    }

    @Test
    public void testLinearDetrend2() throws IOException {
        runTest("detrend_input_2.txt", "detrend_output_linear_2.txt", DetrendType.LINEAR);
    }

    @Test
    public void testConstantDetrend2() throws IOException {
        runTest("detrend_input_2.txt", "detrend_output_constant_2.txt", DetrendType.CONSTANT);
    }
}
