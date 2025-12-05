package com.hissain.jscipy.signal;

import java.util.Arrays;

class MedFilt {

    public double[] medfilt(double[] data, int kernelSize) {
        if (kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Kernel size must be odd.");
        }
        int halfKernel = kernelSize / 2;
        double[] result = new double[data.length];
        double[] window = new double[kernelSize];

        for (int i = 0; i < data.length; i++) {
            for (int k = 0; k < kernelSize; k++) {
                int idx = i - halfKernel + k;
                if (idx < 0 || idx >= data.length) {
                    window[k] = 0.0;
                } else {
                    window[k] = data[idx];
                }
            }
            Arrays.sort(window);
            result[i] = window[halfKernel];
        }
        return result;
    }
}
