package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import com.hissain.jscipy.signal.util.LoadTxt;

public class ConvolveTest {

    @Test
    void testConvolve() throws IOException {
        double[] signal = LoadTxt.read("datasets/convolve_input_signal.txt");
        double[] window = LoadTxt.read("datasets/convolve_input_window.txt");
        double[] expected = LoadTxt.read("datasets/convolve_output.txt");

        Convolve convolve = new Convolve();
        double[] actual = convolve.convolve(signal, window, "same");

        com.hissain.jscipy.signal.util.LoadTxt.write("datasets/convolve_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for Convolve: " + rmse);

        assertArrayEquals(expected, actual, 1e-6);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }
}
