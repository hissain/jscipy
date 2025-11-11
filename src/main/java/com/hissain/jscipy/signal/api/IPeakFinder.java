package com.hissain.jscipy.signal.api;

import com.hissain.jscipy.signal.PeakFinder;

/**
 * {@code IPeakFinder} defines the public API for peak finding functionality within the jSciPy library.
 * It provides methods to identify local maxima in 1D signals with various filtering options.
 *
 * <p>Implementations of this interface should provide robust and efficient peak detection,
 * mimicking the behavior of Python's {@code scipy.signal.find_peaks}.</p>
 */
public interface IPeakFinder {

    /**
     * Finds peaks (local maxima) in a 1D signal based on the provided parameters.
     *
     * <p>This method identifies indices where the signal value is greater than its immediate neighbors.
     * Further filtering can be applied using the {@code params} object to refine the peak detection
     * based on criteria such as minimum distance between peaks, minimum peak height, or minimum prominence.</p>
     *
     * @param signal The input 1D signal as a {@code double} array. Must not be {@code null} or empty.
     * @param params An instance of {@link PeakFinder.PeakParams} containing optional parameters
     *               for filtering the detected peaks. If {@code null}, default parameters (no filtering) are used.
     * @return A {@link PeakFinder.PeakResult} object containing an array of peak indices
     *         and a map of properties (though currently the properties map is empty).
     *         Returns an empty {@code PeakResult} if the input signal is {@code null} or empty.
     */
    PeakFinder.PeakResult findPeaks(double[] signal, PeakFinder.PeakParams params);
}
