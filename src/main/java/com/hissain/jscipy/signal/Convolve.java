package com.hissain.jscipy.signal;

class Convolve {

    public double[] convolve(double[] signal, double[] window, ConvolutionMode mode) {
        if (mode != ConvolutionMode.SAME) {
            throw new UnsupportedOperationException("Only 'same' mode is supported for now.");
        }
        int signalLen = signal.length;
        int windowLen = window.length;
        double[] result = new double[signalLen];
        int windowCenter = windowLen / 2;
        double windowSum = 0;
        for (double v : window) {
            windowSum += v;
        }

        for (int i = 0; i < signalLen; i++) {
            double sum = 0;
            for (int j = 0; j < windowLen; j++) {
                int signalIndex = i - windowCenter + j;
                if (signalIndex >= 0 && signalIndex < signalLen) {
                    sum += signal[signalIndex] * window[j];
                }
            }
            result[i] = sum / windowSum;
        }
        return result;
    }
}
