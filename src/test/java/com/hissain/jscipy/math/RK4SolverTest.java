package com.hissain.jscipy.math;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hissain.jscipy.math.RK4Solver.DifferentialEquation;
import com.hissain.jscipy.math.RK4Solver.Solution;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RK4SolverTest {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/datasets/rk4/";
    private static final double TOLERANCE = 1e-4;

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

    @Test
    public void testRK4Solver() throws IOException {
        String inputFilename = "rk4_input.txt";
        String expectedOutputFilename = "rk4_output.txt";

        double[] tSpan = readDataFile(inputFilename);
        double[] expectedOutput = readDataFile(expectedOutputFilename);

        DifferentialEquation eq = (t, y) -> -2 * t * y;

        RK4Solver solver = new RK4Solver();
        Solution solution = solver.solve(eq, 1.0, tSpan);

        // Save the Java output
        String outputFilename = expectedOutputFilename.replace(".txt", "_java.txt");
        try (java.io.PrintWriter writer = new java.io.PrintWriter(TEST_DATA_DIR + outputFilename)) {
            for (double v : solution.y) {
                writer.println(v);
            }
        }

        double rmse = 0;
        for (int i = 0; i < solution.y.length; i++) {
            rmse += Math.pow(solution.y[i] - expectedOutput[i], 2);
        }
        rmse = Math.sqrt(rmse / solution.y.length);
        System.out.println("RMSE for " + inputFilename + ": " + rmse);
        assertTrue(rmse < TOLERANCE);
    }
}
