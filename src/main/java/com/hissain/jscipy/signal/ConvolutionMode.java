package com.hissain.jscipy.signal;

/**
 * Enum representing the mode of convolution.
 */
public enum ConvolutionMode {
    /**
     * The output is the full discrete linear convolution of the inputs.
     */
    FULL,
    /**
     * The output consists only of those elements that do not rely on the zero-padding.
     */
    VALID,
    /**
     * The output is the same size as the first input.
     */
    SAME
}
