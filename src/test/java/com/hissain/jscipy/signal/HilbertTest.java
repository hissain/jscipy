package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hissain.jscipy.signal.Hilbert;

public class HilbertTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";
    private static final double TOLERANCE = 1e-10;

    private double[] readDataFile(String filename) throws IOException {
        List<Double> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_DATA_DIR + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    data.add(Double.parseDouble(line));
                }
            }
        }
        return data.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private void runTest(String inputFilename, String expectedRealFilename, String expectedImagFilename) throws IOException {
        double[] signal = readDataFile(inputFilename);
        double[] expectedReal = readDataFile(expectedRealFilename);
        double[] expectedImag = readDataFile(expectedImagFilename);

        Hilbert hilbert = new Hilbert();
        Complex[] output = hilbert.hilbert(signal);

        double[] outputReal = new double[output.length];
        double[] outputImag = new double[output.length];

        for (int i = 0; i < output.length; i++) {
            outputReal[i] = output[i].getReal();
            outputImag[i] = output[i].getImaginary();
        }

        // Save Java output
        String outputRealFilename = expectedRealFilename.replace(".txt", "_java.txt");
        String outputImagFilename = expectedImagFilename.replace(".txt", "_java.txt");

        try (PrintWriter writer = new PrintWriter(TEST_DATA_DIR + outputRealFilename)) {
            for (double v : outputReal) {
                writer.println(v);
            }
        }
        try (PrintWriter writer = new PrintWriter(TEST_DATA_DIR + outputImagFilename)) {
            for (double v : outputImag) {
                writer.println(v);
            }
        }

        // RMSE Check
        double rmseReal = 0;
        double rmseImag = 0;
        for (int i = 0; i < output.length; i++) {
            rmseReal += Math.pow(outputReal[i] - expectedReal[i], 2);
            rmseImag += Math.pow(outputImag[i] - expectedImag[i], 2);
        }
        rmseReal = Math.sqrt(rmseReal / output.length);
        rmseImag = Math.sqrt(rmseImag / output.length);

        System.out.println("RMSE for " + inputFilename + " (Real): " + rmseReal);
        System.out.println("RMSE for " + inputFilename + " (Imag): " + rmseImag);

        assertTrue(rmseReal < TOLERANCE, "Real part RMSE too high: " + rmseReal);
        assertTrue(rmseImag < TOLERANCE, "Imaginary part RMSE too high: " + rmseImag);
    }

    @Test
    public void testHilbert1() throws IOException {
        runTest("hilbert_input_1.txt", "hilbert_output_1_real.txt", "hilbert_output_1_imag.txt");
    }

    @Test
    public void testHilbert2() throws IOException {
        runTest("hilbert_input_2.txt", "hilbert_output_2_real.txt", "hilbert_output_2_imag.txt");
    }
}
