package com.hissain.jscipy.signal.api;

import com.hissain.jscipy.signal.RK4Solver;

/**
 * {@code IRK4Solver} defines the public API for the Runge-Kutta 4th Order (RK4)
 * numerical solver functionality within the jSciPy library.
 *
 * <p>Implementations of this interface provide methods to solve ordinary differential equations (ODEs)
 * using the RK4 method, offering both fixed-step and specified-time-point integration.</p>
 */
public interface IRK4Solver {

    /**
     * Functional interface for defining the differential equation dy/dt = f(t, y).
     * This interface is used to provide the right-hand side of the ODE to the solver.
     */
    @FunctionalInterface
    interface DifferentialEquation {
        /**
         * Computes the value of dy/dt at a given time t and solution y.
         *
         * @param t The current time.
         * @param y The current value of the solution.
         * @return The computed value of dy/dt.
         */
        double compute(double t, double y);
    }

    /**
     * {@code Solution} is a static nested class that holds the results of an RK4 integration.
     * It contains arrays for the time points and the corresponding solution values.
     */
    class Solution {
        /**
         * An array of time points at which the solution was computed.
         */
        public double[] t;
        /**
         * An array of solution values corresponding to each time point.
         */
        public double[] y;

        /**
         * Constructs a new Solution object.
         *
         * @param t An array of time points.
         * @param y An array of solution values.
         */
        public Solution(double[] t, double[] y) {
            this.t = t;
            this.y = y;
        }
    }

    /**
     * Solves an ordinary differential equation (ODE) using the RK4 method with a fixed step size.
     *
     * @param f The differential equation dy/dt = f(t, y), provided as a {@link DifferentialEquation} functional interface.
     * @param y0 The initial condition for the solution at {@code t0}.
     * @param t0 The initial time.
     * @param tf The final time. The integration will proceed from {@code t0} up to {@code tf}.
     * @param h The fixed step size for the integration. Must be positive.
     * @return A {@link Solution} object containing arrays of time points and corresponding solution values.
     * @throws IllegalArgumentException if {@code h} is not positive, or if {@code t0} is greater than {@code tf}.
     * @throws NullPointerException if {@code f} is {@code null}.
     */
    Solution solve(DifferentialEquation f, double y0, double t0, double tf, double h);

    /**
     * Solves an ordinary differential equation (ODE) using the RK4 method at specified time points.
     * The step size for each interval is determined by the difference between consecutive time points in {@code tSpan}.
     *
     * @param f The differential equation dy/dt = f(t, y), provided as a {@link DifferentialEquation} functional interface.
     * @param y0 The initial condition for the solution at {@code tSpan[0]}.
     * @param tSpan An array of time points where the solution is desired. Must be sorted in ascending order.
     *              The first element {@code tSpan[0]} is the initial time.
     * @return A {@link Solution} object containing arrays of the provided time points and corresponding solution values.
     * @throws IllegalArgumentException if {@code tSpan} is {@code null}, empty, or not sorted in ascending order.
     * @throws NullPointerException if {@code f} is {@code null}.
     */
    Solution solve(DifferentialEquation f, double y0, double[] tSpan);
}
