package com.hissain.jscipy.signal;

import com.hissain.jscipy.signal.Signal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BesselFilterTest {

    @Test
    public void testBesselFiltFilt() throws IOException {
        // Load input and expected data
        double[] input = loadData("python/data/bessel_input.txt");
        double[] expected = loadData("python/data/bessel_output_scipy.txt");

        // Parameters (must match python generation)
        double fs = 1000.0;
        double cutoff = 10.0;
        int order = 4;

        // Run Java filter
        double[] actual = Signal.bessel_filtfilt(input, fs, cutoff, order);

        // Save output for plotting
        saveData("python/data/bessel_output_java.txt", actual);

        // Calculate RMSE
        double squaredError = 0;
        double squaredErrorCenter = 0;
        int centerStart = input.length / 4;
        int centerEnd = 3 * input.length / 4;
        int centerCount = 0;

        for (int i = 0; i < input.length; i++) {
            double diff = actual[i] - expected[i];
            squaredError += diff * diff;

            if (i >= centerStart && i < centerEnd) {
                squaredErrorCenter += diff * diff;
                centerCount++;
            }
        }
        double rmse = Math.sqrt(squaredError / input.length);
        double rmseCenter = Math.sqrt(squaredErrorCenter / centerCount);

        System.out.println("Bessel Filter RMSE (Total): " + rmse);
        System.out.println("Bessel Filter RMSE (Center 50%): " + rmseCenter);

        // Assert RMSE is effectively zero (matching SciPy to machine precision)
        assertTrue(rmse < 1e-13, "RMSE should be negligible (" + rmse + ")");
    }

    private double[] loadData(String path) throws IOException {
        List<Double> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    list.add(Double.parseDouble(line));
                }
            }
        }
        double[] res = new double[list.size()];
        for (int i = 0; i < res.length; i++)
            res[i] = list.get(i);
        return res;
    }

    private void saveData(String path, double[] data) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            for (double d : data) {
                fw.write(String.format(Locale.US, "%.18e\n", d));
            }
        }
    }
}
