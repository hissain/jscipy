package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;
import com.hissain.jscipy.signal.Windows;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;

/**
 * A class for performing Fast Fourier Transforms (FFT) and Short-Time Fourier
 * Transforms (STFT).
 * This class uses the Apache Commons Math library for FFT calculations.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/fft/fft_comparison_1_light.png"
 * alt="FFT Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
 */
public class FFT {

    private final FastFourierTransformer transformer;

    /**
     * Constructs a new FFT object.
     */
    public FFT() {
        this.transformer = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    private JComplex[] dft(double[] input) {
        int n = input.length;
        JComplex[] output = new JComplex[n];
        for (int k = 0; k < n; k++) {
            double sumReal = 0;
            double sumImag = 0;
            for (int t = 0; t < n; t++) {
                double angle = -2 * Math.PI * k * t / n;
                sumReal += input[t] * Math.cos(angle);
                sumImag += input[t] * Math.sin(angle);
            }
            output[k] = new JComplex(sumReal, sumImag);
        }
        return output;
    }

    private JComplex[] idft(JComplex[] input) {
        int n = input.length;
        JComplex[] output = new JComplex[n];
        for (int t = 0; t < n; t++) {
            double sumReal = 0;
            double sumImag = 0;
            for (int k = 0; k < n; k++) {
                double angle = 2 * Math.PI * k * t / n;
                sumReal += input[k].getReal() * Math.cos(angle) - input[k].getImaginary() * Math.sin(angle);
                sumImag += input[k].getReal() * Math.sin(angle) + input[k].getImaginary() * Math.cos(angle);
            }
            output[t] = new JComplex(sumReal / n, sumImag / n);
        }
        return output;
    }

    /**
     * Computes the forward FFT of a real-valued signal.
     * 
     * @param input The input signal.
     * @return The complex-valued FFT of the signal as an array of {@link JComplex}
     *         objects.
     */
    public JComplex[] fft(double[] input) {
        int n = input.length;
        if (Integer.bitCount(n) == 1) { // Power of 2
            Complex[] result = transformer.transform(input, TransformType.FORWARD);
            return toJComplex(result);
        } else {
            return dft(input);
        }
    }

    /**
     * Computes the forward FFT of a complex-valued signal.
     * 
     * @param input The complex-valued input signal.
     * @return The complex-valued FFT of the signal.
     */
    public JComplex[] fft(JComplex[] input) {
        int n = input.length;
        if (Integer.bitCount(n) == 1) { // Power of 2
            Complex[] complexInput = fromJComplex(input);
            Complex[] result = transformer.transform(complexInput, TransformType.FORWARD);
            return toJComplex(result);
        } else {
            // Fallback for non-power-of-2 (using DFT for now, or could compose)
            // Note: The internal dft method strictly takes double[], so we need a complex
            // DFT
            // For now, let's implement a simple complex DFT to support this case
            return complexDft(input);
        }
    }

    private JComplex[] complexDft(JComplex[] input) {
        int n = input.length;
        JComplex[] output = new JComplex[n];
        for (int k = 0; k < n; k++) {
            double sumReal = 0;
            double sumImag = 0;
            for (int t = 0; t < n; t++) {
                double angle = -2 * Math.PI * k * t / n;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                // (a+bi)(c+di) = (ac-bd) + (ad+bc)i
                // input[t] * (cos + i*sin)
                double a = input[t].getReal();
                double b = input[t].getImaginary();
                sumReal += a * cos - b * sin;
                sumImag += a * sin + b * cos;
            }
            output[k] = new JComplex(sumReal, sumImag);
        }
        return output;
    }

    /**
     * Computes the inverse FFT of a complex-valued signal.
     * 
     * @param input The complex-valued input signal.
     * @return The complex-valued inverse FFT of the signal as an array of
     *         {@link JComplex} objects.
     */
    public JComplex[] ifft(JComplex[] input) {
        int n = input.length;
        if (Integer.bitCount(n) == 1) { // Power of 2
            Complex[] complexInput = fromJComplex(input);
            Complex[] result = transformer.transform(complexInput, TransformType.INVERSE);
            return toJComplex(result);
        } else {
            return idft(input);
        }
    }

    /**
     * Computes the 2D FFT of a real-valued 2D array.
     * <p>
     * <img src=
     * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/fft2/fft2_comparison_forward_light.png"
     * alt="FFT2 Comparison" style="width: 600px; max-width: 90%; display: block; margin: 0 auto;">
     * 
     * @param input The 2D input array [rows][cols].
     * @return The 2D complex-valued FFT.
     */
    public JComplex[][] fft2(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        JComplex[][] intermediate = new JComplex[rows][cols];

        // 1. FFT of rows
        for (int i = 0; i < rows; i++) {
            intermediate[i] = fft(input[i]);
        }

        // 2. Transpose
        JComplex[][] transposed = transpose(intermediate);

        // 3. FFT of columns (now rows of transposed)
        JComplex[][] spectrumTransposed = new JComplex[cols][rows];
        for (int i = 0; i < cols; i++) {
            spectrumTransposed[i] = fft(transposed[i]);
        }

        // 4. Transpose back
        return transpose(spectrumTransposed);
    }

    /**
     * Computes the inverse 2D FFT.
     * 
     * @param input The 2D complex spectrum [rows][cols].
     * @return The 2D complex spatial domain signal.
     */
    public JComplex[][] ifft2(JComplex[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        JComplex[][] intermediate = new JComplex[rows][cols];

        // 1. IFFT of rows
        for (int i = 0; i < rows; i++) {
            intermediate[i] = ifft(input[i]);
        }

        // 2. Transpose
        JComplex[][] transposed = transpose(intermediate);

        // 3. IFFT of columns
        JComplex[][] resultTransposed = new JComplex[cols][rows];
        for (int i = 0; i < cols; i++) {
            resultTransposed[i] = ifft(transposed[i]);
        }

        // 4. Transpose back
        return transpose(resultTransposed);
    }

    private JComplex[][] transpose(JComplex[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        JComplex[][] result = new JComplex[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    /**
     * Computes the forward FFT of a real-valued signal and returns the positive
     * frequency components.
     * 
     * @param input The input signal.
     * @return The positive frequency components of the FFT as an array of
     *         {@link JComplex} objects.
     */
    public JComplex[] rfft(double[] input) {
        JComplex[] fftResult = fft(input);
        int n = input.length;
        int resultSize = n / 2 + 1;
        return Arrays.copyOf(fftResult, resultSize);
    }

    /**
     * Computes the inverse FFT of a real-valued signal.
     * 
     * @param input The complex-valued input signal.
     * @param n     The length of the original signal.
     * @return The real-valued inverse FFT of the signal.
     */
    public double[] irfft(JComplex[] input, int n) {
        // Reconstruct the full complex spectrum from the RFFT output
        JComplex[] fullSpectrum = new JComplex[n];
        fullSpectrum[0] = input[0]; // DC component

        for (int i = 1; i < input.length - 1; i++) {
            fullSpectrum[i] = input[i];
            fullSpectrum[n - i] = input[i].conjugate();
        }

        if (n % 2 == 0) { // Even length input
            fullSpectrum[n / 2] = input[n / 2];
        }

        JComplex[] ifftResult = ifft(fullSpectrum);
        double[] realResult = new double[n];
        for (int i = 0; i < n; i++) {
            realResult[i] = ifftResult[i].getReal();
        }
        return realResult;
    }

    /**
     * Computes the Short-Time Fourier Transform (STFT) with default parameters.
     * Uses nperseg=256, noverlap=128, Hann window, and zero-padding.
     * 
     * @param x The input signal.
     * @return A 2D array of complex values [frequency bins][time frames]
     *         representing the STFT.
     */
    public JComplex[][] stft(double[] x) {
        return stft(x, -1, -1, -1, null, "zeros", true);
    }

    /**
     * Computes the Inverse Short-Time Fourier Transform (ISTFT) with default
     * parameters.
     * Uses nperseg=256, noverlap=128, Hann window, and assumes zero-padding was
     * used.
     * 
     * @param stftMatrix The STFT matrix [frequency bins][time frames].
     * @return The reconstructed time-domain signal.
     */
    public double[] istft(JComplex[][] stftMatrix) {
        return istft(stftMatrix, -1, -1, -1, null, "zeros", -1);
    }

    /**
     * Computes the Short-Time Fourier Transform (STFT) of a signal.
     * This implementation matches the behavior of scipy.signal.stft with default
     * parameters.
     * 
     * @param x        The input signal.
     * @param nperseg  The length of each segment (window size). Default is 256.
     *                 Pass -1 to use default.
     * @param noverlap The number of points to overlap between segments. Default is
     *                 nperseg // 2. Pass -1 to use default.
     * @param nfft     The FFT size to use. If -1, uses nperseg. Must be >=
     *                 nperseg.
     * @param window   The window function to apply. If null, uses Hann window.
     * @param boundary The boundary extension mode. "zeros" pads with zeros, null
     *                 means no padding.
     * @param padded   Whether to pad the signal on both sides. Default is true.
     * @return A 2D array of complex values [frequency bins][time frames]
     *         representing the STFT.
     */
    public JComplex[][] stft(double[] x, int nperseg, int noverlap, int nfft,
            double[] window, String boundary, boolean padded) {
        // Set defaults
        if (nperseg <= 0)
            nperseg = 256;
        if (noverlap == -1)
            noverlap = nperseg / 2;
        if (nfft <= 0)
            nfft = nperseg;
        if (boundary == null)
            boundary = "zeros";

        // Generate default Hann window if not provided
        if (window == null) {
            window = Windows.hanning(nperseg, false); // hannWindow(nperseg);
        }

        // Calculate window scaling factor to match scipy behavior
        // scipy uses: scale = 1.0 / np.sum(window) for 'spectrum' scaling (default)
        double windowScale = 0.0;
        for (double w : window) {
            windowScale += w;
        }

        // Validate parameters
        if (nfft < nperseg) {
            throw new IllegalArgumentException("nfft must be greater than or equal to nperseg");
        }
        if (noverlap >= nperseg) {
            throw new IllegalArgumentException("noverlap must be less than nperseg");
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
        // scipy includes partial frames at the end
        int numFrames = 0;
        if (signal.length >= nperseg) {
            // This matches scipy's calculation
            numFrames = 1 + (int) Math.ceil((double) (signal.length - nperseg) / hop);
        }

        // Calculate frequency bins (for real signal, only positive frequencies)
        int numFreqBins = nfft / 2 + 1;

        JComplex[][] stftResult = new JComplex[numFreqBins][numFrames];

        // Process each frame
        for (int frameIdx = 0; frameIdx < numFrames; frameIdx++) {
            int start = frameIdx * hop;

            // Extract and window the segment
            double[] segment = new double[nfft];
            for (int i = 0; i < nperseg && (start + i) < signal.length; i++) {
                segment[i] = signal[start + i] * window[i];
            }
            // Rest of segment is zero-padded if nfft > nperseg

            // Compute FFT
            JComplex[] fftResult = rfft(segment);

            // Apply scaling after FFT (scipy behavior)
            for (int freqIdx = 0; freqIdx < numFreqBins; freqIdx++) {
                double real = fftResult[freqIdx].getReal() / windowScale;
                double imag = fftResult[freqIdx].getImaginary() / windowScale;
                stftResult[freqIdx][frameIdx] = new JComplex(real, imag);
            }
        }

        return stftResult;
    }

    /**
     * Computes the Inverse Short-Time Fourier Transform (ISTFT).
     * This implementation matches the behavior of scipy.signal.istft with default
     * parameters.
     * 
     * @param stftMatrix  The STFT matrix [frequency bins][time frames].
     * @param nperseg     The length of each segment (window size). Must match the
     *                    STFT parameters.
     * @param noverlap    The number of points to overlap between segments. Must
     *                    match the STFT parameters.
     * @param nfft        The FFT size used. If -1, inferred from stftMatrix
     *                    dimensions.
     * @param window      The window function used in STFT. If null, uses Hann
     *                    window.
     * @param boundary    The boundary extension mode used in STFT. "zeros" means
     *                    padding was used.
     * @param inputLength The expected output length. If -1, inferred from
     *                    stftMatrix.
     * @return The reconstructed time-domain signal.
     */
    public double[] istft(JComplex[][] stftMatrix, int nperseg, int noverlap, int nfft,
            double[] window, String boundary, int inputLength) {
        // Set defaults
        if (nperseg <= 0)
            nperseg = 256;
        if (noverlap == -1)
            noverlap = nperseg / 2;

        int numFreqBins = stftMatrix.length;
        int numFrames = stftMatrix[0].length;

        // Infer nfft from frequency bins
        if (nfft <= 0) {
            nfft = (numFreqBins - 1) * 2;
        }
        if (boundary == null)
            boundary = "zeros";

        // Generate default Hann window if not provided
        if (window == null) {
            window = Windows.hanning(nperseg, false); // hannWindow(nperseg);
        }

        // Calculate window scaling factor to match stft
        // Use sum of window values for 'spectrum' scaling
        double windowScale = 0.0;
        for (double w : window) {
            windowScale += w;
        }

        int hop = nperseg - noverlap;

        // Calculate output length with padding
        int outputLengthWithPad = (numFrames - 1) * hop + nperseg;

        double[] output = new double[outputLengthWithPad];
        double[] windowSum = new double[outputLengthWithPad];

        // Process each frame
        for (int frameIdx = 0; frameIdx < numFrames; frameIdx++) {
            int start = frameIdx * hop;

            // Extract frequency frame
            JComplex[] frame = new JComplex[numFreqBins];
            for (int i = 0; i < numFreqBins; i++) {
                frame[i] = stftMatrix[i][frameIdx];
            }

            // Compute inverse FFT
            double[] timeSegment = irfft(frame, nfft);

            // Apply window and overlap-add (no additional scaling needed here)
            for (int i = 0; i < nperseg && (start + i) < outputLengthWithPad; i++) {
                output[start + i] += timeSegment[i] * window[i];
                windowSum[start + i] += window[i] * window[i];
            }
        }

        // Normalize by window sum
        for (int i = 0; i < outputLengthWithPad; i++) {
            if (windowSum[i] > 1e-10) {
                // The factor of windowScale accounts for the scaling applied in STFT
                output[i] = output[i] * windowScale / windowSum[i];
            }
        }

        // Remove padding if it was added
        if (boundary.equals("zeros")) {
            int padLength = nperseg / 2;
            if (outputLengthWithPad > 2 * padLength) {
                double[] unpaddedOutput = new double[outputLengthWithPad - 2 * padLength];
                System.arraycopy(output, padLength, unpaddedOutput, 0, unpaddedOutput.length);
                return unpaddedOutput;
            }
        }

        return output;
    }

    private JComplex[] toJComplex(Complex[] input) {
        JComplex[] output = new JComplex[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = new JComplex(input[i].getReal(), input[i].getImaginary());
        }
        return output;
    }

    private Complex[] fromJComplex(JComplex[] input) {
        Complex[] output = new Complex[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = new Complex(input[i].getReal(), input[i].getImaginary());
        }
        return output;
    }
}
