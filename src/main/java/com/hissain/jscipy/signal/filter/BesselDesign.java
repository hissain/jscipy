package com.hissain.jscipy.signal.filter;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;

/**
 * Design Bessel-Thomson filters.
 * <p>
 * This class implements the design of Bessel analog prototypes and converts
 * them
 * to digital filters using the bilinear transform.
 * Use {@link Bessel} for the user-facing API.
 */
class BesselDesign extends Cascade {

    class AnalogLowPass extends LayoutBase {
        private int nPoles;
        private String norm;

        public AnalogLowPass(int _nPoles, String _norm) {
            super(_nPoles);
            nPoles = _nPoles;
            norm = _norm;
        }

        public void design() {
            reset(); // LayoutBase.reset()
            addPoles(calculatePoles(nPoles));
            setNormal(0, 1.0); // Normalize at DC
        }

        private void addPoles(Complex[] poles) {
            // Pairs conjugate poles
            boolean[] used = new boolean[poles.length];

            for (int i = 0; i < poles.length; i++) {
                if (used[i])
                    continue;

                Complex p1 = poles[i];

                // Check if real
                if (Math.abs(p1.getImaginary()) < 1e-9) {
                    add(p1, Complex.INF);
                    used[i] = true;
                    continue;
                }

                // Find conjugate
                int bestJ = -1;

                for (int j = i + 1; j < poles.length; j++) {
                    if (used[j])
                        continue;

                    Complex p2 = poles[j];
                    // Check if conjugate: real parts close, imag parts opposite
                    if (Math.abs(p1.getReal() - p2.getReal()) < 1e-9 &&
                            Math.abs(p1.getImaginary() + p2.getImaginary()) < 1e-9) {
                        bestJ = j;
                        break;
                    }
                }

                if (bestJ != -1) {
                    addPoleZeroConjugatePairs(p1, Complex.INF);
                    used[i] = true;
                    used[bestJ] = true;
                } else {
                    add(p1, Complex.INF);
                    used[i] = true;
                }
            }
        }

        private Complex[] calculatePoles(int n) {
            // Calculate coefficients of reverse Bessel polynomial
            double[] coeffs = new double[n + 1];
            for (int k = 0; k <= n; k++) {
                coeffs[k] = reverseBesselCoeff(n, k);
            }

            // Find roots using LaguerreSolver
            LaguerreSolver solver = new LaguerreSolver();
            Complex[] roots = solver.solveAllComplex(coeffs, 0);

            // Normalize
            // norm='phase': Scale by (a0)^(-1/n)
            if ("phase".equals(norm)) {
                double a0 = coeffs[0];
                double scale = Math.pow(a0, -1.0 / n);
                for (int i = 0; i < roots.length; i++) {
                    roots[i] = roots[i].multiply(scale);
                }
            }
            return roots;
        }

        private double reverseBesselCoeff(int n, int k) {
            return factorial(2 * n - k) / (Math.pow(2, n - k) * factorial(k) * factorial(n - k));
        }

        private double factorial(int x) {
            if (x <= 1)
                return 1;
            double res = 1;
            for (int i = 2; i <= x; i++)
                res *= i;
            return res;
        }
    }

    private final String norm;

    public BesselDesign(int order, String norm) {
        // order parameter is passed to setupXxx methods, not stored
        this.norm = (norm != null) ? norm : "phase";
    }

    public BesselDesign(int order) {
        this(order, "phase");
    }

    // --- Setup Methods ---

    private void setupLowPass(int order, double sampleRate,
            double cutoffFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, norm);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order);

        new LowPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
                m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void lowPass(int order, double sampleRate, double cutoffFrequency) {
        setupLowPass(order, sampleRate, cutoffFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupHighPass(int order, double sampleRate,
            double cutoffFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, norm);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order);

        new HighPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
                m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void highPass(int order, double sampleRate, double cutoffFrequency) {
        setupHighPass(order, sampleRate, cutoffFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupBandPass(int order, double sampleRate,
            double centerFrequency, double widthFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, norm);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order * 2);

        new BandPassTransform(centerFrequency / sampleRate, widthFrequency
                / sampleRate, m_digitalProto, m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void bandPass(int order, double sampleRate, double centerFrequency,
            double widthFrequency) {
        setupBandPass(order, sampleRate, centerFrequency, widthFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupBandStop(int order, double sampleRate,
            double centerFrequency, double widthFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, norm);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order * 2);

        new BandStopTransform(centerFrequency / sampleRate, widthFrequency
                / sampleRate, m_digitalProto, m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void bandStop(int order, double sampleRate, double centerFrequency,
            double widthFrequency) {
        setupBandStop(order, sampleRate, centerFrequency, widthFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }
}
