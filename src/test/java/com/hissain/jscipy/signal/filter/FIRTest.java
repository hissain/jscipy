package com.hissain.jscipy.signal.filter;

import com.hissain.jscipy.signal.util.LoadTxt;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FIRTest {

    private static final String DATA_DIR = System.getProperty("user.dir") + "/datasets/fir/";

    @Test
    public void testFirwinLowpass31() throws IOException {
        double[] expected = LoadTxt.read(DATA_DIR + "fir_lowpass_31_100.txt");
        // numtaps=31, cutoff=100, fs=1000, pass_zero=true (lowpass)
        double[] actual = FIR.firwin_lowpass(31, 100, 1000.0);

        assertCoeffs(expected, actual, "Lowpass 31");

        // Save for visual verification if needed
        saveForPlot("fir_lowpass_31_java.txt", actual);
    }

    @Test
    public void testFirwinHighpass31() throws IOException {
        double[] expected = LoadTxt.read(DATA_DIR + "fir_highpass_31_100.txt");
        double[] actual = FIR.firwin_highpass(31, 100, 1000.0);

        assertCoeffs(expected, actual, "Highpass 31");
    }

    @Test
    public void testFirwinBandpass51() throws IOException {
        double[] expected = LoadTxt.read(DATA_DIR + "fir_bandpass_51_100_200.txt");
        double[] actual = FIR.firwin_bandpass(51, 100, 200, 1000.0);

        assertCoeffs(expected, actual, "Bandpass 51");

        saveForPlot("fir_bandpass_51_java.txt", actual);
    }

    @Test
    public void testFirwinBandstop51() throws IOException {
        double[] expected = LoadTxt.read(DATA_DIR + "fir_bandstop_51_100_200.txt");
        double[] actual = FIR.firwin_bandstop(51, 100, 200, 1000.0);

        assertCoeffs(expected, actual, "Bandstop 51");
    }

    @Test
    public void testFirwinLowpass32() throws IOException {
        double[] expected = LoadTxt.read(DATA_DIR + "fir_lowpass_32_100.txt");
        double[] actual = FIR.firwin_lowpass(32, 100, 1000.0);

        assertCoeffs(expected, actual, "Lowpass 32 (Even)");
    }

    private void assertCoeffs(double[] expected, double[] actual, String msg) {
        assertEquals(expected.length, actual.length, "Length mismatch: " + msg);
        double rmse = calculateRMSE(expected, actual);
        System.out.println(msg + " RMSE: " + rmse);
        assertEquals(0.0, rmse, 5e-14, "RMSE verification failed: " + msg);
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        double sumSq = 0;
        for (int i = 0; i < expected.length; i++) {
            double diff = expected[i] - actual[i];
            sumSq += diff * diff;
        }
        return Math.sqrt(sumSq / expected.length);
    }

    private void saveForPlot(String filename, double[] data) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(DATA_DIR + filename)) {
            for (double val : data) {
                out.printf(java.util.Locale.US, "%.16e%n", val);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
