package com.hissain.jscipy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestMetrics {
    private static final String METRICS_FILE = "datasets/test_metrics.json";

    public static synchronized void log(String module, String testName, double rmse) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("datasets"));

            // Append to file
            try (PrintWriter out = new PrintWriter(new FileWriter(METRICS_FILE, true))) {
                String json = String.format("{\"module\": \"%s\", \"test\": \"%s\", \"rmse\": %e}",
                        module, testName, rmse);
                out.println(json);
            }
        } catch (IOException e) {
            System.err.println("Failed to write metrics: " + e.getMessage());
        }
    }
}
