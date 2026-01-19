package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hissain.jscipy.signal.filter.MedFilt;
import com.hissain.jscipy.signal.util.LoadTxt;

public class MedFiltTest {

    private static final double TOLERANCE = 1e-10;

    @Test
    void testMedFilt() throws IOException {
        double[] input = LoadTxt.read("datasets/medfilt/medfilt_input.txt");
        double[] expected = LoadTxt.read("datasets/medfilt/medfilt_output.txt");
        int kernelSize = (int) LoadTxt.read("datasets/medfilt/medfilt_kernel.txt")[0];

        MedFilt medFilt = new MedFilt();
        double[] actual = medFilt.medfilt(input, kernelSize);

        com.hissain.jscipy.signal.util.LoadTxt.write("datasets/medfilt/medfilt_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for MedFilt: " + rmse);

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
