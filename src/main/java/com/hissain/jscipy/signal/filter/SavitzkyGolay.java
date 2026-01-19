package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.linear.*;

/**
 * Implements Savitzky-Golay filter for data smoothing and differentiation.
 * Based on the method described in Numerical Recipes and SciPy's
 * implementation.
 */
public class SavitzkyGolay {

    /**
     * Applies a Savitzky-Golay filter to an array.
     *
     * @param x            The data to be filtered.
     * @param windowLength The length of the filter window (i.e., the number of
     *                     coefficients). window_length must be a positive odd
     *                     integer.
     * @param polyOrder    The order of the polynomial used to fit the samples.
     *                     polyorder must be less than window_length.
     * @param deriv        The order of the derivative to compute. This must be a
     *                     non-negative integer. The default is 0, which means to
     *                     filter the data without differentiating.
     * @param delta        The spacing of the samples to which the data will be
     *                     applied. This is only used if deriv > 0. Default is 1.0.
     * @return The filtered data.
     */
    public double[] savgol_filter(double[] x, int windowLength, int polyOrder, int deriv, double delta) {
        if (deriv < 0) {
            throw new IllegalArgumentException("Derivative order must be non-negative.");
        }
        if (delta <= 0) {
            throw new IllegalArgumentException("Delta must be positive.");
        }
        return applySavitzkyGolay(x, windowLength, polyOrder, deriv, delta);
    }

    /**
     * Applies a Savitzky-Golay filter to an array (smoothing, derivative=0).
     *
     * @param x            The data to be filtered.
     * @param windowLength The length of the filter window.
     * @param polyOrder    The order of the polynomial.
     * @return The filtered (smoothed) data.
     */
    public double[] savgol_filter(double[] x, int windowLength, int polyOrder) {
        return savgol_filter(x, windowLength, polyOrder, 0, 1.0);
    }

    /**
     * Apply Savitzky-Golay filter using 'interp' mode (polynomial fitting at
     * edges).
     *
     * @param data         The input data array.
     * @param windowLength The length of the filter window.
     * @param polyOrder    The order of the polynomial.
     * @param deriv        The order of the derivative.
     * @param delta        The spacing of the samples.
     * @return The filtered data.
     */
    private double[] applySavitzkyGolay(double[] data, int windowLength, int polyOrder, int deriv, double delta) {
        if (windowLength % 2 == 0 || windowLength < 1) {
            throw new IllegalArgumentException("Window length must be an odd positive integer.");
        }
        if (windowLength <= polyOrder) {
            throw new IllegalArgumentException("Polynomial order must be less than window length.");
        }
        if (polyOrder < deriv) {
            throw new IllegalArgumentException("Polynomial order must be greater than or equal to derivative order.");
        }
        if (data.length < windowLength) {
            throw new IllegalArgumentException("Data length must be greater than or equal to window length.");
        }

        int halfWindow = windowLength / 2;
        double[] output = new double[data.length];

        // Process interior points with standard convolution
        double[] coeffs = calculateSavitzkyGolayCoefficients(windowLength, polyOrder, deriv, delta, halfWindow);

        for (int i = halfWindow; i < data.length - halfWindow; i++) {
            double sum = 0.0;
            for (int j = 0; j < windowLength; j++) {
                sum += data[i - halfWindow + j] * coeffs[j];
            }
            output[i] = sum;
        }

        // Handle left edge with polynomial fit
        for (int i = 0; i < halfWindow; i++) {
            double[] coeffsLeft = calculateSavitzkyGolayCoefficients(windowLength, polyOrder, deriv, delta, i);
            double sum = 0.0;
            for (int j = 0; j < Math.min(windowLength, data.length); j++) {
                sum += data[j] * coeffsLeft[j];
            }
            output[i] = sum;
        }

        // Handle right edge with polynomial fit
        for (int i = data.length - halfWindow; i < data.length; i++) {
            int pos = windowLength - (data.length - i);
            double[] coeffsRight = calculateSavitzkyGolayCoefficients(windowLength, polyOrder, deriv, delta, pos);
            double sum = 0.0;
            for (int j = 0; j < Math.min(windowLength, data.length); j++) {
                int dataIdx = data.length - windowLength + j;
                if (dataIdx >= 0 && dataIdx < data.length) {
                    sum += data[dataIdx] * coeffsRight[j];
                }
            }
            output[i] = sum;
        }

        return output;
    }

    /**
     * Calculates Savitzky-Golay filter coefficients.
     *
     * @param windowLength The length of the filter window.
     * @param polyOrder    The order of the polynomial.
     * @param deriv        The order of the derivative.
     * @param delta        The spacing of the samples.
     * @param pos          The position within the window to evaluate (default:
     *                     center).
     * @return An array of filter coefficients.
     */
    private double[] calculateSavitzkyGolayCoefficients(int windowLength, int polyOrder, int deriv, double delta,
            int pos) {
        if (pos < 0 || pos >= windowLength) {
            throw new IllegalArgumentException("Position must be within window bounds");
        }

        int halfWindow = windowLength / 2;
        double[] x = new double[windowLength];
        for (int i = 0; i < windowLength; i++) {
            x[i] = i - halfWindow;
        }

        // Construct the Vandermonde matrix
        double[][] vData = new double[windowLength][polyOrder + 1];
        for (int r = 0; r < windowLength; r++) {
            for (int c = 0; c <= polyOrder; c++) {
                vData[r][c] = Math.pow(x[r], c);
            }
        }
        RealMatrix vMatrix = MatrixUtils.createRealMatrix(vData);

        // Calculate (V^T * V)^-1 * V^T
        RealMatrix vT = vMatrix.transpose();
        RealMatrix vTv = vT.multiply(vMatrix);
        RealMatrix vTvInv;
        try {
            vTvInv = new LUDecomposition(vTv).getSolver().getInverse();
        } catch (SingularMatrixException e) {
            throw new IllegalArgumentException(
                    "Singular matrix encountered. Try reducing polyOrder or increasing windowLength.", e);
        }
        RealMatrix pseudoInverse = vTvInv.multiply(vT);

        // The coefficients for the derivative are in the 'deriv'-th row of
        // pseudoInverse
        RealVector coefficientsVector = pseudoInverse.getRowVector(deriv);
        double[] coefficientsRow = coefficientsVector.toArray();

        // Scale by deriv! / delta^deriv
        double factorial = factorial(deriv);
        double scale = factorial / Math.pow(delta, deriv);

        // For center position, use direct coefficients (more efficient for standard
        // convolution)
        // The center position logic in Python implementation calculates coefficients
        // for x=0.
        // If pos matches halfWindow, we are evaluating at x=0.
        // The pre-calculated 'coefficientsRow' corresponds to the coefficients for y[0]
        // (or y[deriv])
        // when the polynomial is fit around 0.
        // Wait, the pseudoInverse method gives coefficients C_j such that f(0) =
        // sum(C_j * y_j).
        // So for center (x=0), we can use coefficientsRow directly?
        // Actually, Scipy implementation evaluates the polynomial at 'pos'.
        // Let's stick to the general evaluation to be safe and match the logic.

        // Evaluate the polynomial basis at the specified position to get the effective
        // filter coefficients
        double xPos = pos - halfWindow;
        double[] result = new double[windowLength];

        for (int i = 0; i < windowLength; i++) {
            double sum = 0.0;
            // Sum over polynomial terms: coeffs[i] = sum_k ( P_{deriv, k} * xPos^k *
            // V_{i,k} )?
            // Actually, we want to find weights w_i such that f^(deriv)(xPos) = sum(w_i *
            // y_i).
            // The fitted polynomial is p(x) = sum_k (c_k * x^k).
            // c_k = sum_i (pseudoInverse_{k,i} * y_i).
            // derivative of p(x) at xPos: p^(d)(xPos) = sum_k (c_k * d/dx^d(x^k)|xPos).
            // d/dx^d(x^k) = (k! / (k-d)!) * x^(k-d) if k >= d, else 0.

            // But the Kotlin implementation did:
            // sum += pseudoInverse.getEntry(deriv, k) * posX.pow(k.toDouble()) * v[i][k]
            // This looks wrong if it's just taking the 'deriv' row of pseudoInverse.
            // The 'deriv' row of pseudoInverse gives coefficients to estimate the d-th
            // derivative at x=0 (if the basis is centered at 0).
            // If we want derivative at xPos, we need to combine them.

            // Re-reading SciPy source code or logic:
            // `savgol_coeffs` returns coefficients for the filter.
            // If `use = 'dot'`, it calculates `(V^T V)^-1 V^T` and takes the row
            // corresponding to `deriv`.
            // BUT, this is valid for pos=0 (center) if X is centered.
            // If pos != 0, we need to evaluate the polynomial derivative at pos.

            // The Kotlin code had:
            /*
             * for (i in 0 until windowLength) {
             * var sum = 0.0
             * // Sum over polynomial terms
             * for (k in 0..polyOrder) {
             * sum += pseudoInverse.getEntry(deriv, k) * posX.pow(k.toDouble()) * v[i][k]
             * }
             * result[i] = sum * scale
             * }
             */
            // This loop uses `pseudoInverse.getEntry(deriv, k)`. This is fetching the
            // `deriv`-th row?
            // No, `getEntry(row, col)`. `deriv` is the row index.
            // The rows of pseudoInverse correspond to the powers 0, 1, ..., polyOrder.
            // Row 0 estimates coefficient c_0 (value).
            // Row 1 estimates coefficient c_1 (1st deriv at 0).
            // Row k estimates coefficient c_k (k-th deriv at 0 / k!).

            // If we want value at xPos (deriv=0):
            // y(xPos) = c_0 + c_1*xPos + c_2*xPos^2 + ...
            // = sum_k (c_k * xPos^k).
            // = sum_k ( (sum_i M_{ki} y_i) * xPos^k )
            // = sum_i y_i * (sum_k M_{ki} * xPos^k)
            // So weight w_i = sum_k (M_{ki} * xPos^k).
            // This matches the Kotlin code logic (if `deriv` was k? No).

            // The Kotlin code used `pseudoInverse.getEntry(deriv, k)`. This is wrong if it
            // iterates k.
            // It should iterate rows of pseudoInverse (which are polynomial orders).
            // Let's fix the logic.
            // We need weights w_i.
            // w_i = sum_{m=0}^{polyOrder} ( (d^d/dx^d x^m)|xPos * pseudoInverse_{m,i} )
            // Term (d^d/dx^d x^m)|xPos:
            // If m < d: 0
            // If m >= d: m*(m-1)*...*(m-d+1) * xPos^(m-d) = (m! / (m-d)!) * xPos^(m-d).

            // The Kotlin code had logic for `deriv=0`:
            // sum += pseudoInverse.getEntry(deriv, k) ...
            // If deriv=0, it accessed row 0. Row 0 is c_0.
            // c_0 is value at 0.
            // It multiplied by xPos^k??

            // Actually, simpler way:
            // Use `savgol_coeffs` from scipy logic:
            // fit the polynomial at xPos.
            // Or just fit at 0 and shift x?

            // Let's use the definition:
            // The weights are obtained by solving the least squares for a delta function?
            // Or just:
            // We want to estimate y^(d)(xPos).
            // y(x) = sum c_m x^m.
            // y^(d)(x) = sum c_m (d/dx^d x^m).
            // w_i = sum_m ( d/dx^d x^m |_{xPos} * M_{mi} )
            // where M is pseudoInverse.

            for (int m = 0; m <= polyOrder; m++) {
                double term = 0;
                if (m >= deriv) {
                    double fact = factorial(m) / factorial(m - deriv);
                    term = fact * Math.pow(xPos, m - deriv);
                }
                // M_{mi} is pseudoInverse.getEntry(m, i)
                sum += term * pseudoInverse.getEntry(m, i);
            }
            result[i] = sum / Math.pow(delta, deriv); // Scale by delta only, factorial is handled above?
            // Wait, standard SavGol assumes x spacing is 1. If delta != 1, we divide by
            // delta^deriv.
            // The factorial part comes from derivative of power.
        }
        return result;
    }

    private double factorial(int n) {
        if (n < 0)
            throw new IllegalArgumentException("Factorial not defined for negative numbers");
        if (n <= 1)
            return 1;
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
