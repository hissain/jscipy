package com.hissain.jscipy.signal.filter;

import com.hissain.jscipy.Signal;

/**
 * Bessel (Thomson) Filter implementation.
 * <p>
 * Provides static methods for designing and applying Bessel filters,
 * characterized by maximal flatness of the group delay (linear phase response).
 */
public class Bessel {

    /**
     * Design a low-pass Bessel filter.
     *
     * @param sampleRate The sampling rate in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The order of the filter.
     * @return Array of Biquad sections representing the filter.
     */
    public static Biquad[] lowPass(double sampleRate, double cutoff, int order) {
        BesselDesign design = new BesselDesign(order, "phase");
        design.lowPass(order, sampleRate, cutoff);
        return design.getBiquads();
    }

    /**
     * Design a high-pass Bessel filter.
     *
     * @param sampleRate The sampling rate in Hz.
     * @param cutoff     The cutoff frequency in Hz.
     * @param order      The order of the filter.
     * @return Array of Biquad sections representing the filter.
     */
    public static Biquad[] highPass(double sampleRate, double cutoff, int order) {
        BesselDesign design = new BesselDesign(order, "phase");
        design.highPass(order, sampleRate, cutoff);
        return design.getBiquads();
    }

    /**
     * Design a band-pass Bessel filter.
     *
     * @param sampleRate The sampling rate in Hz.
     * @param lowCutoff  The lower cutoff frequency in Hz.
     * @param highCutoff The upper cutoff frequency in Hz.
     * @param order      The order of the filter.
     * @return Array of Biquad sections representing the filter.
     */
    public static Biquad[] bandPass(double sampleRate, double lowCutoff, double highCutoff, int order) {
        BesselDesign design = new BesselDesign(order, "phase");
        double center = Math.sqrt(lowCutoff * highCutoff);
        double width = highCutoff - lowCutoff;
        design.bandPass(order, sampleRate, center, width);
        return design.getBiquads();
    }

    /**
     * Design a band-stop (notch) Bessel filter.
     *
     * @param sampleRate The sampling rate in Hz.
     * @param lowCutoff  The lower cutoff frequency in Hz.
     * @param highCutoff The upper cutoff frequency in Hz.
     * @param order      The order of the filter.
     * @return Array of Biquad sections representing the filter.
     */
    public static Biquad[] bandStop(double sampleRate, double lowCutoff, double highCutoff, int order) {
        BesselDesign design = new BesselDesign(order, "phase");
        double center = Math.sqrt(lowCutoff * highCutoff);
        double width = highCutoff - lowCutoff;
        design.bandStop(order, sampleRate, center, width);
        return design.getBiquads();
    }

    /**
     * Apply a Bessel filter forward and backward (zero-phase filtering).
     * Defaults to low-pass filter.
     *
     * @param signal     Input signal array.
     * @param sampleRate Sampling rate in Hz.
     * @param cutoff     Cutoff frequency in Hz.
     * @param order      Filter order.
     * @return Filtered signal with zero phase distortion.
     */
    public static double[] filtfilt(double[] signal, double sampleRate, double cutoff, int order) {
        Biquad[] sos = lowPass(sampleRate, cutoff, order);
        return runFiltFilt(signal, sos);
    }

    /**
     * Apply a zero-phase Bessel high-pass filter (filtfilt).
     *
     * @param signal     Input signal array.
     * @param sampleRate Sampling rate in Hz.
     * @param cutoff     Cutoff frequency in Hz.
     * @param order      Filter order.
     * @return Filtered signal with zero phase distortion.
     */
    public static double[] filtfilt_highpass(double[] signal, double sampleRate, double cutoff, int order) {
        Biquad[] sos = highPass(sampleRate, cutoff, order);
        return runFiltFilt(signal, sos);
    }

    /**
     * Apply a zero-phase Bessel band-pass filter (filtfilt).
     *
     * @param signal     Input signal array.
     * @param sampleRate Sampling rate in Hz.
     * @param lowCutoff  Lower cutoff frequency in Hz.
     * @param highCutoff Upper cutoff frequency in Hz.
     * @param order      Filter order.
     * @return Filtered signal with zero phase distortion.
     */
    public static double[] filtfilt_bandpass(double[] signal, double sampleRate, double lowCutoff,
            double highCutoff, int order) {
        Biquad[] sos = bandPass(sampleRate, lowCutoff, highCutoff, order);
        return runFiltFilt(signal, sos);
    }

    /**
     * Apply a zero-phase Bessel band-stop (notch) filter (filtfilt).
     *
     * @param signal     Input signal array.
     * @param sampleRate Sampling rate in Hz.
     * @param lowCutoff  Lower cutoff frequency in Hz.
     * @param highCutoff Upper cutoff frequency in Hz.
     * @param order      Filter order.
     * @return Filtered signal with zero phase distortion.
     */
    public static double[] filtfilt_bandstop(double[] signal, double sampleRate, double lowCutoff,
            double highCutoff, int order) {
        Biquad[] sos = bandStop(sampleRate, lowCutoff, highCutoff, order);
        return runFiltFilt(signal, sos);
    }

    private static double[] runFiltFilt(double[] signal, Biquad[] sos) {
        int n = signal.length;
        int nSections = sos.length;
        // Bessel filters have slow settling time, but if we use proper initialization
        // (zi),
        // we can use standard padding length to match SciPy.
        int padlen = 3 * (2 * nSections + 1);
        if (padlen > n - 1) {
            padlen = n - 1;
        }

        // Forward filter with padding
        double[] padded = Signal.padSignal(signal, padlen);

        // Create states
        DirectFormII[] states = new DirectFormII[nSections];
        for (int i = 0; i < nSections; i++)
            states[i] = new DirectFormII();

        // Initialize states for Step Response (Forward)
        // This sets the internal delays to the steady-state values corresponding
        // to a constant input of padded[0].
        initializeStates(states, sos, padded[0]);

        // Pass 1: Forward
        for (int i = 0; i < padded.length; i++) {
            double val = padded[i];
            for (int s = 0; s < nSections; s++) {
                val = states[s].process1(val, sos[s]);
            }
            padded[i] = val;
        }

        // Reverse
        reverse(padded);

        // Initialize states for Step Response (Backward)
        // Input to backward pass is the reversed output of forward pass.
        // We initialize steady state based on its first sample.
        initializeStates(states, sos, padded[0]);

        // Pass 2: Backward
        for (int i = 0; i < padded.length; i++) {
            double val = padded[i];
            for (int s = 0; s < nSections; s++) {
                val = states[s].process1(val, sos[s]);
            }
            padded[i] = val;
        }

        // Reverse back
        reverse(padded);

        // Strip padding
        double[] output = new double[n];
        System.arraycopy(padded, padlen, output, 0, n);

        return output;
    }

    private static void initializeStates(DirectFormII[] states, Biquad[] sos, double initialInput) {
        double currentInput = initialInput;
        for (int i = 0; i < states.length; i++) {
            Biquad s = sos[i];
            // Calculate steady state for Standard Direct Form II
            // w_ss = input / (1 + a1 + a2)
            // v1 = v2 = w_ss
            double sumA = 1.0 + s.m_a1 + s.m_a2;
            double w_ss = currentInput / sumA;
            states[i].convertAndSetState(w_ss, w_ss);

            // Calculate output steady state for next stage
            // y_ss = w_ss * (b0 + b1 + b2)
            double sumB = s.m_b0 + s.m_b1 + s.m_b2;
            currentInput = w_ss * sumB;
        }
    }

    private static void reverse(double[] array) {
        int left = 0;
        int right = array.length - 1;
        while (left < right) {
            double temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }
}
