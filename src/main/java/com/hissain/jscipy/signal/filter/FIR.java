package com.hissain.jscipy.signal.filter;

import com.hissain.jscipy.signal.Windows;

/**
 * Finite Impulse Response (FIR) filter design methods.
 * Matches scipy.signal.firwin
 * <p>
 * <picture>
 * <source media="(prefers-color-scheme: dark)" srcset=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/fir/fir_comparison_dark.png">
 * <source media="(prefers-color-scheme: light)" srcset=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/fir/fir_comparison_light.png">
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/fir/fir_comparison_light.png"
 * alt="FIR Filter Verification" style="width: 600px; max-width: 90%; display:
 * block; margin: 0 auto;">
 * </picture>
 */
public class FIR {

    /**
     * Sinc function: sin(pi * x) / (pi * x)
     */
    private static double sinc(double x) {
        if (Math.abs(x) < 1e-9) {
            return 1.0;
        }
        x *= Math.PI;
        return Math.sin(x) / x;
    }

    /**
     * Design an FIR filter using the window method.
     *
     * @param numtaps      The number of taps in the FIR filter. FIR filter length.
     * @param cutoff       The cutoff frequency (relative to fs/2).
     * @param windowParams Optional window parameters. If slightly confusing usage,
     *                     simplified API provided below.
     * @return The filter coefficients.
     */
    // Internal generic implementation
    private double[] firwin_impl(int numtaps, double[] cutoff, boolean pass_zero, String window, double fs) {
        // Normalize cutoff to Nyquist
        double nyq = fs / 2.0;
        double[] normalized_cutoff = new double[cutoff.length];
        for (int i = 0; i < cutoff.length; i++) {
            normalized_cutoff[i] = cutoff[i] / nyq;
            if (normalized_cutoff[i] <= 0 || normalized_cutoff[i] >= 1) {
                throw new IllegalArgumentException(
                        "Invalid cutoff frequency: " + cutoff[i] + ". Must be 0 < f < fs/2.");
            }
        }

        // Build the ideal filter response
        double[] h = new double[numtaps];
        double alpha = (numtaps - 1) / 2.0;

        // This implementation follows the specific logic for Type I/II filters
        // (symmetric)
        // Calculating ideal impulse response h(n)

        // Standard formula:
        // h[n] = 2f_c * sinc(2f_c * (n - alpha))

        // Handling multiple bands (generalized):
        // If pass_zero is true (starts with passband):
        // Response is sum of (cutoff[i+1] - cutoff[i]) bands?
        // Simpler approach:
        // H(f) starts at 1. at first cutoff goes to 0. next cutoff goes to 1...
        // This is "add lowpass(c0) - lowpass(c1) + lowpass(c2)..." ?

        // Let's implement based on SciPy `firwin` logic:
        // It computes "bands" of frequencies.
        // For each band edge `f`:
        // Add `m * 2 * f * sinc(2 * f * (n - alpha))` where m is change in gain?

        // If pass_zero (Lowpass like):
        // Gain 1 from 0 to f1. Gain 0 from f1 to f2...
        // Change at f1 is -1.
        // So h[n] = 1 (DC) - 1 * 2*f1*sinc(...) ?
        // Actually, lowpass(f) corresponds to gain 1 from 0 to f.
        // h[n] = 2*f*sinc(...)

        // If Highpass (pass_zero=false):
        // Gain 0 from 0 to f1. Gain 1 from f1 to ...
        // Change at f1 is +1.
        // So h[n] = 0 (DC) + 1 * 2*f1*sinc(...)
        // But we need to account for DC term.
        // Ideal highpass = delta[n] - lowpass.

        // General Superposition:
        // h[n] = sum { (gain_right - gain_left) * 2 * f_edge * sinc(2*f_edge*(n-alpha))
        // }
        // For 0 frequency (DC), if pass_zero is true, gain starts at 1. If false,
        // starts at 0.
        // But 2*0*sinc is 0.
        // The "offset" comes from the integration constant?
        // Usually:
        // h[n] = int_{-0.5}^{0.5} H(f) exp(j 2pi f (n-alpha)) df
        // If pass_zero:
        // Intervals: [0, c1], [c2, c3], ...
        // h[n] = 2c1 sinc(2c1(n-a)) + 2(c3-c2) sinc... No.

        // Correct superposition for multiple bands:
        // Current gain `cur_gain`. Starts at 1 if pass_zero, else 0.
        // At each cutoff `c`, gain flips (0->1 or 1->0).
        // Change `d = new_gain - cur_gain`.
        // Add `d * 2 * c * sinc(2 * c * (n - alpha))` to h[n].
        // Finally, if `pass_zero` was true, does it need a base?
        // No, standard integration works out nicely EXCEPT for DC term behavior?
        // Wait, standard Sinc is integral of Rect [-f, f].
        // This corresponds to Lowpass.
        // Lowpass(f) has gain 1 at DC.
        // So if we start with Lowpass (pass_zero=True), we sum +2*c*sinc.
        // If we start with Highpass (pass_zero=False):
        // We want 1 - Lowpass.
        // H_high(f) = 1 - H_low(f).
        // h_high[n] = delta[n] - h_low[n].
        // In generalized loop:
        // If pass_zero=True (Lowpass, Bandstop):
        // bands are [0, c1], [c2, c3]...
        // h = Lowpass(c1) + (Lowpass(c3) - Lowpass(c2)) + ...
        // This matches my formula derived?
        // Yes.
        // If pass_zero=False (Highpass, Bandpass):
        // bands are [c1, c2], [c3, c4]...
        // h = (Lowpass(c2) - Lowpass(c1)) + ...

        // So the algorithm is:
        // Initialize h[n] = 0.
        // Convert cutoffs to normalized.
        // If pass_zero:
        // edges: 0, c1, c2, c3...
        // pairs: (0, c1), (c2, c3)...
        // For each pair (left, right):
        // if left==0: h += 2*right*sinc
        // else: h += 2*right*sinc - 2*left*sinc
        // If not pass_zero:
        // edges: c1, c2, c3, c4...
        // pairs: (c1, c2), (c3, c4)...
        // For each pair:
        // h += 2*right*sinc - 2*left*sinc
        // (If odd number of cutoffs, last one goes to Nyquist 0.5? i.e. cutoff[end] to
        // 0.5)
        // Wait, firwin API usually takes explicit cutoffs.
        // If 'bandpass' with [f1, f2], pass_zero=False.
        // h = Lowpass(f2) - Lowpass(f1).
        // If 'highpass' with [f1], pass_zero=False.
        // h = Lowpass(0.5) - Lowpass(f1)?
        // Lowpass(0.5) is delta functions (all pass).
        // Yes, 2*0.5*sinc(2*0.5*x) = sinc(x). Normalized sinc(k) is discrete delta?
        // Not exactly in discretized time unless special care.
        // Scipy uses: `h[n] = delta(n-alpha) - ...` logic for highpass.

        // Refined Algorithm (matching SciPy `firwin` exactly):
        // 1. Define bands based on pass_zero.
        // `pass_nyquist` depends on numtaps and symmetries.
        // 2. Compute `h[n]` by summing band contributions.
        // Band [f1, f2]: term = 2*f2*sinc(2*f2*(n-a)) - 2*f1*sinc(2*f1*(n-a))
        // (If f2=0.5, term is sinc(n-a) - ...)
        // (If f1=0, term is ... - 0)

        // Handling the "High frequency" (f=0.5) case:
        // If a band extends to Nyquist (0.5), the Fourier integral of 1 from f_low to
        // 0.5 corresponds to:
        // delta[n] - 2*f_low*sinc... ?
        // Ideally yes. If n==alpha, delta=1. Else 0.

        for (int n = 0; n < numtaps; n++) {
            double val = 0;
            double m = n - alpha;

            // Logic: toggle gain at each cutoff.
            // Current theoretical gain starts at (pass_zero ? 1 : 0).
            double current_gain = pass_zero ? 1.0 : 0.0;

            // We iterate through cutoffs.
            // At each cutoff, gain flips.
            // Change is (new - old).
            // We add change * 2 * f * sinc(2 * f * m)

            for (int i = 0; i < normalized_cutoff.length; i++) {
                double f = normalized_cutoff[i];
                // Gain flips
                double new_gain = (current_gain == 1.0) ? 0.0 : 1.0;
                // Change = current - new (left - right) for superposition
                double change = current_gain - new_gain;

                val += change * f * sinc(f * m);

                current_gain = new_gain;
            }

            // If after all cutoffs, current_gain is 1.0, it means we pass Nyquist.
            // The band is open-ended [last_cutoff, 0.5].
            // We need to add the contribution for the upper edge (0.5).
            // Contribution of edge f is -change * ... ? No.
            // Let's use the Superposition:
            // h = sum( (gain_right - gain_left) * Integral term )
            // We treated cutoffs as steps.
            // If the final state is 1, it means we effectively have a step DOWN at 0.5?
            // No, the integral goes up to 0.5.
            // If gain is 1 at 0.5, we have a component "1 * 2*0.5*sinc( m )" ?
            // Yes.
            if (current_gain == 1.0) {
                // Add Nyquist term
                // 2 * 0.5 * sinc(2 * 0.5 * m) = sinc(m)
                // sinc(m) is 1 if m=0, 0 integer m!=0.
                // But m = n - alpha.
                // If numtaps is odd, alpha is integer. m is integer. sinc(m) is
                // delta[n_center].
                // If numtaps is even, alpha is x.5. m is x.5. sinc(m) is generic.

                val += sinc(m);
            }

            h[n] = val;
        }

        // Apply Window
        double[] w = Windows.get_window(window, numtaps);
        for (int i = 0; i < numtaps; i++) {
            h[i] *= w[i];
        }

        // Scaling
        // SciPy defaults to scale=True (scale to unity gain at center of passband).
        // Passband center:
        // If pass_zero, 0 Hz.
        // If not pass_zero (e.g. bandpass), center of first band?
        // SciPy `firwin` checks first passband.
        // If pass_zero, scale at 0 Hz (sum of coeffs).
        // If not pass_zero, scale at center of first passband.

        // Simple scaling:
        // If pass_zero, sum(h) should be 1.
        // If not pass_zero:
        // If cutoffs=[0.1, 0.2] (bandpass), center = 0.15 * Hz.
        // Calculate response at f_center and scale.
        // Response H(f) = sum h[n] exp(-j 2pi f n)
        // Magnitude |H(f)| = 1.

        // Let's simplified handling:
        // Most common cases.
        // If user wants scaling (default true).

        // Our API will support a helper to generic scale?
        // For now, implement simple 'unity gain at DC' for lowpass?
        // SciPy behavior:
        // If pass_zero: scale sum to 1.
        // If not pass_zero: scale frequency response at center of first band to 1.

        if (true) { // Always scale for now (or make parameter)
            double scaleFrequency = 0.0;
            if (pass_zero) {
                scaleFrequency = 0.0;
            } else {
                // If Highpass (1 cutoff), scale at Nyquist (1.0).
                // If Bandpass (2 cutoffs), scale at center of band.
                if (normalized_cutoff.length == 1) {
                    scaleFrequency = 1.0;
                } else {
                    scaleFrequency = 0.5 * (normalized_cutoff[0] + normalized_cutoff[1]);
                }
            }

            // Compute response at scaleFrequency
            // H(f) = sum h[k] * exp(-j * PI * f_norm * k)? No.
            // Response at normalized freq F (0..1):
            // omega = PI * F.
            // H = sum h[n] e^{-j n omega} ?
            // Wait, F=1 is Nyquist. omega=PI.

            JComplex resp = new JComplex(0, 0);
            double omega = Math.PI * scaleFrequency * 2.0; // Wait. Normalized to fs/2=1 implies 0.5 is fs/4.
            // Standard: normalized f' = f / (fs/2). range 0..1.
            // omega = f' * PI.

            // SciPy: freq argument to freqz is normalized to Nyquist?
            // let's stick to definition:
            // exp(-j * 2 * PI * (f/fs) * n)
            // f_norm = f / (fs/2) => f = f_norm * fs / 2.
            // exp(-j * 2 * PI * (f_norm/2) * n) = exp(-j * PI * f_norm * n).

            omega = Math.PI * scaleFrequency;

            for (int i = 0; i < numtaps; i++) {
                double angle = -i * omega;
                resp = resp.add(new JComplex(Math.cos(angle) * h[i], Math.sin(angle) * h[i]));
            }

            double mag = resp.abs();
            if (Math.abs(mag) > 1e-15) {
                for (int i = 0; i < numtaps; i++)
                    h[i] /= mag;
            }
        }

        return h;
    }

    // Public Facade

    /**
     * Design an FIR filter using the window method.
     * Default window is "hamming".
     *
     * @param numtaps   The number of taps.
     * @param cutoff    Cutoff frequency (Hz). Scalar or array.
     * @param fs        Sampling frequency (Hz).
     * @param pass_zero If true, the zero frequency is passed (Lowpass, Bandstop).
     *                  If false, the zero frequency is stopped (Highpass,
     *                  Bandpass).
     * @return Filter coefficients.
     */
    public static double[] firwin(int numtaps, double[] cutoff, double fs, boolean pass_zero) {
        return new FIR().firwin_impl(numtaps, cutoff, pass_zero, "hamming", fs);
    }

    /**
     * Design a Lowpass FIR filter.
     *
     * @param numtaps The number of taps.
     * @param cutoff  Cutoff frequency (Hz).
     * @param fs      Sampling frequency (Hz).
     * @return Filter coefficients.
     */
    public static double[] firwin_lowpass(int numtaps, double cutoff, double fs) {
        return firwin(numtaps, new double[] { cutoff }, fs, true);
    }

    /**
     * Design a Highpass FIR filter.
     * (Requires odd number of taps for strict highpass usually, but window method
     * handles evens with bad response at Pi).
     *
     * @param numtaps The number of taps.
     * @param cutoff  Cutoff frequency (Hz).
     * @param fs      Sampling frequency (Hz).
     * @return Filter coefficients.
     */
    public static double[] firwin_highpass(int numtaps, double cutoff, double fs) {
        return firwin(numtaps, new double[] { cutoff }, fs, false);
    }

    /**
     * Design a Bandpass FIR filter.
     *
     * @param numtaps The number of taps.
     * @param low     Lower cutoff frequency (Hz).
     * @param high    Upper cutoff frequency (Hz).
     * @param fs      Sampling frequency (Hz).
     * @return Filter coefficients.
     */
    public static double[] firwin_bandpass(int numtaps, double low, double high, double fs) {
        return firwin(numtaps, new double[] { low, high }, fs, false);
    }

    /**
     * Design a Bandstop FIR filter.
     *
     * @param numtaps The number of taps.
     * @param low     Lower cutoff frequency (Hz).
     * @param high    Upper cutoff frequency (Hz).
     * @param fs      Sampling frequency (Hz).
     * @return Filter coefficients.
     */
    public static double[] firwin_bandstop(int numtaps, double low, double high, double fs) {
        return firwin(numtaps, new double[] { low, high }, fs, true);
    }

    // Internal Complex Helper (minimal)
    private static class JComplex {
        double real, imag;

        public JComplex(double r, double i) {
            real = r;
            imag = i;
        }

        public JComplex add(JComplex o) {
            return new JComplex(real + o.real, imag + o.imag);
        }

        public double abs() {
            return Math.hypot(real, imag);
        }
    }

}
