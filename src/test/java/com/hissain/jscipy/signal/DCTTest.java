package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class DCTTest {

    private static final String DATASETS_DIR = "datasets";
    private static final double TOLERANCE = 1e-12;

    private double[] loadData(String filename) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(DATASETS_DIR, filename))) {
            return lines.mapToDouble(Double::parseDouble).toArray();
        }
    }

    private void saveData(String filename, double[] data) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (double d : data) {
            sb.append(d).append("\n");
        }
        Files.write(Paths.get(DATASETS_DIR, filename), sb.toString().getBytes());
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException("Arrays must have the same length");
        }
        double sumSq = 0.0;
        for (int i = 0; i < expected.length; i++) {
            double diff = expected[i] - actual[i];
            sumSq += diff * diff;
        }
        return java.lang.Math.sqrt(sumSq / expected.length);
    }

    @Test
    void testDctBasic() throws IOException {
        double[] x = loadData("dct_basic_input.txt");
        double[] expected = loadData("dct_basic_output.txt");

        double[] actual = Signal.dct(x);

        double rmse = calculateRMSE(expected, actual);
        System.out.printf("DCT Basic RMSE: %.6e%n", rmse);

        assertArrayEquals(expected, actual, TOLERANCE, "DCT basic mismatch");
    }

    @Test
    void testDctRandomEven() throws IOException {
        double[] x = loadData("dct_random_even_input.txt");
        double[] expected = loadData("dct_random_even_output.txt");

        double[] actual = Signal.dct(x);

        saveData("dct_random_even_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.printf("DCT Random Even RMSE: %.6e%n", rmse);

        assertArrayEquals(expected, actual, TOLERANCE, "DCT random even length mismatch");
    }

    @Test
    void testDctRandomOdd() throws IOException {
        double[] x = loadData("dct_random_odd_input.txt");
        double[] expected = loadData("dct_random_odd_output.txt");

        double[] actual = Signal.dct(x);

        double rmse = calculateRMSE(expected, actual);
        System.out.printf("DCT Random Odd RMSE: %.6e%n", rmse);

        assertArrayEquals(expected, actual, TOLERANCE, "DCT random odd length mismatch");
    }

    @Test
    void testDctOrtho() throws IOException {
        double[] x = loadData("dct_ortho_input.txt");
        double[] expected = loadData("dct_ortho_output.txt");

        double[] actual = Signal.dct(x, true); // Use ortho

        double rmse = calculateRMSE(expected, actual);
        System.out.printf("DCT Ortho RMSE: %.6e%n", rmse);

        assertArrayEquals(expected, actual, TOLERANCE, "DCT ortho mismatch");
    }
}
