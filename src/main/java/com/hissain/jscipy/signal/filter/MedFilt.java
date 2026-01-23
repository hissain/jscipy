package com.hissain.jscipy.signal.filter;

import java.util.Arrays;

/**
 * Implements a median filter for signal smoothing.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/medfilt/medfilt_comparison_light.png"
 * alt="Median Filter Comparison" style="width: 50%; min-width: 300px; display: block; margin: 0 auto;">
 */
public class MedFilt {

    /**
     * Applies a median filter to the signal.
     * <p>
     * <img src=
     * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/medfilt/medfilt_comparison_light.png"
     * alt="Median Filter Comparison" style="width: 50%; min-width: 300px; display: block; margin: 0 auto;">
     *
     * @param signal     The input signal.
     * @param kernelSize The size of the kernel (must be odd).
     * @return The filtered signal.
     */
    public double[] medfilt(double[] signal, int kernelSize) {
        if (kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Kernel size must be odd.");
        }
        int halfKernel = kernelSize / 2;
        double[] result = new double[signal.length];
        double[] window = new double[kernelSize];

        for (int i = 0; i < signal.length; i++) {
            for (int k = 0; k < kernelSize; k++) {
                int idx = i - halfKernel + k;
                if (idx < 0 || idx >= signal.length) {
                    window[k] = 0.0;
                } else {
                    window[k] = signal[idx];
                }
            }
            Arrays.sort(window);
            result[i] = window[halfKernel];
        }
        return result;
    }
}
