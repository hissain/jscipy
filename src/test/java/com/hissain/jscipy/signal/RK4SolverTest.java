package com.hissain.jscipy.signal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.hissain.jscipy.signal.RK4Solver;
import com.hissain.jscipy.signal.RK4Solver.DifferentialEquation;
import com.hissain.jscipy.signal.RK4Solver.Solution;

public class RK4SolverTest {
    public static void main(String[] args) throws IOException {
        String inputFilename = args[0];
        
        List<Double> tSpanList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                tSpanList.add(Double.parseDouble(line.trim()));
            }
        }
        
        double[] tSpan = tSpanList.stream().mapToDouble(d -> d).toArray();
        
        DifferentialEquation eq = (t, y) -> -2 * t * y;
        
        RK4Solver solver = new RK4Solver();
        Solution solution = solver.solve(eq, 1.0, tSpan);
        
        for (double y : solution.y) {
            System.out.println(y);
        }
    }
}
