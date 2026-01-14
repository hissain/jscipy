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
 *  Copyright (c) 2009 by Vinnie Falco
 *  Copyright (c) 2016 by Bernd Porr
 */

package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.complex.Complex;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal design class for Elliptic (Cauer) filters.
 * Based on scipy.signal.ellipap implementation.
 */
class EllipticDesign extends Cascade {

    private static final int ELLIPDEG_MMAX = 7;
    private static final int ARC_JAC_SN_MAXITER = 100;

    class AnalogLowPass extends LayoutBase {

        private int nPoles;
        private double rippleDb;
        private double stopBandDb;

        public AnalogLowPass(int _nPoles, double _rippleDb, double _stopBandDb) {
            super(_nPoles);
            nPoles = _nPoles;
            rippleDb = _rippleDb;
            stopBandDb = _stopBandDb;
        }

        public void design() {
            reset();

            // Compute parameters matching scipy
            double eps_sq = Math.pow(10, 0.1 * rippleDb) - 1;
            double eps = Math.sqrt(eps_sq);
            double ck1_sq = eps_sq / (Math.pow(10, 0.1 * stopBandDb) - 1);

            // Compute m using ellipdeg
            double m = ellipdeg(nPoles, ck1_sq);
            double sqrtM = Math.sqrt(m);

            // K(m)
            double capk = EllipticFunctions.ellipk(m);
            double K1 = EllipticFunctions.ellipk(ck1_sq);

            // Compute r = arc_jac_sc1(1/eps, ck1_sq)
            double r = arcJacSc1(1.0 / eps, ck1_sq);

            // v0
            double v0 = capk * r / (nPoles * K1);

            // Jacobi functions at v0 with parameter 1-m
            double[] ellipV0 = EllipticFunctions.ellipj(v0, 1.0 - m);
            double sv = ellipV0[0];
            double cv = ellipV0[1];
            double dv = ellipV0[2];

            // j indices - for even N: [1, 3, 5, ...], for odd N: [0, 2, 4, ...] but skip 0
            int pairs = nPoles / 2;

            for (int i = 0; i < pairs; i++) {
                int j;
                if (nPoles % 2 == 0) {
                    j = 2 * i + 1; // [1, 3, 5, ...]
                } else {
                    j = 2 * (i + 1); // [2, 4, 6, ...] for odd, skipping 0
                }

                double u = j * capk / nPoles;
                double[] ellipU = EllipticFunctions.ellipj(u, m);
                double s = ellipU[0];
                double c = ellipU[1];
                double d = ellipU[2];

                // Zero = j / (sqrt(m) * sn)
                double zeroImag = 1.0 / (sqrtM * s);
                Complex zero = new Complex(0, zeroImag);

                // Pole = -(c * d * sv * cv + j * s * dv) / (1 - (d * sv)^2)
                double denom = 1.0 - (d * sv) * (d * sv);
                double poleRe = -c * d * sv * cv / denom;
                double poleIm = s * dv / denom;

                Complex pole = new Complex(poleRe, poleIm);

                addPoleZeroConjugatePairs(pole, zero);
            }

            // Handle odd-order filter - real pole
            if (nPoles % 2 == 1) {
                // For odd order, there's a real pole
                // pole = -sv * cv / (1 - sv^2) approximately
                double poleReal = -sv; // Simplified
                Complex pole = new Complex(poleReal, 0);
                add(pole, Complex.INF);
            }

            // Set gain normalization
            // For even order, k = k / sqrt(1 + eps^2)
            if (nPoles % 2 == 0) {
                setNormal(0, 1.0 / Math.sqrt(1 + eps_sq));
            } else {
                setNormal(0, 1.0);
            }
        }

        /**
         * Ellipdeg - solve degree equation using nomes.
         */
        private double ellipdeg(int n, double m1) {
            double K1 = EllipticFunctions.ellipk(m1);
            double K1p = EllipticFunctions.ellipk(1.0 - m1);

            double q1 = Math.exp(-Math.PI * K1p / K1);
            double q = Math.pow(q1, 1.0 / n); // q = q1^(1/n)

            double num = 0;
            for (int mm = 0; mm <= ELLIPDEG_MMAX; mm++) {
                num += Math.pow(q, mm * (mm + 1));
            }

            double den = 1.0;
            for (int mm = 1; mm <= ELLIPDEG_MMAX + 1; mm++) {
                den += 2 * Math.pow(q, mm * mm);
            }

            return 16 * q * Math.pow(num / den, 4);
        }

        /**
         * arc_jac_sc1(w, m) - Real inverse Jacobian sc with complementary modulus.
         * Solve for z in: w = sc(z, 1-m) = sn(z, 1-m) / cn(z, 1-m)
         * 
         * Uses: arc_jac_sc(w, 1-m) = Im[arc_jac_sn(j*w, m)]
         */
        private double arcJacSc1(double w, double m) {
            // For the complementary modulus case:
            // sc(z, 1-m) = -i * sn(i*z, m) (identity)
            // So arc_sc(w, 1-m) = -i * arc_sn(i*w, m)

            // Compute arc_sn(i*w, m) and take imaginary part
            double[] result = arcJacSn(0, w, m); // arcJacSn takes (real, imag) of complex sn value
            return result[1]; // Return imaginary part
        }

        /**
         * Inverse Jacobian sn using Landen transformation.
         * Computes z such that sn(z, m) = w_real + j*w_imag
         * Returns [Re(z), Im(z)]
         */
        private double[] arcJacSn(double wReal, double wImag, double m) {
            double k = Math.sqrt(m);

            if (k >= 1) {
                // For k=1, sn(z,1) = tanh(z), so arc_sn = arctanh
                double w = Math.sqrt(wReal * wReal + wImag * wImag);
                return new double[] { 0.5 * Math.log((1 + w) / (1 - w)), 0 };
            }

            // Landen transformation
            List<Double> ks = new ArrayList<>();
            ks.add(k);

            int niter = 0;
            while (ks.get(ks.size() - 1) != 0 && niter < ARC_JAC_SN_MAXITER) {
                double k_ = ks.get(ks.size() - 1);
                double kp = Math.sqrt((1 - k_) * (1 + k_)); // complement
                double kNext = (1 - kp) / (1 + kp);
                ks.add(kNext);
                niter++;
                if (kNext < 1e-15)
                    break;
            }

            // K calculation
            double K = 1;
            for (int i = 1; i < ks.size(); i++) {
                K *= (1 + ks.get(i));
            }
            K *= Math.PI / 2;

            // wn transformation (complex arithmetic needed)
            double wnReal = wReal;
            double wnImag = wImag;

            for (int i = 0; i < ks.size() - 1; i++) {
                double kn = ks.get(i);
                double knext = ks.get(i + 1);

                // wnext = 2 * wn / ((1 + knext) * (1 + sqrt(1 - (kn*wn)^2)))
                // For complex wn, this is more involved

                // Simplified for real w along imaginary axis (which is our case)
                // wn = j*w is purely imaginary
                // 1 - (kn*wn)^2 = 1 - (kn*j*w)^2 = 1 + (kn*w)^2 (real, positive)

                double wMag = Math.sqrt(wnReal * wnReal + wnImag * wnImag);
                double factor = 1 + Math.sqrt(1 + kn * kn * wMag * wMag);
                double denom = (1 + knext) * factor;

                wnReal = 2 * wnReal / denom;
                wnImag = 2 * wnImag / denom;
            }

            // u = 2/pi * arcsin(wn_final)
            // For purely imaginary wn: arcsin(j*w) = j*asinh(w)
            // So u_imag = 2/pi * asinh(wnImag)
            double uImag = 2 / Math.PI * MathSupplement.asinh(wnImag);

            // z = K * u where u is purely imaginary
            // For purely imaginary input, result is purely imaginary
            return new double[] { 0, K * uImag };
        }
    }

    private void setupLowPass(int order, double rippleDb, double stopBandDb, double sampleRate,
            double cutoffFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, rippleDb, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order);

        new LowPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
                m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void lowPass(int order, double rippleDb, double stopBandDb, double sampleRate, double cutoffFrequency) {
        setupLowPass(order, rippleDb, stopBandDb, sampleRate, cutoffFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupHighPass(int order, double rippleDb, double stopBandDb, double sampleRate,
            double cutoffFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, rippleDb, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order);

        new HighPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
                m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void highPass(int order, double rippleDb, double stopBandDb, double sampleRate, double cutoffFrequency) {
        setupHighPass(order, rippleDb, stopBandDb, sampleRate, cutoffFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupBandStop(int order, double rippleDb, double stopBandDb, double sampleRate,
            double centerFrequency, double widthFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, rippleDb, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order * 2);

        new BandStopTransform(centerFrequency / sampleRate, widthFrequency
                / sampleRate, m_digitalProto, m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void bandStop(int order, double rippleDb, double stopBandDb, double sampleRate, double centerFrequency,
            double widthFrequency) {
        setupBandStop(order, rippleDb, stopBandDb, sampleRate, centerFrequency, widthFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupBandPass(int order, double rippleDb, double stopBandDb, double sampleRate,
            double centerFrequency, double widthFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, rippleDb, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order * 2);

        new BandPassTransform(centerFrequency / sampleRate, widthFrequency
                / sampleRate, m_digitalProto, m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void bandPass(int order, double rippleDb, double stopBandDb, double sampleRate, double centerFrequency,
            double widthFrequency) {
        setupBandPass(order, rippleDb, stopBandDb, sampleRate, centerFrequency, widthFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }
}
