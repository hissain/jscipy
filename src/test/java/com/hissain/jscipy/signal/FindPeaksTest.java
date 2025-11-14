package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.hissain.jscipy.signal.FindPeaks;

public class FindPeaksTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/";

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

    private int[] readPeakIndices(String filename) throws IOException {
        double[] doubleData = readDataFile(filename);
        return Arrays.stream(doubleData).mapToInt(d -> (int) d).toArray();
    }

    private void runTest(String inputFilename, String expectedOutputFilename, FindPeaks.PeakParams params) throws IOException {
        double[] signal = readDataFile(inputFilename);
        int[] expectedPeaks = readPeakIndices(expectedOutputFilename);
        FindPeaks findPeaks = new FindPeaks();
        int[] peaks = findPeaks.findPeaks(signal, params).peaks;
        assertArrayEquals(expectedPeaks, peaks);
    }

    @Test
    public void testDataset1() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 1;
        runTest("findpeaks_input1.txt", "findpeaks_output1.txt", params);
    }

    @Test
    public void testDataset2() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 20;
        runTest("findpeaks_input2.txt", "findpeaks_output2.txt", params);
    }

    @Test
    public void testDataset3() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 30;
        runTest("findpeaks_input3.txt", "findpeaks_output3.txt", params);
    }

    @Test
    public void testDataset4() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 20;
        runTest("findpeaks_input4.txt", "findpeaks_output4.txt", params);
    }

    @Test
    public void testDataset5() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 70;
        runTest("findpeaks_input5.txt", "findpeaks_output5.txt", params);
    }

    @Test
    public void testDataset6() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 20;
        params.height = 0.7;
        runTest("findpeaks_input6.txt", "findpeaks_output6.txt", params);
    }

    @Test
    public void testDataset7() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 20;
        params.prominence = 0.7;
        runTest("findpeaks_input7.txt", "findpeaks_output7.txt", params);
    }

    @Test
    public void testDataset8() throws IOException {
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 20;
        params.height = 0.7;
        params.prominence = 0.7;
        runTest("findpeaks_input8.txt", "findpeaks_output8.txt", params);
    }

    public static void main(String[] args) throws IOException {
        String inputFilename = args[0];
        
        List<Double> signalList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                signalList.add(Double.parseDouble(line.trim()));
            }
        }
        
        double[] signal = signalList.stream().mapToDouble(Double::doubleValue).toArray();
        
        // For simplicity, using default parameters for command-line execution
        // In a real scenario, parameters might be passed as additional arguments
        FindPeaks findPeaks = new FindPeaks();
        FindPeaks.PeakResult result = findPeaks.findPeaks(signal, new FindPeaks.PeakParams());
        
        for (int peak : result.peaks) {
            System.out.println(peak);
        }
    }
}
