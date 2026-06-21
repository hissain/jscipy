package com.hissain.jscipy.signal;

/**
 * Waveform generation utilities, mirroring {@code scipy.signal} waveform functions.
 * <p>
 * Provides methods to generate common test and analysis signals:
 * chirp, square wave, sawtooth wave, Gaussian pulse, and unit impulse.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/waveform/waveform_comparison_light.png"
 * alt="Waveform Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 */
public class Waveform {

    /**
     * Frequency-swept cosine generator (chirp signal).
     * Matches {@code scipy.signal.chirp}.
     *
     * @param t      Time array (seconds).
     * @param f0     Frequency at time t=0 (Hz).
     * @param t1     Time at which {@code f1} is defined (seconds).
     * @param f1     Frequency at time {@code t1} (Hz).
     * @param method Sweep type: "linear", "quadratic", "logarithmic", or "hyperbolic".
     * @return Chirp signal evaluated at each time in {@code t}.
     */
    public double[] chirp(double[] t, double f0, double t1, double f1, String method) {
        double[] result = new double[t.length];
        for (int i = 0; i < t.length; i++) {
            double phase = chirpPhase(t[i], f0, t1, f1, method);
            result[i] = Math.cos(phase);
        }
        return result;
    }

    /**
     * Frequency-swept cosine generator using linear sweep (default).
     *
     * @param t  Time array (seconds).
     * @param f0 Frequency at time t=0 (Hz).
     * @param t1 Time at which {@code f1} is defined (seconds).
     * @param f1 Frequency at time {@code t1} (Hz).
     * @return Chirp signal evaluated at each time in {@code t}.
     */
    public double[] chirp(double[] t, double f0, double t1, double f1) {
        return chirp(t, f0, t1, f1, "linear");
    }

    private double chirpPhase(double t, double f0, double t1, double f1, String method) {
        switch (method.toLowerCase()) {
            case "linear": {
                double beta = (f1 - f0) / t1;
                return 2 * Math.PI * (f0 * t + 0.5 * beta * t * t);
            }
            case "quadratic": {
                double beta = (f1 - f0) / (t1 * t1);
                return 2 * Math.PI * (f0 * t + beta * t * t * t / 3.0);
            }
            case "logarithmic": {
                if (f0 == f1) return 2 * Math.PI * f0 * t;
                double beta = t1 / Math.log(f1 / f0);
                return 2 * Math.PI * beta * f0 * (Math.pow(f1 / f0, t / t1) - 1.0);
            }
            case "hyperbolic": {
                if (f0 == f1) return 2 * Math.PI * f0 * t;
                double sing = -f1 * t1 / (f0 - f1);
                return 2 * Math.PI * (-sing * f0) * Math.log(Math.abs(1.0 - t / sing));
            }
            default:
                throw new IllegalArgumentException("Unknown chirp method: " + method);
        }
    }

    /**
     * Return a periodic square wave with the given duty cycle.
     * Matches {@code scipy.signal.square}.
     * <p>
     * The function takes values +1 and -1. The duty cycle is the fraction of
     * the period during which the signal is +1.
     *
     * @param t    Time array. The period is 2*pi radians.
     * @param duty Duty cycle (0 to 1). Default 0.5.
     * @return Square wave evaluated at each element of {@code t}.
     */
    public double[] square(double[] t, double duty) {
        double[] result = new double[t.length];
        for (int i = 0; i < t.length; i++) {
            double tmod = ((t[i] % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);
            result[i] = (tmod < 2 * Math.PI * duty) ? 1.0 : -1.0;
        }
        return result;
    }

    /**
     * Return a periodic square wave with duty cycle 0.5.
     *
     * @param t Time array. The period is 2*pi radians.
     * @return Square wave evaluated at each element of {@code t}.
     */
    public double[] square(double[] t) {
        return square(t, 0.5);
    }

    /**
     * Return a periodic sawtooth or triangle wave.
     * Matches {@code scipy.signal.sawtooth}.
     * <p>
     * The sawtooth waveform has a period of 2*pi, rising from -1 to +1 in
     * [0, 2*pi*width] and then dropping from +1 to -1 in [2*pi*width, 2*pi].
     *
     * @param t     Time array. The period is 2*pi radians.
     * @param width Width of the rising ramp (0 to 1). width=1 gives a pure sawtooth,
     *              width=0.5 gives a triangle wave.
     * @return Sawtooth wave evaluated at each element of {@code t}.
     */
    public double[] sawtooth(double[] t, double width) {
        double[] result = new double[t.length];
        for (int i = 0; i < t.length; i++) {
            double tmod = ((t[i] % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);
            if (tmod < width * 2 * Math.PI) {
                result[i] = (width == 0) ? Double.NaN : tmod / (Math.PI * width) - 1.0;
            } else {
                result[i] = (width == 1) ? Double.NaN
                        : (Math.PI * (width + 1) - tmod) / (Math.PI * (1 - width));
            }
        }
        return result;
    }

    /**
     * Return a periodic sawtooth wave (width=1).
     *
     * @param t Time array. The period is 2*pi radians.
     * @return Sawtooth wave evaluated at each element of {@code t}.
     */
    public double[] sawtooth(double[] t) {
        return sawtooth(t, 1.0);
    }

    /**
     * Return a Gaussian modulated sinusoid (Gaussian pulse).
     * Matches {@code scipy.signal.gausspulse} with {@code retquad=False, retenv=False}.
     * <p>
     * The pulse has a center frequency {@code fc} and a fractional bandwidth {@code bw}
     * at the -6 dB level.
     *
     * @param t  Time array (seconds).
     * @param fc Center frequency (Hz). Default 1000.
     * @param bw Fractional bandwidth (ratio). Default 0.5.
     * @return Gaussian modulated sinusoid evaluated at each element of {@code t}.
     */
    public double[] gausspulse(double[] t, double fc, double bw) {
        double bwr = -6.0;
        double ref = Math.pow(10.0, bwr / 20.0);
        double a = -(Math.PI * fc * bw) * (Math.PI * fc * bw) / (4.0 * Math.log(ref));
        double[] result = new double[t.length];
        for (int i = 0; i < t.length; i++) {
            result[i] = Math.exp(-a * t[i] * t[i]) * Math.cos(2 * Math.PI * fc * t[i]);
        }
        return result;
    }

    /**
     * Return a Gaussian modulated sinusoid with default bandwidth of 0.5.
     *
     * @param t  Time array (seconds).
     * @param fc Center frequency (Hz).
     * @return Gaussian modulated sinusoid evaluated at each element of {@code t}.
     */
    public double[] gausspulse(double[] t, double fc) {
        return gausspulse(t, fc, 0.5);
    }

    /**
     * Return a unit impulse (discrete delta function) signal.
     * Matches {@code scipy.signal.unit_impulse}.
     * <p>
     * Returns a signal of length {@code shape} that is zero everywhere except at
     * index {@code idx} where it equals 1.
     *
     * @param shape Total length of the output signal.
     * @param idx   Index at which the impulse occurs. Use -1 for center (shape/2).
     * @return Unit impulse signal of length {@code shape}.
     */
    public double[] unitImpulse(int shape, int idx) {
        double[] result = new double[shape];
        int pos = (idx == -1) ? shape / 2 : idx;
        if (pos < 0 || pos >= shape) {
            throw new IllegalArgumentException("idx out of bounds for shape " + shape);
        }
        result[pos] = 1.0;
        return result;
    }

    /**
     * Return a unit impulse at index 0 (default).
     *
     * @param shape Total length of the output signal.
     * @return Unit impulse signal with impulse at index 0.
     */
    public double[] unitImpulse(int shape) {
        return unitImpulse(shape, 0);
    }
}
