package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CorrelateTest {

    private static final String DATASETS_DIR = "datasets/correlate";
    private static final double TOLERANCE = 1e-14;

    private double[] loadData(String filename) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(DATASETS_DIR, filename))) {
            return lines.mapToDouble(Double::parseDouble).toArray();
        }
    }

    private void runTest(String testId, ConvolutionMode mode) throws IOException {
        double[] in1 = loadData(testId + "_input1.txt");
        double[] in2 = loadData(testId + "_input2.txt");
        double[] expected = loadData(testId + "_output.txt");

        double[] actual = Signal.correlate(in1, in2, mode);

        // Write actual output for visualization
        com.hissain.jscipy.signal.util.LoadTxt.write(DATASETS_DIR + "/" + testId + "_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for " + testId + ": " + rmse);

        assertArrayEquals(expected, actual, TOLERANCE, "Failed for test: " + testId);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException(
                    "Array lengths differ: expected=" + expected.length + ", actual=" + actual.length);
        }
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    @Test
    void testBasicFull() throws IOException {
        runTest("correlate_basic_full", ConvolutionMode.FULL);
    }

    @Test
    void testBasicSame() throws IOException {
        runTest("correlate_basic_same", ConvolutionMode.SAME);
    }

    @Test
    void testBasicValid() throws IOException {
        runTest("correlate_basic_valid", ConvolutionMode.VALID);
    }

    @Test
    void testRandomFull() throws IOException {
        runTest("correlate_random_full", ConvolutionMode.FULL);
    }

    @Test
    void testRandomSame() throws IOException {
        runTest("correlate_random_same", ConvolutionMode.SAME);
    }

    @Test
    void testRandomValid() throws IOException {
        runTest("correlate_random_valid", ConvolutionMode.VALID);
    }
}
