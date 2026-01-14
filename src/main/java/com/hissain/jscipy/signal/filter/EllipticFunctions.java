/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Elliptic integrals and Jacobi elliptic functions.
 * 
 * Algorithms based on:
 * - Abramowitz & Stegun, "Handbook of Mathematical Functions" Section 16.4
 * - stdlib-js/math ellipj implementation (Apache 2.0 license)
 * - SciPy's special functions library
 */

package com.hissain.jscipy.signal.filter;

/**
 * Elliptic integrals and Jacobi elliptic functions.
 */
class EllipticFunctions {

    private static final double EPSILON = 1.0e-15;
    private static final double PI_2 = Math.PI / 2.0;

    /**
     * Complete elliptic integral of the first kind K(m).
     * Uses arithmetic-geometric mean method.
     * 
     * @param m Parameter, where m = k^2 and 0 <= m <= 1
     * @return K(m)
     */
    public static double ellipk(double m) {
        if (m < 0.0 || m > 1.0) {
            throw new IllegalArgumentException("Parameter m must be in [0, 1], got: " + m);
        }

        if (m == 0.0) {
            return PI_2;
        }

        if (m == 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        // Arithmetic-Geometric Mean algorithm
        double a = 1.0;
        double g = Math.sqrt(1.0 - m);

        while (Math.abs(a - g) > EPSILON * a) {
            double temp = (a + g) / 2.0;
            g = Math.sqrt(a * g);
            a = temp;
        }

        return PI_2 / a;
    }

    /**
     * Jacobi elliptic functions sn(u,m), cn(u,m), dn(u,m).
     * Uses the arithmetic-geometric mean from A&S 16.4.
     * 
     * Based on stdlib-js/math implementation.
     * 
     * @param u Argument
     * @param m Parameter, where m = k^2 and 0 <= m <= 1
     * @return Array [sn(u,m), cn(u,m), dn(u,m), phi] where phi is the amplitude
     */
    public static double[] ellipj(double u, double m) {
        if (m < 0.0 || m > 1.0) {
            throw new IllegalArgumentException("Parameter m must be in [0, 1], got: " + m);
        }

        // Special case: m = 0
        if (m < EPSILON) {
            double sn = Math.sin(u);
            double cn = Math.cos(u);
            double dn = 1.0;
            double phi = u;
            return new double[] { sn, cn, dn, phi };
        }

        // Special case: m = 1
        if (m > 1.0 - EPSILON) {
            double sn = Math.tanh(u);
            double cn = 1.0 / Math.cosh(u);
            double dn = cn;
            double phi = Math.asin(sn);
            return new double[] { sn, cn, dn, phi };
        }

        // A&S 16.4.1 - Compute using the arithmetic-geometric mean
        final int MAX_ITER = 9;
        double[] ca = new double[MAX_ITER + 1];

        double a = 1.0;
        double b = Math.sqrt(1.0 - m);
        int n = 0;

        while (n < MAX_ITER) {
            double atmp = (a + b) * 0.5;
            double c = (a - b) * 0.5;
            b = Math.sqrt(a * b);
            a = atmp;
            ca[n] = c / a; // Store ratio c/a

            if (Math.abs(ca[n]) < EPSILON) {
                break;
            }
            n++;
        }

        // A&S 16.4.3 - Back-substitution
        double phi1 = (1 << n) * u * a;

        while (n > 1) {
            n--;
            phi1 = 0.5 * (phi1 + Math.asin(ca[n] * Math.sin(phi1)));
        }

        // Final iteration with ca[0]
        double phi0 = 0.5 * (phi1 + Math.asin(ca[0] * Math.sin(phi1)));
        double am = phi0;

        // Compute Jacobi elliptic functions
        double sn = Math.sin(am);
        double cn = Math.cos(am);

        // Compute dn - use definition when denominator is small
        double dnDenom = Math.cos(phi1 - phi0);
        double dn;
        if (Math.abs(dnDenom) < 0.1) {
            dn = Math.sqrt(1.0 - m * sn * sn);
        } else {
            dn = cn / dnDenom;
        }

        return new double[] { sn, cn, dn, am };
    }

    /**
     * Jacobi elliptic function cd(u,m) = cn(u,m) / dn(u,m).
     * 
     * @param u Argument
     * @param m Parameter, where m = k^2
     * @return cd(u,m)
     */
    public static double ellipcd(double u, double m) {
        double[] result = ellipj(u, m);
        return result[1] / result[2]; // cn / dn
    }

    /**
     * Compute sn, cn, dn at u*K where K is the complete elliptic integral.
     * 
     * @param u Normalized argument (typically between 0 and 1)
     * @param m Parameter, where m = k^2
     * @return Array [sn(u*K,m), cn(u*K,m), dn(u*K,m)]
     */
    public static double[] ellipjAtK(double u, double m) {
        double K = ellipk(m);
        double[] result = ellipj(u * K, m);
        return new double[] { result[0], result[1], result[2] };
    }
}
