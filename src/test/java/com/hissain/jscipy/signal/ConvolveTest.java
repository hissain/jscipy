package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.hissain.jscipy.signal.util.LoadTxt;

public class ConvolveTest {

    private static final double TOLERANCE = 5e-14;

    @Test
    void testConvolve() throws IOException {
        double[] signal = LoadTxt.read("datasets/convolve/convolve_input_signal.txt");
        double[] window = LoadTxt.read("datasets/convolve/convolve_input_window.txt");
        double[] expected = LoadTxt.read("datasets/convolve/convolve_output.txt");

        Convolve convolve = new Convolve();
        double[] actual = convolve.convolve(signal, window, ConvolutionMode.SAME);

        com.hissain.jscipy.signal.util.LoadTxt.write("datasets/convolve/convolve_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Convolve: " + rmse);

        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
        assertArrayEquals(expected, actual, TOLERANCE);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }
}
