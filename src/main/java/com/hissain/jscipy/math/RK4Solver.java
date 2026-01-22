package com.hissain.jscipy.math;

/**
 * Runge-Kutta 4th Order Method for solving ODEs
 * Usage similar to Python's scipy.integrate.odeint
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/rk4/rk4_comparison_light.png"
 * alt="RK4Solver Comparison" width="600">
 */
public class RK4Solver {

    /**
     * Functional interface for defining the differential equation dy/dt = f(t, y)
     */
    @FunctionalInterface
    public interface DifferentialEquation {
        double compute(double t, double y);
    }

    /**
     * Result class to hold the solution
     */
    public static class Solution {
        /** Array of time points */
        public final double[] t;
        /** Array of solution values corresponding to time points */
        public final double[] y;

        /**
         * Constructs a new Solution object.
         * 
         * @param t Array of time points.
         * @param y Array of solution values corresponding to time points.
         */
        public Solution(double[] t, double[] y) {
            this.t = t;
            this.y = y;
        }

        /**
         * Prints the solution (time and y values) to the console.
         */
        public void print() {
            System.out.println("t\t\ty");
            System.out.println("------------------------");
            for (int i = 0; i < t.length; i++) {
                System.out.printf("%.4f\t\t%.6f%n", t[i], y[i]);
            }
        }
    }

    /**
     * Solve ODE using RK4 method
     * 
     * @param f  The differential equation dy/dt = f(t, y)
     * @param y0 Initial condition
     * @param t0 Initial time
     * @param tf Final time
     * @param h  Step size
     * @return Solution object containing t and y arrays
     */
    public Solution solve(DifferentialEquation f, double y0, double t0, double tf, double h) {
        int n = (int) Math.ceil((tf - t0) / h) + 1;
        double[] t = new double[n];
        double[] y = new double[n];

        t[0] = t0;
        y[0] = y0;

        for (int i = 0; i < n - 1; i++) {
            double k1 = h * f.compute(t[i], y[i]);
            double k2 = h * f.compute(t[i] + h / 2.0, y[i] + k1 / 2.0);
            double k3 = h * f.compute(t[i] + h / 2.0, y[i] + k2 / 2.0);
            double k4 = h * f.compute(t[i] + h, y[i] + k3);

            y[i + 1] = y[i] + (k1 + 2 * k2 + 2 * k3 + k4) / 6.0;
            t[i + 1] = t[i] + h;
        }

        return new Solution(t, y);
    }

    /**
     * Solve ODE using RK4 method with specified time points.
     * Uses internal sub-stepping for improved accuracy when time points are sparse.
     * 
     * @param f     The differential equation dy/dt = f(t, y)
     * @param y0    Initial condition
     * @param tSpan Array of time points where solution is desired
     * @return Solution object containing t and y arrays
     */
    public Solution solve(DifferentialEquation f, double y0, double[] tSpan) {
        return solve(f, y0, tSpan, 100); // Default 100 sub-steps for high accuracy
    }

    /**
     * Solve ODE using RK4 method with specified time points and sub-stepping.
     * 
     * @param f        The differential equation dy/dt = f(t, y)
     * @param y0       Initial condition
     * @param tSpan    Array of time points where solution is desired
     * @param subSteps Number of internal steps between each user-specified time
     *                 point.
     *                 Higher values give better accuracy. Use 1 for no
     *                 sub-stepping.
     * @return Solution object containing t and y arrays
     */
    public Solution solve(DifferentialEquation f, double y0, double[] tSpan, int subSteps) {
        int n = tSpan.length;
        double[] y = new double[n];
        y[0] = y0;

        for (int i = 0; i < n - 1; i++) {
            double t = tSpan[i];
            double yVal = y[i];
            double intervalH = (tSpan[i + 1] - tSpan[i]) / subSteps;

            // Perform sub-steps for improved accuracy
            for (int s = 0; s < subSteps; s++) {
                double k1 = intervalH * f.compute(t, yVal);
                double k2 = intervalH * f.compute(t + intervalH / 2.0, yVal + k1 / 2.0);
                double k3 = intervalH * f.compute(t + intervalH / 2.0, yVal + k2 / 2.0);
                double k4 = intervalH * f.compute(t + intervalH, yVal + k3);

                yVal = yVal + (k1 + 2 * k2 + 2 * k3 + k4) / 6.0;
                t += intervalH;
            }

            y[i + 1] = yVal;
        }

        return new Solution(tSpan, y);
    }

    /**
     * Example usage and test cases
     */
    /**
     * Main method for command-line execution of the RK4Solver.
     * This method demonstrates solving various differential equations.
     * 
     * @param args Command-line arguments (not directly used for solving, but for
     *             demonstration).
     */
    public static void main(String[] args) {
        System.out.println("=== Example 1: dy/dt = y (exponential growth) ===");
        System.out.println("Exact solution: y = e^t");
        System.out.println();

        // Define the differential equation dy/dt = y
        DifferentialEquation eq1 = (t, y) -> y;

        RK4Solver solver = new RK4Solver();
        Solution sol1 = solver.solve(eq1, 1.0, 0.0, 2.0, 0.1);
        sol1.print();

        System.out.println("\n=== Example 2: dy/dt = -2*t*y (Gaussian) ===");
        System.out.println("Exact solution: y = e^(-t^2)");
        System.out.println();

        // Define the differential equation dy/dt = -2*t*y
        DifferentialEquation eq2 = (t, y) -> -2 * t * y;

        Solution sol2 = solver.solve(eq2, 1.0, 0.0, 2.0, 0.2);
        sol2.print();

        System.out.println("\n=== Example 3: dy/dt = t - y (with specified time points) ===");
        System.out.println();

        // Define the differential equation dy/dt = t - y
        DifferentialEquation eq3 = (t, y) -> t - y;

        double[] timePoints = { 0, 0.5, 1.0, 1.5, 2.0 };
        Solution sol3 = solver.solve(eq3, 0.5, timePoints);
        sol3.print();

        System.out.println("\n=== Example 4: Simple Harmonic Oscillator (using system reduction) ===");
        System.out.println("d²y/dt² = -y  =>  dy/dt = v, dv/dt = -y");
        System.out.println();

        // For second order ODE, we need to extend to vector form
        // This is a simple demonstration with manual tracking
        double y_pos = 1.0; // initial position
        double y_vel = 0.0; // initial velocity
        double t = 0.0;
        double h = 0.1;

        System.out.println("t\t\ty");
        System.out.println("------------------------");
        System.out.printf("%.4f\t\t%.6f%n", t, y_pos);

        for (int i = 0; i < 20; i++) {
            // RK4 for position
            double k1_y = h * y_vel;
            double k1_v = h * (-y_pos);

            double k2_y = h * (y_vel + k1_v / 2);
            double k2_v = h * (-(y_pos + k1_y / 2));

            double k3_y = h * (y_vel + k2_v / 2);
            double k3_v = h * (-(y_pos + k2_y / 2));

            double k4_y = h * (y_vel + k3_v);
            double k4_v = h * (-(y_pos + k3_y));

            y_pos += (k1_y + 2 * k2_y + 2 * k3_y + k4_y) / 6.0;
            y_vel += (k1_v + 2 * k2_v + 2 * k3_v + k4_v) / 6.0;
            t += h;

            System.out.printf("%.4f\t\t%.6f%n", t, y_pos);
        }
    }
}
