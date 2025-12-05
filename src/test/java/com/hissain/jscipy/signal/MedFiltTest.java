package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import com.hissain.jscipy.signal.util.LoadTxt;

public class MedFiltTest {

    @Test
    void testMedFilt() throws IOException {
        double[] input = LoadTxt.read("datasets/medfilt_input.txt");
        double[] expected = LoadTxt.read("datasets/medfilt_output.txt");
        int kernelSize = (int) LoadTxt.read("datasets/medfilt_kernel.txt")[0];

        MedFilt medFilt = new MedFilt();
        double[] actual = medFilt.medfilt(input, kernelSize);

        com.hissain.jscipy.signal.util.LoadTxt.write("datasets/medfilt_output_java.txt", actual);

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for MedFilt: " + rmse);

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
