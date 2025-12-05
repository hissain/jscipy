package com.hissain.jscipy.signal.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadTxt {

    public static double[] read(String filePath) throws IOException {
        List<Double> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataList.add(Double.parseDouble(line.trim()));
            }
        }
        return dataList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public static void write(String filePath, double[] data) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
            for (double v : data) {
                writer.println(v);
            }
        }
    }
}
