package com.hissain.jscipy.signal;

import com.hissain.jscipy.Signal;
import com.hissain.jscipy.TestMetrics;
import com.hissain.jscipy.signal.util.LoadTxt;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeakPropertiesTest {

    private static final String DATA_DIR = "datasets/peak_properties/";
    private static final double TOLERANCE = 1e-14;

    @Test
    public void testPeakProminencesMultiPeak() throws IOException {
        String testId = "test1";

        double[] x = LoadTxt.read(DATA_DIR + testId + "_input.txt");
        // Peaks indices are stored as doubles in txt, convert to int
        double[] peaksDouble = LoadTxt.read(DATA_DIR + testId + "_peaks.txt");
        int[] peaks = new int[peaksDouble.length];
        for (int i = 0; i < peaksDouble.length; i++)
            peaks[i] = (int) peaksDouble[i];

        double[] expectedProminences = LoadTxt.read(DATA_DIR + testId + "_prominences.txt");
        double[] expectedLeftBases = LoadTxt.read(DATA_DIR + testId + "_left_bases.txt");
        double[] expectedRightBases = LoadTxt.read(DATA_DIR + testId + "_right_bases.txt");

        // Calculate
        FindPeaks.ProminenceResult result = Signal.peak_prominences(x, peaks);

        // Validate Prominences
        double rmseProminence = calculateRMSE(expectedProminences, result.prominences);
        System.out.println("RMSE Prominence (MultiPeak): " + rmseProminence);
        TestMetrics.log("Signal", "PeakProminence_MultiPeak", rmseProminence);
        assertArrayEquals(expectedProminences, result.prominences, TOLERANCE, "Prominences mismatch");

        // Validate Bases
        for (int i = 0; i < peaks.length; i++) {
            assertEquals((int) expectedLeftBases[i], result.leftBases[i], "Left Base mismatch at peak " + i);
            assertEquals((int) expectedRightBases[i], result.rightBases[i], "Right Base mismatch at peak " + i);
        }
    }

    @Test
    public void testPeakWidthsMultiPeak() throws IOException {
        String testId = "test1";
        double[] x = LoadTxt.read(DATA_DIR + testId + "_input.txt");
        double[] peaksDouble = LoadTxt.read(DATA_DIR + testId + "_peaks.txt");
        int[] peaks = new int[peaksDouble.length];
        for (int i = 0; i < peaksDouble.length; i++)
            peaks[i] = (int) peaksDouble[i];

        // Expected Data (default rel_height=0.5)
        double[] expectedWidths = LoadTxt.read(DATA_DIR + testId + "_widths.txt");
        double[] expectedWidthHeights = LoadTxt.read(DATA_DIR + testId + "_width_heights.txt");
        double[] expectedLeftIps = LoadTxt.read(DATA_DIR + testId + "_left_ips.txt");
        double[] expectedRightIps = LoadTxt.read(DATA_DIR + testId + "_right_ips.txt");

        // Calculate
        FindPeaks.WidthResult result = Signal.peak_widths(x, peaks, 0.5);

        // Validate
        double rmseWidths = calculateRMSE(expectedWidths, result.widths);
        System.out.println("RMSE Widths (MultiPeak, 0.5): " + rmseWidths);
        TestMetrics.log("Signal", "PeakWidths_MultiPeak_0.5", rmseWidths);

        assertArrayEquals(expectedWidths, result.widths, TOLERANCE, "Widths mismatch");
        assertArrayEquals(expectedWidthHeights, result.widthHeights, TOLERANCE, "Width Heights mismatch");
        assertArrayEquals(expectedLeftIps, result.leftIps, TOLERANCE, "Left IPs mismatch");
        assertArrayEquals(expectedRightIps, result.rightIps, TOLERANCE, "Right IPs mismatch");
    }

    @Test
    public void testPeakWidthsFullHeight() throws IOException {
        String testId = "test1"; // Only width files have prefix
        String prefix = testId + "_rh100_";

        double[] x = LoadTxt.read(DATA_DIR + testId + "_input.txt");
        double[] peaksDouble = LoadTxt.read(DATA_DIR + testId + "_peaks.txt");
        int[] peaks = new int[peaksDouble.length];
        for (int i = 0; i < peaksDouble.length; i++)
            peaks[i] = (int) peaksDouble[i];

        double[] expectedWidths = LoadTxt.read(DATA_DIR + prefix + "widths.txt");

        FindPeaks.WidthResult result = Signal.peak_widths(x, peaks, 1.0);

        double rmseWidths = calculateRMSE(expectedWidths, result.widths);
        System.out.println("RMSE Widths (MultiPeak, 1.0): " + rmseWidths);
        TestMetrics.log("Signal", "PeakWidths_MultiPeak_1.0", rmseWidths);

        // Relaxed tolerance for full width at base due to potential flat base
        // interpolation differences
        assertArrayEquals(expectedWidths, result.widths, 1e-10, "Widths (full) mismatch");
    }

    @Test
    public void testPeakProminencesChirp() throws IOException {
        String testId = "test2_chirp";

        double[] x = LoadTxt.read(DATA_DIR + testId + "_input.txt");
        double[] peaksDouble = LoadTxt.read(DATA_DIR + testId + "_peaks.txt");
        int[] peaks = new int[peaksDouble.length];
        for (int i = 0; i < peaksDouble.length; i++)
            peaks[i] = (int) peaksDouble[i];

        double[] expectedProminences = LoadTxt.read(DATA_DIR + testId + "_prominences.txt");

        FindPeaks.ProminenceResult result = Signal.peak_prominences(x, peaks);

        double rmse = calculateRMSE(expectedProminences, result.prominences);
        System.out.println("RMSE Prominence (Chirp): " + rmse);
        TestMetrics.log("Signal", "PeakProminence_Chirp", rmse);

        assertArrayEquals(expectedProminences, result.prominences, TOLERANCE);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        if (expected.length != actual.length)
            throw new IllegalArgumentException("Length mismatch");
        double sumSq = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSq += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSq / expected.length);
    }
}
