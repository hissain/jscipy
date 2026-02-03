package com.hissain.jscipy.signal.filter;

import com.hissain.jscipy.Signal;
import com.hissain.jscipy.TestMetrics;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for filter transforms (BandPass, BandStop, HighPass).
 * These tests exercise the transform classes through the public filter APIs
 * to improve code coverage for internal transform implementation classes.
 */
public class FilterTransformTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/filters/";
    private static final double TOLERANCE = 1e-14;
    private static final double TOLERANCE_RELAXED = 5e-14; // For transforms with slightly higher numerical error

    private double[] readDataFile(String filename) throws IOException {
        List<Double> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
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

    // ========================================================================
    // HighPassTransform Coverage Tests
    // ========================================================================

    @Test
    public void testHighPassTransformViaButterworth() throws IOException {
        double[] signal = readDataFile(TEST_DATA_DIR + "transform_highpass_input.txt");
        double[] expected = readDataFile(TEST_DATA_DIR + "transform_highpass_output.txt");

        double fs = 250.0;
        double cutoff = 50.0;
        int order = 4;

        double[] output = Signal.filtfilt_highpass(signal, fs, cutoff, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);

        double rmse = calculateRMSE(output, expected);
        System.out.println("HighPass Transform RMSE: " + rmse);
        TestMetrics.log("Filters", "HighPass Transform Coverage", rmse);
        assertTrue(rmse < TOLERANCE, "HighPass Transform RMSE too high: " + rmse);
    }

    @Test
    public void testHighPassTransformViaChebyshev1() {
        // Test with synthetic data to ensure Chebyshev also uses HighPassTransform
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double cutoff = 20.0;
        int order = 3;
        double ripple = 1.0;

        double[] output = Signal.cheby1_filtfilt_highpass(signal, fs, cutoff, order, ripple);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
        // Just verify the filter runs successfully - transform is exercised
    }

    // ========================================================================
    // BandPassTransform Coverage Tests
    // ========================================================================

    @Test
    public void testBandPassTransformViaButterworth() throws IOException {
        double[] signal = readDataFile(TEST_DATA_DIR + "transform_bandpass_input.txt");
        double[] expected = readDataFile(TEST_DATA_DIR + "transform_bandpass_output.txt");

        double fs = 250.0;
        double centerFreq = 60.0;
        double bandwidth = 20.0;
        int order = 4;

        double[] output = Signal.filtfilt_bandpass(signal, fs, centerFreq, bandwidth, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);

        double rmse = calculateRMSE(output, expected);
        System.out.println("BandPass Transform RMSE: " + rmse);
        TestMetrics.log("Filters", "BandPass Transform Coverage", rmse);
        assertTrue(rmse < TOLERANCE, "BandPass Transform RMSE too high: " + rmse);
    }

    @Test
    public void testBandPassTransformViaChebyshev1() {
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double centerFreq = 25.0;
        double bandwidth = 10.0;
        int order = 3;
        double ripple = 1.0;

        double[] output = Signal.cheby1_filtfilt_bandpass(signal, fs, centerFreq, bandwidth, order, ripple);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    @Test
    public void testBandPassTransformNarrowBand() throws IOException {
        // Test with narrow bandwidth to exercise transform edge cases
        double[] signal = readDataFile(TEST_DATA_DIR + "transform_bandpass_input.txt");

        double fs = 250.0;
        double centerFreq = 60.0;
        double bandwidth = 5.0; // Narrow bandwidth
        int order = 2;

        double[] output = Signal.filtfilt_bandpass(signal, fs, centerFreq, bandwidth, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    // ========================================================================
    // BandStopTransform Coverage Tests
    // ========================================================================

    @Test
    public void testBandStopTransformViaButterworth() throws IOException {
        double[] signal = readDataFile(TEST_DATA_DIR + "transform_bandstop_input.txt");
        double[] expected = readDataFile(TEST_DATA_DIR + "transform_bandstop_output.txt");

        double fs = 250.0;
        double centerFreq = 60.0; // Notch out 60 Hz power line noise
        double bandwidth = 10.0;
        int order = 4;

        double[] output = Signal.filtfilt_bandstop(signal, fs, centerFreq, bandwidth, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);

        double rmse = calculateRMSE(output, expected);
        System.out.println("BandStop Transform RMSE: " + rmse);
        TestMetrics.log("Filters", "BandStop Transform Coverage", rmse);
        assertTrue(rmse < TOLERANCE_RELAXED, "BandStop Transform RMSE too high: " + rmse);
    }

    @Test
    public void testBandStopTransformViaChebyshev1() {
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double centerFreq = 25.0;
        double bandwidth = 10.0;
        int order = 3;
        double ripple = 1.0;

        double[] output = Signal.cheby1_filtfilt_bandstop(signal, fs, centerFreq, bandwidth, order, ripple);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    @Test
    public void testBandStopTransformNarrowNotch() {
        // Test narrow notch filter (typical for power line noise removal)
        double[] signal = generateTestSignal();
        double fs = 250.0;
        double centerFreq = 60.0;
        double bandwidth = 2.0; // Very narrow notch
        int order = 2;

        double[] output = Signal.filtfilt_bandstop(signal, fs, centerFreq, bandwidth, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    // ========================================================================
    // Additional Coverage Tests for Various Filter Types
    // ========================================================================

    @Test
    public void testHighPassTransformLowFrequencyCutoff() {
        // Test low cutoff frequency to exercise transform edge cases
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double cutoff = 5.0; // Low cutoff
        int order = 2;

        double[] output = Signal.filtfilt_highpass(signal, fs, cutoff, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    @Test
    public void testHighPassTransformHighFrequencyCutoff() {
        // Test higher cutoff frequency
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double cutoff = 40.0; // High cutoff (near Nyquist)
        int order = 2;

        double[] output = Signal.filtfilt_highpass(signal, fs, cutoff, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    @Test
    public void testBandPassTransformLowCenterFrequency() {
        // Test with low center frequency
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double centerFreq = 10.0;
        double bandwidth = 5.0;
        int order = 2;

        double[] output = Signal.filtfilt_bandpass(signal, fs, centerFreq, bandwidth, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    @Test
    public void testBandStopTransformLowCenterFrequency() {
        // Test bandstop with low center frequency
        double[] signal = generateTestSignal();
        double fs = 100.0;
        double centerFreq = 10.0;
        double bandwidth = 5.0;
        int order = 2;

        double[] output = Signal.filtfilt_bandstop(signal, fs, centerFreq, bandwidth, order);

        assertNotNull(output);
        assertEquals(signal.length, output.length);
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private double[] generateTestSignal() {
        // Generate a simple test signal with multiple frequency components
        int n = 100;
        double[] signal = new double[n];
        for (int i = 0; i < n; i++) {
            double t = i / 100.0;
            signal[i] = Math.sin(2 * Math.PI * 5 * t) + // 5 Hz
                    0.5 * Math.sin(2 * Math.PI * 25 * t) + // 25 Hz
                    0.3 * Math.sin(2 * Math.PI * 40 * t); // 40 Hz
        }
        return signal;
    }

    private double calculateRMSE(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum / a.length);
    }

    private double calculateMean(double[] data) {
        double sum = 0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }
}
