package com.hissain.jscipy.signal;

/**
 * Enum representing the type of detrending.
 */
public enum DetrendType {
    /**
     * Removes a linear trend from the signal.
     */
    LINEAR,
    /**
     * Removes a constant trend (mean) from the signal.
     */
    CONSTANT
}
