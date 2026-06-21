package com.hissain.jscipy.signal;

import com.hissain.jscipy.TestMetrics;
import com.hissain.jscipy.signal.util.LoadTxt;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaveformTest {

    private static final double TOLERANCE = 1e-10;
    private static final String DIR = "datasets/waveform/";

    private double rmse(double[] expected, double[] actual) {
        double sum = 0;
        for (int i = 0; i < expected.length; i++) {
            double d = expected[i] - actual[i];
            sum += d * d;
        }
        return Math.sqrt(sum / expected.length);
    }

    // --- chirp ---

    @Test
    void testChirpLinear() throws IOException {
        double[] t = LoadTxt.read(DIR + "t.txt");
        double[] expected = LoadTxt.read(DIR + "chirp_linear.txt");
        double[] actual = new Waveform().chirp(t, 1.0, 1.0, 100.0, "linear");
        LoadTxt.write(DIR + "chirp_linear_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "chirp_linear", rmse);
        assertTrue(rmse < TOLERANCE, "chirp linear RMSE: " + rmse);
    }

    @Test
    void testChirpQuadratic() throws IOException {
        double[] t = LoadTxt.read(DIR + "t.txt");
        double[] expected = LoadTxt.read(DIR + "chirp_quadratic.txt");
        double[] actual = new Waveform().chirp(t, 1.0, 1.0, 100.0, "quadratic");
        LoadTxt.write(DIR + "chirp_quadratic_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "chirp_quadratic", rmse);
        assertTrue(rmse < TOLERANCE, "chirp quadratic RMSE: " + rmse);
    }

    @Test
    void testChirpLogarithmic() throws IOException {
        double[] t = LoadTxt.read(DIR + "t.txt");
        double[] expected = LoadTxt.read(DIR + "chirp_logarithmic.txt");
        double[] actual = new Waveform().chirp(t, 1.0, 1.0, 100.0, "logarithmic");
        LoadTxt.write(DIR + "chirp_logarithmic_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "chirp_logarithmic", rmse);
        assertTrue(rmse < TOLERANCE, "chirp logarithmic RMSE: " + rmse);
    }

    @Test
    void testChirpHyperbolic() throws IOException {
        double[] t = LoadTxt.read(DIR + "t.txt");
        double[] expected = LoadTxt.read(DIR + "chirp_hyperbolic.txt");
        double[] actual = new Waveform().chirp(t, 1.0, 1.0, 100.0, "hyperbolic");
        LoadTxt.write(DIR + "chirp_hyperbolic_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "chirp_hyperbolic", rmse);
        assertTrue(rmse < TOLERANCE, "chirp hyperbolic RMSE: " + rmse);
    }

    // --- square ---

    @Test
    void testSquareDuty05() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_rad.txt");
        double[] expected = LoadTxt.read(DIR + "square_duty05.txt");
        double[] actual = new Waveform().square(t, 0.5);
        LoadTxt.write(DIR + "square_duty05_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "square_duty05", rmse);
        assertTrue(rmse < TOLERANCE, "square duty=0.5 RMSE: " + rmse);
    }

    @Test
    void testSquareDuty025() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_rad.txt");
        double[] expected = LoadTxt.read(DIR + "square_duty025.txt");
        double[] actual = new Waveform().square(t, 0.25);
        LoadTxt.write(DIR + "square_duty025_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "square_duty025", rmse);
        assertTrue(rmse < TOLERANCE, "square duty=0.25 RMSE: " + rmse);
    }

    @Test
    void testSquareDuty075() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_rad.txt");
        double[] expected = LoadTxt.read(DIR + "square_duty075.txt");
        double[] actual = new Waveform().square(t, 0.75);
        LoadTxt.write(DIR + "square_duty075_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "square_duty075", rmse);
        assertTrue(rmse < TOLERANCE, "square duty=0.75 RMSE: " + rmse);
    }

    // --- sawtooth ---

    @Test
    void testSawtoothWidth1() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_rad.txt");
        double[] expected = LoadTxt.read(DIR + "sawtooth_width1.txt");
        double[] actual = new Waveform().sawtooth(t, 1.0);
        LoadTxt.write(DIR + "sawtooth_width1_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "sawtooth_width1", rmse);
        assertTrue(rmse < TOLERANCE, "sawtooth width=1 RMSE: " + rmse);
    }

    @Test
    void testSawtoothWidth05() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_rad.txt");
        double[] expected = LoadTxt.read(DIR + "sawtooth_width05.txt");
        double[] actual = new Waveform().sawtooth(t, 0.5);
        LoadTxt.write(DIR + "sawtooth_width05_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "sawtooth_width05", rmse);
        assertTrue(rmse < TOLERANCE, "sawtooth width=0.5 RMSE: " + rmse);
    }

    @Test
    void testSawtoothWidth0() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_rad.txt");
        double[] expected = LoadTxt.read(DIR + "sawtooth_width0.txt");
        double[] actual = new Waveform().sawtooth(t, 0.0);
        LoadTxt.write(DIR + "sawtooth_width0_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "sawtooth_width0", rmse);
        assertTrue(rmse < TOLERANCE, "sawtooth width=0 RMSE: " + rmse);
    }

    // --- gausspulse ---

    @Test
    void testGausspulseFc1000Bw05() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_gauss.txt");
        double[] expected = LoadTxt.read(DIR + "gausspulse_fc1000_bw05.txt");
        double[] actual = new Waveform().gausspulse(t, 1000.0, 0.5);
        LoadTxt.write(DIR + "gausspulse_fc1000_bw05_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "gausspulse_fc1000_bw05", rmse);
        assertTrue(rmse < TOLERANCE, "gausspulse fc=1000 bw=0.5 RMSE: " + rmse);
    }

    @Test
    void testGausspulseFc500Bw03() throws IOException {
        double[] t = LoadTxt.read(DIR + "t_gauss.txt");
        double[] expected = LoadTxt.read(DIR + "gausspulse_fc500_bw03.txt");
        double[] actual = new Waveform().gausspulse(t, 500.0, 0.3);
        LoadTxt.write(DIR + "gausspulse_fc500_bw03_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "gausspulse_fc500_bw03", rmse);
        assertTrue(rmse < TOLERANCE, "gausspulse fc=500 bw=0.3 RMSE: " + rmse);
    }

    // --- unit impulse ---

    @Test
    void testUnitImpulseIdx0() throws IOException {
        double[] expected = LoadTxt.read(DIR + "unit_impulse_n100_idx0.txt");
        double[] actual = new Waveform().unitImpulse(100, 0);
        LoadTxt.write(DIR + "unit_impulse_n100_idx0_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "unit_impulse_idx0", rmse);
        assertTrue(rmse < TOLERANCE, "unit_impulse idx=0 RMSE: " + rmse);
    }

    @Test
    void testUnitImpulseMid() throws IOException {
        double[] expected = LoadTxt.read(DIR + "unit_impulse_n100_mid.txt");
        double[] actual = new Waveform().unitImpulse(100, -1);
        LoadTxt.write(DIR + "unit_impulse_n100_mid_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "unit_impulse_mid", rmse);
        assertTrue(rmse < TOLERANCE, "unit_impulse mid RMSE: " + rmse);
    }

    @Test
    void testUnitImpulseIdx25() throws IOException {
        double[] expected = LoadTxt.read(DIR + "unit_impulse_n100_idx25.txt");
        double[] actual = new Waveform().unitImpulse(100, 25);
        LoadTxt.write(DIR + "unit_impulse_n100_idx25_java.txt", actual);
        double rmse = rmse(expected, actual);
        TestMetrics.log("Waveform", "unit_impulse_idx25", rmse);
        assertTrue(rmse < TOLERANCE, "unit_impulse idx=25 RMSE: " + rmse);
    }
}
