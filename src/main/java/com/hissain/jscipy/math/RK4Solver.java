package com.hissain.jscipy.math;

/**
 * Runge-Kutta 4th Order Method for solving ODEs
 * Usage similar to Python's scipy.integrate.odeint
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/rk4/rk4_comparison_light.png"
 * alt="RK4Solver Comparison" style="width: 600px; max-width: 90%; display:
 * block; margin: 0 auto;">
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

}
