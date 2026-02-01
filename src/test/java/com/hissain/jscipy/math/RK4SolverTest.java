package com.hissain.jscipy.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RK4SolverTest {

    @Test
    public void testSolve() {
        RK4Solver solver = new RK4Solver();
        // dy/dt = y, y(0)=1 -> y(t) = e^t
        RK4Solver.DifferentialEquation eq = (t, y) -> y;

        RK4Solver.Solution sol = solver.solve(eq, 1.0, 0.0, 1.0, 0.1);
        assertNotNull(sol);
        assertEquals(11, sol.t.length); // 0 to 1 with 0.1 step -> 11 points
        assertEquals(0.0, sol.t[0], 1e-6);
        assertEquals(1.0, sol.y[0], 1e-6);
        assertEquals(Math.exp(1.0), sol.y[10], 1e-2); // approx check
    }

    @Test
    public void testSolveWithTimePoints() {
        RK4Solver solver = new RK4Solver();
        RK4Solver.DifferentialEquation eq = (t, y) -> -y; // y = e^-t

        double[] tSpan = { 0.0, 0.5, 1.0 };
        RK4Solver.Solution sol = solver.solve(eq, 1.0, tSpan);

        assertNotNull(sol);
        assertEquals(3, sol.t.length);
        assertEquals(Math.exp(-1.0), sol.y[2], 1e-3);
    }
}
