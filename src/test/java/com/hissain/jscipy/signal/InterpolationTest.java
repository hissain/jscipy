package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterpolationTest {

    private final Interpolation interpolation = new Interpolation();
    private static final double TOLERANCE = 0.001;

    @Test
    public void testLinearInterpolationData1() throws IOException {
        double[] x = readData("datasets/interpolation_input_x_1.txt");
        double[] y = readData("datasets/interpolation_input_y_1.txt");
        double[] newX = readData("datasets/interpolation_input_new_x_1.txt");
        double[] expectedY = readData("datasets/interpolation_output_linear_1.txt");

        double[] actualY = interpolation.linear(x, y, newX);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/interpolation_output_linear_java_1.txt")) {
            for (double v : actualY) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expectedY, actualY);
        System.out.println("RMSE for Linear Interpolation 1: " + rmse);
        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
        assertArrayEquals(expectedY, actualY, TOLERANCE);
    }

    @Test
    public void testLinearInterpolationData2() throws IOException {
        double[] x = readData("datasets/interpolation_input_x_2.txt");
        double[] y = readData("datasets/interpolation_input_y_2.txt");
        double[] newX = readData("datasets/interpolation_input_new_x_2.txt");
        double[] expectedY = readData("datasets/interpolation_output_linear_2.txt");

        double[] actualY = interpolation.linear(x, y, newX);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/interpolation_output_linear_java_2.txt")) {
            for (double v : actualY) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expectedY, actualY);
        System.out.println("RMSE for Linear Interpolation 2: " + rmse);
        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
        assertArrayEquals(expectedY, actualY, TOLERANCE);
    }

    @Test
    public void testCubicInterpolationData1() throws IOException {
        double[] x = readData("datasets/interpolation_input_x_1.txt");
        double[] y = readData("datasets/interpolation_input_y_1.txt");
        double[] newX = readData("datasets/interpolation_input_new_x_1.txt");
        double[] expectedY = readData("datasets/interpolation_output_cubic_1.txt");

        double[] actualY = interpolation.cubic(x, y, newX);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/interpolation_output_cubic_java_1.txt")) {
            for (double v : actualY) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expectedY, actualY);
        System.out.println("RMSE for Cubic Interpolation 1: " + rmse);
        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
        assertArrayEquals(expectedY, actualY, TOLERANCE);
    }

    @Test
    public void testCubicInterpolationData2() throws IOException {
        double[] x = readData("datasets/interpolation_input_x_2.txt");
        double[] y = readData("datasets/interpolation_input_y_2.txt");
        double[] newX = readData("datasets/interpolation_input_new_x_2.txt");
        double[] expectedY = readData("datasets/interpolation_output_cubic_2.txt");

        double[] actualY = interpolation.cubic(x, y, newX);

        try (java.io.PrintWriter writer = new java.io.PrintWriter("datasets/interpolation_output_cubic_java_2.txt")) {
            for (double v : actualY) {
                writer.println(v);
            }
        }

        double rmse = calculateRMSE(expectedY, actualY);
        System.out.println("RMSE for Cubic Interpolation 2: " + rmse);
        assertTrue(rmse < TOLERANCE, "RMSE too high: " + rmse);
        assertArrayEquals(expectedY, actualY, TOLERANCE);
    }

    private double[] readData(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private double calculateRMSE(double[] expected, double[] actual) {
        double sumSquareError = 0;
        for (int i = 0; i < expected.length; i++) {
            sumSquareError += Math.pow(expected[i] - actual[i], 2);
        }
        return Math.sqrt(sumSquareError / expected.length);
    }
}
