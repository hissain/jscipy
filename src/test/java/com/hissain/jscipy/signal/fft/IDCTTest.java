package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.util.LoadTxt;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IDCTTest {

    private static final String DATA_DIR = System.getProperty("user.dir") + "/datasets/dct/";

    @Test
    public void testIDCTBasic() throws IOException {
        double[] input = LoadTxt.read(DATA_DIR + "dct_basic_output.txt"); // DCT output is input to IDCT
        double[] expected = LoadTxt.read(DATA_DIR + "dct_basic_idct.txt"); // Expected is original signal (mostly)

        DCT dct = new DCT();
        double[] result = dct.idct(input);

        // Save result for plotting using standard IO
        try (java.io.PrintWriter out = new java.io.PrintWriter(DATA_DIR + "dct_basic_idct_java.txt")) {
            for (double val : result) {
                out.printf(java.util.Locale.US, "%.16e%n", val);
            }
        }

        assertEquals(expected.length, result.length, "Length mismatch");
        double rmse = calculateRMSE(expected, result);
        System.out.println("IDCT Basic RMSE: " + rmse);
        assertEquals(0.0, rmse, 1e-14, "IDCT Basic failed");
    }

    @Test
    public void testIDCTRandomEven() throws IOException {
        double[] input = LoadTxt.read(DATA_DIR + "dct_random_even_output.txt");
        double[] expected = LoadTxt.read(DATA_DIR + "dct_random_even_idct.txt");

        DCT dct = new DCT();
        double[] result = dct.idct(input);

        double rmse = calculateRMSE(expected, result);
        System.out.println("IDCT Random Even RMSE: " + rmse);
        assertEquals(0.0, rmse, 1e-14, "IDCT Random Even failed");
    }

    @Test
    public void testIDCTRandomOdd() throws IOException {
        double[] input = LoadTxt.read(DATA_DIR + "dct_random_odd_output.txt");
        double[] expected = LoadTxt.read(DATA_DIR + "dct_random_odd_idct.txt");

        DCT dct = new DCT();
        double[] result = dct.idct(input);

        double rmse = calculateRMSE(expected, result);
        System.out.println("IDCT Random Odd RMSE: " + rmse);
        assertEquals(0.0, rmse, 1e-14, "IDCT Random Odd failed");
    }

    @Test
    public void testIDCTOrtho() throws IOException {
        double[] input = LoadTxt.read(DATA_DIR + "dct_ortho_output.txt");
        double[] expected = LoadTxt.read(DATA_DIR + "dct_ortho_idct.txt");

        DCT dct = new DCT();
        double[] result = dct.idct(input, true); // true for ortho

        double rmse = calculateRMSE(expected, result);
        System.out.println("IDCT Ortho RMSE: " + rmse);
        assertEquals(0.0, rmse, 1e-14, "IDCT Ortho failed");
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException("Arrays differ in length");
        }
        double sumSq = 0;
        for (int i = 0; i < expected.length; i++) {
            double diff = expected[i] - actual[i];
            sumSq += diff * diff;
        }
        return Math.sqrt(sumSq / expected.length);
    }
}
