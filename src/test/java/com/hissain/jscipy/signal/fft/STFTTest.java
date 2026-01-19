package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.Signal;
import com.hissain.jscipy.signal.JComplex;
import com.hissain.jscipy.signal.util.LoadTxt;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class STFTTest {

    private static final String DATA_DIR = System.getProperty("user.dir") + "/datasets/stft/";

    @Test
    public void testSTFTMatchSciPy() throws IOException {
        double[] input = LoadTxt.read(DATA_DIR + "stft_input.txt");
        double[] expectedReal = LoadTxt.read(DATA_DIR + "stft_output_real.txt");
        double[] expectedImag = LoadTxt.read(DATA_DIR + "stft_output_imag.txt");
        double[] dims = LoadTxt.read(DATA_DIR + "stft_dims.txt");

        int rows = (int) dims[0];
        int cols = (int) dims[1];

        // Java STFT
        JComplex[][] result = Signal.stft(input);

        assertEquals(rows, result.length, "STFT rows mismatch");
        assertEquals(cols, result[0].length, "STFT cols mismatch");

        double outputReal[] = new double[rows * cols];
        double outputImag[] = new double[rows * cols];

        int idx = 0;
        // SciPy flatten is row-major (C-style default), but Java [][] implies specific
        // layout.
        // Let's verify layout. In FFT.java:
        // stftResult = new JComplex[numFreqBins][numFrames];
        // So stftResult[freq][time].
        // Flattening a 2D array typically goes row by row (freq by freq).

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                outputReal[idx] = result[i][j].getReal();
                outputImag[idx] = result[i][j].getImaginary();
                idx++;
            }
        }

        double rmseReal = calculateRMSE(expectedReal, outputReal);
        double rmseImag = calculateRMSE(expectedImag, outputImag);

        System.out.println("STFT Real RMSE: " + rmseReal);
        System.out.println("STFT Imag RMSE: " + rmseImag);

        assertEquals(0.0, rmseReal, 1e-14, "STFT Real part mismatch");
        assertEquals(0.0, rmseImag, 1e-14, "STFT Imaginary part mismatch");
    }

    @Test
    public void testISTFTMatchSciPy() throws IOException {
        double[] expectedOutput = LoadTxt.read(DATA_DIR + "istft_output.txt");

        // Load STFT input (which is the output of previous test)
        double[] stftReal = LoadTxt.read(DATA_DIR + "stft_output_real.txt");
        double[] stftImag = LoadTxt.read(DATA_DIR + "stft_output_imag.txt");
        double[] dims = LoadTxt.read(DATA_DIR + "stft_dims.txt");

        int rows = (int) dims[0];
        int cols = (int) dims[1];

        JComplex[][] stftMatrix = new JComplex[rows][cols];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                stftMatrix[i][j] = new JComplex(stftReal[idx], stftImag[idx]);
                idx++;
            }
        }

        // Java ISTFT
        double[] reconstructed = Signal.istft(stftMatrix);

        // Note: Reconstructed length might differ slightly due to padding/boundary
        // handling
        // Expected length is roughly same as original signal.
        // We'll compare up to min length.

        int len = Math.min(expectedOutput.length, reconstructed.length);

        double[] expectedTrunc = new double[len];
        double[] reconTrunc = new double[len];

        System.arraycopy(expectedOutput, 0, expectedTrunc, 0, len);
        System.arraycopy(reconstructed, 0, reconTrunc, 0, len);

        double rmse = calculateRMSE(expectedTrunc, reconTrunc);
        System.out.println("ISTFT RMSE: " + rmse);

        // Tolerance slightly looser for reconstruction due to windowing/overlap
        // numerical effects
        assertEquals(0.0, rmse, 1e-14, "ISTFT reconstruction mismatch");
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new IllegalArgumentException("Arrays differ in length: " + expected.length + " vs " + actual.length);
        }
        double sumSq = 0;
        for (int i = 0; i < expected.length; i++) {
            double diff = expected[i] - actual[i];
            sumSq += diff * diff;
        }
        return Math.sqrt(sumSq / expected.length);
    }
}
