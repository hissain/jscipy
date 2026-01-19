package com.hissain.jscipy.signal.filter;

import com.hissain.jscipy.Signal;
import com.hissain.jscipy.signal.util.LoadTxt;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SOSFilterTest {

    private static final String DATA_DIR = System.getProperty("user.dir") + "/datasets/";

    @Test
    public void testSOSFiltMatchSciPy() throws IOException {
        double[] input = LoadTxt.read(DATA_DIR + "stft_input.txt"); // Reusing input signal
        double[] sosFlat = LoadTxt.read(DATA_DIR + "sos_coeffs.txt");
        double[] dims = LoadTxt.read(DATA_DIR + "sos_dims.txt");
        double[] expectedOutput = LoadTxt.read(DATA_DIR + "sos_filtered_output.txt");

        int nSections = (int) dims[0];
        int nCoeffs = (int) dims[1]; // Should be 6

        assertEquals(6, nCoeffs, "SOS coeffs dim should be 6");

        // Reconstruct SOS array
        double[][] sos = new double[nSections][6];
        int idx = 0;
        for (int i = 0; i < nSections; i++) {
            for (int j = 0; j < 6; j++) {
                sos[i][j] = sosFlat[idx++];
            }
        }

        // Run SOS Filter via Signal facade
        double[] result = Signal.sosfilt(input, sos);

        double rmse = calculateRMSE(expectedOutput, result);
        System.out.println("SOS Filter RMSE: " + rmse);

        assertEquals(0.0, rmse, 1e-14, "SOS Filter output mismatch");
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
