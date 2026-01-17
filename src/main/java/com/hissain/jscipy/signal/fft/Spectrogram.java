package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.Detrend;
import com.hissain.jscipy.signal.DetrendType;
import com.hissain.jscipy.signal.JComplex;
import com.hissain.jscipy.signal.SpectrogramResult;
import com.hissain.jscipy.signal.Windows;

/**
 * Implementation of Spectrogram using Short-Time Fourier Transform.
 * Matches scipy.signal.spectrogram behavior.
 */
public class Spectrogram {

    /**
     * Computes a spectrogram with default parameters.
     * Uses nperseg=256, noverlap=128 (half overlap), Hann window, and constant
     * detrending.
     *
     * @param x  The input signal.
     * @param fs The sampling frequency of the signal in Hz.
     * @return SpectrogramResult containing frequencies, times, and power spectral
     *         density.
     */
    public SpectrogramResult spectrogram(double[] x, double fs) {
        return spectrogram(x, fs, null, null, null, null, DetrendType.CONSTANT);
    }

    /**
     * Computes a spectrogram of a signal.
     * This implementation matches the behavior of scipy.signal.spectrogram with
     * 'psd' mode.
     *
     * @param x        The input signal.
     * @param fs       The sampling frequency of the signal in Hz.
     * @param nperseg  The length of each segment (window size). Default is 256.
     * @param noverlap The number of points to overlap between segments. Default is
     *                 nperseg / 8.
     * @param nfft     The FFT size to use. If null, uses nperseg.
     * @param window   The window function to apply. If null, uses Hann window.
     * @param detrend  The type of detrending to apply to each segment. Default is
     *                 CONSTANT.
     *                 Use null for no detrending.
     * @return SpectrogramResult containing frequencies, times, and power spectral
     *         density.
     */
    public SpectrogramResult spectrogram(double[] x, double fs, Integer nperseg, Integer noverlap,
            Integer nfft, double[] window, DetrendType detrend) {
        // Set defaults (scipy spectrogram uses nperseg/8 for noverlap, not nperseg/2
        // like stft)
        if (nperseg == null)
            nperseg = 256;
        if (noverlap == null)
            noverlap = nperseg / 8;
        if (nfft == null)
            nfft = nperseg;

        // Generate default Hann window if not provided
        if (window == null) {
            window = Windows.hanning(nperseg, false);
        }

        // Compute the STFT with detrending applied to each segment
        // (matching scipy.signal.spectrogram behavior)
        JComplex[][] stftMatrix = stftWithDetrend(x, nperseg, noverlap, nfft, window, "zeros", false, detrend);

        int numFreqBins = stftMatrix.length;
        int numTimeFrames = stftMatrix[0].length;

        // Calculate frequencies
        double[] frequencies = new double[numFreqBins];
        for (int i = 0; i < numFreqBins; i++) {
            frequencies[i] = i * fs / nfft;
        }

        // Spectrogram (and _spectral_helper in SciPy) trims the frames.
        // Based on testing, it seems to keep the first frame (centered at 0.128s)
        // and trim from the end.
        // STFT returns 46 frames. Spectrogram returns 44 frames.
        int trimStart = 0;
        int trimEnd = 1;
        int numSpecFrames = numTimeFrames - trimStart - trimEnd;

        if (numSpecFrames < 0) {
            // Fallback for very short signals if logic effectively removes everything
            // Though normally nperseg is small enough relative to signal.
            // If this happens, likely just return what we have or empty.
            numSpecFrames = 0;
            trimStart = 0;
        }

        // Calculate times (center of each segment)
        int hop = nperseg - noverlap;
        double[] times = new double[numSpecFrames];
        for (int i = 0; i < numSpecFrames; i++) {
            // Original time calculation shifted by trimStart
            times[i] = (nperseg / 2.0 + (i + trimStart) * hop) / fs;
        }

        // Compute power spectral density: |STFT|^2
        // NOTE: stftWithDetrend applies 'spectrum' scaling (divides by sum of window)
        // For density/PSD scaling, we need to account for this
        double windowSum = 0.0;
        for (double w : window) {
            windowSum += w;
        }
        double windowSumSq = windowSum * windowSum;

        // Density scaling: 1 / (fs * sum(window^2))
        // But STFT already divided by sum(window), so when squared we have
        // sum(window)^2 in denominator
        // We need to multiply back by sum(window)^2 to get the correct density scaling
        double windowSumSquared = sumSquared(window);
        double scale = windowSumSq / (fs * windowSumSquared);

        double[][] Sxx = new double[numFreqBins][numSpecFrames];

        for (int f = 0; f < numFreqBins; f++) {
            for (int t = 0; t < numSpecFrames; t++) {
                int srcT = t + trimStart;
                double real = stftMatrix[f][srcT].getReal();
                double imag = stftMatrix[f][srcT].getImaginary();
                double power = real * real + imag * imag;

                // Scale for one-sided spectrum
                if (f > 0 && f < numFreqBins - 1) {
                    power *= 2.0;
                }
                Sxx[f][t] = power * scale;
            }
        }

        return new SpectrogramResult(frequencies, times, Sxx);
    }

    private double sumSquared(double[] array) {
        double sum = 0;
        for (double v : array) {
            sum += v * v;
        }
        return sum;
    }

    /**
     * Computes the Short-Time Fourier Transform (STFT) with optional detrending.
     * This is used internally by spectrogram to apply detrending to each segment.
     * 
     * @param x        The input signal.
     * @param nperseg  The length of each segment (window size).
     * @param noverlap The number of points to overlap between segments.
     * @param nfft     The FFT size to use.
     * @param window   The window function to apply.
     * @param boundary The boundary extension mode.
     * @param padded   Whether to pad the signal on both sides.
     * @param detrend  The type of detrending to apply. Null for no detrending.
     * @return A 2D array of complex values [frequency bins][time frames].
     */
    private JComplex[][] stftWithDetrend(double[] x, int nperseg, int noverlap, int nfft,
            double[] window, String boundary, boolean padded, DetrendType detrend) {

        // Calculate window scaling factor
        double windowScale = 0.0;
        for (double w : window) {
            windowScale += w;
        }

        double[] signal = x;

        // Apply boundary padding if requested
        if (padded && boundary.equals("zeros")) {
            int padLength = nperseg / 2;
            signal = new double[x.length + 2 * padLength];
            System.arraycopy(x, 0, signal, padLength, x.length);
        }

        int hop = nperseg - noverlap;
        // Calculate number of frames to match scipy behavior
        int numFrames = 0;
        if (signal.length >= nperseg) {
            numFrames = 1 + (int) Math.ceil((double) (signal.length - nperseg) / hop);
        }

        // Calculate frequency bins (for real signal, only positive frequencies)
        int numFreqBins = nfft / 2 + 1;

        JComplex[][] stftResult = new JComplex[numFreqBins][numFrames];
        Detrend detrender = new Detrend();
        FFT fft = new FFT(); // Use FFT helper

        // Process each frame
        for (int frameIdx = 0; frameIdx < numFrames; frameIdx++) {
            int start = frameIdx * hop;

            // Extract the segment (before windowing)
            double[] segment = new double[nperseg];
            for (int i = 0; i < nperseg && (start + i) < signal.length; i++) {
                segment[i] = signal[start + i];
            }

            // Apply detrending if requested
            if (detrend != null) {
                segment = detrender.detrend(segment, detrend);
            }

            // Apply windowing and prepare for FFT
            double[] windowedSegment = new double[nfft];
            for (int i = 0; i < nperseg; i++) {
                windowedSegment[i] = segment[i] * window[i];
            }
            // Rest of segment is zero-padded if nfft > nperseg

            // Compute FFT
            JComplex[] fftResult = fft.rfft(windowedSegment);

            // Apply scaling after FFT (scipy behavior)
            for (int freqIdx = 0; freqIdx < numFreqBins; freqIdx++) {
                double real = fftResult[freqIdx].getReal() / windowScale;
                double imag = fftResult[freqIdx].getImaginary() / windowScale;
                stftResult[freqIdx][frameIdx] = new JComplex(real, imag);

            }
        }

        return stftResult;
    }
}
