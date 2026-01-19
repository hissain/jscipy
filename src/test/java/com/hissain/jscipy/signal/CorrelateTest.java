package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CorrelateTest {

    private static final String DATASETS_DIR = "datasets";

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

        assertArrayEquals(expected, actual, 1e-9, "Failed for test: " + testId);
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
