package com.hissain.jscipy.signal.fft;

import org.junit.jupiter.api.Test;

import com.hissain.jscipy.signal.JComplex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hissain.jscipy.TestMetrics;

public class FFTTest {

    private final FFT fft = new FFT();
    private static final double FFT_RMSE_TOLERANCE = 1e-14;
    private static final double FFT_MAX_ERROR_TOLERANCE = 1e-13;
    private static final double STFT_RMSE_TOLERANCE = 1e-10;
    private static final double STFT_MAX_ERROR_TOLERANCE = 1e-9;

    @Test
    public void testFFTData1() throws IOException {
        double[] input = readData("datasets/fft/fft_input_1.txt");
        JComplex[] expected = readComplexData("datasets/fft/fft_output_1.txt");
        JComplex[] actual = fft.fft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/fft_output_java_1.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for FFT Data 1: " + rmse);
        System.out.println("Max Error for FFT Data 1: " + maxError);
        com.hissain.jscipy.TestMetrics.log("FFT", "FFT Data 1", rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testFFTData2() throws IOException {
        double[] input = readData("datasets/fft/fft_input_2.txt");
        JComplex[] expected = readComplexData("datasets/fft/fft_output_2.txt");
        JComplex[] actual = fft.fft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/fft_output_java_2.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for FFT Data 2: " + rmse);
        System.out.println("Max Error for FFT Data 2: " + maxError);
        com.hissain.jscipy.TestMetrics.log("FFT", "FFT Data 2", rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testRFFTData1() throws IOException {
        double[] input = readData("datasets/fft/fft_input_1.txt");
        JComplex[] expected = readComplexData("datasets/fft/rfft_output_1.txt");
        JComplex[] actual = fft.rfft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/rfft_output_java_1.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for RFFT Data 1: " + rmse);
        System.out.println("Max Error for RFFT Data 1: " + maxError);
        com.hissain.jscipy.TestMetrics.log("FFT", "RFFT Data 1", rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testRFFTData2() throws IOException {
        double[] input = readData("datasets/fft/fft_input_2.txt");
        JComplex[] expected = readComplexData("datasets/fft/rfft_output_2.txt");
        JComplex[] actual = fft.rfft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/rfft_output_java_2.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        double maxError = calculateMaxError(expected, actual);
        System.out.println("RMSE for RFFT Data 2: " + rmse);
        System.out.println("Max Error for RFFT Data 2: " + maxError);
        com.hissain.jscipy.TestMetrics.log("FFT", "RFFT Data 2", rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIFFTData1() throws IOException {
        JComplex[] input = readComplexData("datasets/fft/fft_output_1.txt");
        JComplex[] expected = readComplexData("datasets/fft/ifft_output_1.txt");
        JComplex[] actual = fft.ifft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/ifft_output_java_1.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IFFT Data 1: " + rmse);
        com.hissain.jscipy.TestMetrics.log("FFT", "IFFT Data 1", rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIFFTData2() throws IOException {
        JComplex[] input = readComplexData("datasets/fft/fft_output_2.txt");
        JComplex[] expected = readComplexData("datasets/fft/ifft_output_2.txt");
        JComplex[] actual = fft.ifft(input);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/ifft_output_java_2.txt")) {
            for (JComplex c : actual) {
                writer.println(c.getReal() + " " + c.getImaginary());
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IFFT Data 2: " + rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertComplexArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIRFFTData1() throws IOException {
        JComplex[] input = readComplexData("datasets/fft/rfft_output_1.txt");
        double[] expected = readData("datasets/fft/irfft_output_1.txt");
        double[] actual = fft.irfft(input, expected.length);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/irfft_output_java_1.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IRFFT Data 1: " + rmse);
        com.hissain.jscipy.TestMetrics.log("FFT", "IRFFT Data 1", rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testIRFFTData2() throws IOException {
        JComplex[] input = readComplexData("datasets/fft/rfft_output_2.txt");
        double[] expected = readData("datasets/fft/irfft_output_2.txt");
        double[] actual = fft.irfft(input, expected.length);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/irfft_output_java_2.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for IRFFT Data 2: " + rmse);
        assertTrue(rmse < FFT_RMSE_TOLERANCE, "RMSE is too high");
        assertArrayEquals(expected, actual, FFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testSTFTData1() throws IOException {
        double[] input = readData("datasets/fft/stft_input_1.txt");
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_1.txt");
        JComplex[][] expected = readSTFTData("datasets/fft/stft_output_1.txt",
                "datasets/fft/stft_output_1_shape.txt");

        JComplex[][] actual = fft.stft(input, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", true);

        writeSTFTData("datasets/fft/stft_output_java_1.txt", actual);

        double rmse = calculateSTFTRMSE(expected, actual);
        double maxError = calculateSTFTMaxError(expected, actual);
        System.out.println("RMSE for STFT Data 1: " + rmse);
        System.out.println("Max Error for STFT Data 1: " + maxError);
        com.hissain.jscipy.TestMetrics.log("FFT", "STFT Data 1", rmse);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertSTFTEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testSTFTData2() throws IOException {
        double[] input = readData("datasets/fft/stft_input_2.txt");
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_2.txt");
        JComplex[][] expected = readSTFTData("datasets/fft/stft_output_2.txt",
                "datasets/fft/stft_output_2_shape.txt");

        JComplex[][] actual = fft.stft(input, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", true);

        writeSTFTData("datasets/fft/stft_output_java_2.txt", actual);

        double rmse = calculateSTFTRMSE(expected, actual);
        double maxError = calculateSTFTMaxError(expected, actual);
        System.out.println("RMSE for STFT Data 2: " + rmse);
        System.out.println("Max Error for STFT Data 2: " + maxError);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertSTFTEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testSTFTData3() throws IOException {
        double[] input = readData("datasets/fft/stft_input_3.txt");
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_3.txt");
        JComplex[][] expected = readSTFTData("datasets/fft/stft_output_3.txt",
                "datasets/fft/stft_output_3_shape.txt");

        JComplex[][] actual = fft.stft(input, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", true);

        writeSTFTData("datasets/fft/stft_output_java_3.txt", actual);

        double rmse = calculateSTFTRMSE(expected, actual);
        double maxError = calculateSTFTMaxError(expected, actual);
        System.out.println("RMSE for STFT Data 3: " + rmse);
        System.out.println("Max Error for STFT Data 3: " + maxError);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertSTFTEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testSTFTData4() throws IOException {
        double[] input = readData("datasets/fft/stft_input_4.txt");
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_4.txt");
        JComplex[][] expected = readSTFTData("datasets/fft/stft_output_4.txt",
                "datasets/fft/stft_output_4_shape.txt");

        JComplex[][] actual = fft.stft(input, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", true);

        writeSTFTData("datasets/fft/stft_output_java_4.txt", actual);

        double rmse = calculateSTFTRMSE(expected, actual);
        double maxError = calculateSTFTMaxError(expected, actual);
        System.out.println("RMSE for STFT Data 4: " + rmse);
        System.out.println("Max Error for STFT Data 4: " + maxError);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertSTFTEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testISTFTData1() throws IOException {
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_1.txt");
        JComplex[][] stftInput = readSTFTData("datasets/fft/stft_output_1.txt",
                "datasets/fft/stft_output_1_shape.txt");
        double[] expected = readData("datasets/fft/istft_output_1.txt");

        double[] actual = fft.istft(stftInput, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", -1);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/istft_output_java_1.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for ISTFT Data 1: " + rmse);
        com.hissain.jscipy.TestMetrics.log("FFT", "ISTFT Data 1", rmse);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertArrayEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testISTFTData2() throws IOException {
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_2.txt");
        JComplex[][] stftInput = readSTFTData("datasets/fft/stft_output_2.txt",
                "datasets/fft/stft_output_2_shape.txt");
        double[] expected = readData("datasets/fft/istft_output_2.txt");

        double[] actual = fft.istft(stftInput, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", -1);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/istft_output_java_2.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for ISTFT Data 2: " + rmse);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertArrayEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testISTFTData3() throws IOException {
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_3.txt");
        JComplex[][] stftInput = readSTFTData("datasets/fft/stft_output_3.txt",
                "datasets/fft/stft_output_3_shape.txt");
        double[] expected = readData("datasets/fft/istft_output_3.txt");

        double[] actual = fft.istft(stftInput, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", -1);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/istft_output_java_3.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for ISTFT Data 3: " + rmse);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertArrayEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    @Test
    public void testISTFTData4() throws IOException {
        Map<String, Integer> params = readSTFTParams("datasets/fft/stft_params_4.txt");
        JComplex[][] stftInput = readSTFTData("datasets/fft/stft_output_4.txt",
                "datasets/fft/stft_output_4_shape.txt");
        double[] expected = readData("datasets/fft/istft_output_4.txt");

        double[] actual = fft.istft(stftInput, params.get("nperseg"), params.get("noverlap"),
                params.get("nfft"), null, "zeros", -1);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/fft/istft_output_java_4.txt")) {
            for (double v : actual) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expected, actual);
        System.out.println("RMSE for ISTFT Data 4: " + rmse);
        assertTrue(rmse < STFT_RMSE_TOLERANCE, "RMSE is too high");
        assertArrayEquals(expected, actual, STFT_MAX_ERROR_TOLERANCE);
    }

    private double[] readData(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private JComplex[] readComplexData(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.map(line -> {
                String[] parts = line.trim().split("\\s+");
                double real = Double.parseDouble(parts[0]);
                double imag = Double.parseDouble(parts[1]);
                return new JComplex(real, imag);
            }).toArray(JComplex[]::new);
        }
    }

    private Map<String, Integer> readSTFTParams(String filePath) throws IOException {
        Map<String, Integer> params = new HashMap<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                String[] parts = line.trim().split("=");
                if (parts.length == 2) {
                    params.put(parts[0], Integer.parseInt(parts[1]));
                }
            });
        }
        return params;
    }

    private JComplex[][] readSTFTData(String dataPath, String shapePath) throws IOException {
        // Read shape
        String[] shapeParts = Files.lines(Paths.get(shapePath))
                .findFirst()
                .orElseThrow()
                .trim()
                .split("\\s+");
        int numFreqBins = Integer.parseInt(shapeParts[0]);
        int numTimeFrames = Integer.parseInt(shapeParts[1]);

        // Read complex data
        JComplex[] flatData = readComplexData(dataPath);

        // Verify data length matches expected dimensions
        int expectedLength = numFreqBins * numTimeFrames;
        if (flatData.length != expectedLength) {
            throw new IllegalStateException(
                    String.format("Data length mismatch: expected %d (%dx%d), got %d",
                            expectedLength, numFreqBins, numTimeFrames, flatData.length));
        }

        // Reshape to 2D array [freq][time] - column-major order
        JComplex[][] result = new JComplex[numFreqBins][numTimeFrames];
        int idx = 0;
        for (int t = 0; t < numTimeFrames; t++) {
            for (int f = 0; f < numFreqBins; f++) {
                if (idx >= flatData.length) {
                    throw new ArrayIndexOutOfBoundsException(
                            String.format("Index %d out of bounds for data length %d at f=%d, t=%d",
                                    idx, flatData.length, f, t));
                }
                result[f][t] = flatData[idx++];
            }
        }

        return result;
    }

    private void writeSTFTData(String filePath, JComplex[][] data) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
            int numFreqBins = data.length;
            int numTimeFrames = data[0].length;

            // Write in column-major order to match Python
            for (int t = 0; t < numTimeFrames; t++) {
                for (int f = 0; f < numFreqBins; f++) {
                    writer.println(data[f][t].getReal() + " " + data[f][t].getImaginary());
                }
            }
        }
    }

    private void assertComplexArrayEquals(JComplex[] expected, JComplex[] actual, double delta) {
        assertEquals(expected.length, actual.length, "Array lengths are not equal");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].getReal(), actual[i].getReal(), delta,
                    "Real part of element " + i + " is not equal");
            assertEquals(expected[i].getImaginary(), actual[i].getImaginary(), delta,
                    "Imaginary part of element " + i + " is not equal");
        }
    }

    private void assertSTFTEquals(JComplex[][] expected, JComplex[][] actual, double delta) {
        assertEquals(expected.length, actual.length, "Number of frequency bins not equal");
        assertEquals(expected[0].length, actual[0].length, "Number of time frames not equal");

        for (int f = 0; f < expected.length; f++) {
            for (int t = 0; t < expected[0].length; t++) {
                assertEquals(expected[f][t].getReal(), actual[f][t].getReal(), delta,
                        "Real part at [" + f + "][" + t + "] not equal");
                assertEquals(expected[f][t].getImaginary(), actual[f][t].getImaginary(), delta,
                        "Imaginary part at [" + f + "][" + t + "] not equal");
            }
        }
    }

    private double calculateRMSE(JComplex[] expected, JComplex[] actual) {
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            double realDiff = expected[i].getReal() - actual[i].getReal();
            double imagDiff = expected[i].getImaginary() - actual[i].getImaginary();
            sumSquareError += realDiff * realDiff + imagDiff * imagDiff;
        }
        return Math.sqrt(sumSquareError / expected.length);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        double sumSquareError = 0;
        int minLen = Math.min(expected.length, actual.length);
        for (int i = 0; i < minLen; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / minLen);
    }

    private double calculateSTFTRMSE(JComplex[][] expected, JComplex[][] actual) {
        double sumSquareError = 0;
        int count = 0;
        for (int f = 0; f < expected.length; f++) {
            for (int t = 0; t < expected[0].length; t++) {
                double realDiff = expected[f][t].getReal() - actual[f][t].getReal();
                double imagDiff = expected[f][t].getImaginary() - actual[f][t].getImaginary();
                sumSquareError += realDiff * realDiff + imagDiff * imagDiff;
                count++;
            }
        }
        return Math.sqrt(sumSquareError / count);
    }

    private double calculateMaxError(JComplex[] expected, JComplex[] actual) {
        double maxError = 0;
        for (int i = 0; i < expected.length; i++) {
            double realDiff = Math.abs(expected[i].getReal() - actual[i].getReal());
            double imagDiff = Math.abs(expected[i].getImaginary() - actual[i].getImaginary());
            double currentMax = Math.max(realDiff, imagDiff);
            if (currentMax > maxError) {
                maxError = currentMax;
            }
        }
        return maxError;
    }

    private double calculateSTFTMaxError(JComplex[][] expected, JComplex[][] actual) {
        double maxError = 0;
        for (int f = 0; f < expected.length; f++) {
            for (int t = 0; t < expected[0].length; t++) {
                double realDiff = Math.abs(expected[f][t].getReal() - actual[f][t].getReal());
                double imagDiff = Math.abs(expected[f][t].getImaginary() - actual[f][t].getImaginary());
                double currentMax = Math.max(realDiff, imagDiff);
                if (currentMax > maxError) {
                    maxError = currentMax;
                }
            }
        }
        return maxError;
    }
}