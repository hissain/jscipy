package com.hissain.jscipy.signal;

/**
 * Represents a complex number with real and imaginary parts.
 * This class is an immutable structure designed to hold complex values
 * without exposing external dependencies.
 */
public class JComplex {
    /**
     * A constant representing the complex number 0 + 0i.
     */
    public static final JComplex ZERO = new JComplex(0.0, 0.0);

    /**
     * The real part of the complex number.
     */
    public final double real;

    /**
     * The imaginary part of the complex number.
     */
    public final double imag;

    /**
     * Constructs a new complex number.
     *
     * @param real The real part.
     * @param imag The imaginary part.
     */
    public JComplex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Gets the real part of this complex number.
     *
     * @return The real part.
     */
    public double getReal() {
        return real;
    }

    /**
     * Gets the imaginary part of this complex number.
     *
     * @return The imaginary part.
     */
    public double getImaginary() {
        return imag;
    }

    /**
     * Returns the conjugate of this complex number.
     * The conjugate of a + bi is a - bi.
     *
     * @return A new JComplex representing the conjugate.
     */
    public JComplex conjugate() {
        return new JComplex(real, -imag);
    }

    /**
     * Multiplies this complex number by a scalar factor.
     *
     * @param factor The scalar value to multiply by.
     * @return A new JComplex representing the result.
     */
    public JComplex multiply(double factor) {
        return new JComplex(real * factor, imag * factor);
    }

    /**
     * Adds another complex number to this one.
     *
     * @param other The complex number to add.
     * @return A new JComplex representing the sum.
     */
    public JComplex add(JComplex other) {
        return new JComplex(real + other.real, imag + other.imag);
    }
}
