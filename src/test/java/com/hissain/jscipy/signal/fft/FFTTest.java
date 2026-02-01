package com.hissain.jscipy.signal.fft;

import com.hissain.jscipy.signal.JComplex;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FFTTest {

    @Test
    public void testFFT_double() {
        FFT fftObject = new FFT();
        double[] input = { 1, 2, 3, 4 }; // Power of 2
        JComplex[] output = fftObject.fft(input);
        assertNotNull(output);
        assertEquals(4, output.length);

        double[] inputNon2 = { 1, 2, 3 }; // Non power of 2
        JComplex[] outputNon2 = fftObject.fft(inputNon2);
        assertNotNull(outputNon2);
        assertEquals(3, outputNon2.length);
    }

    @Test
    public void testFFT_complex() {
        FFT fftObject = new FFT();
        JComplex[] input = { new JComplex(1, 0), new JComplex(2, 0) };
        JComplex[] output = fftObject.fft(input);
        assertNotNull(output);

        // Non power of 2
        JComplex[] inputNon2 = { new JComplex(1, 0), new JComplex(2, 0), new JComplex(3, 0) };
        JComplex[] outputNon2 = fftObject.fft(inputNon2);
        assertNotNull(outputNon2);
    }

    @Test
    public void testIFFT() {
        FFT fftObject = new FFT();
        JComplex[] input = { new JComplex(1, 0), new JComplex(2, 0) };
        JComplex[] output = fftObject.ifft(input);
        assertNotNull(output);

        // Non power of 2
        JComplex[] inputNon2 = { new JComplex(1, 0), new JComplex(2, 0), new JComplex(3, 0) };
        JComplex[] outputNon2 = fftObject.ifft(inputNon2);
        assertNotNull(outputNon2);
    }

    @Test
    public void testFFT2() {
        FFT fftObject = new FFT();
        double[][] input = { { 1, 2 }, { 3, 4 } };
        JComplex[][] output = fftObject.fft2(input);
        assertNotNull(output);
        assertEquals(2, output.length);
        assertEquals(2, output[0].length);

        JComplex[][] ifftOutput = fftObject.ifft2(output);
        assertNotNull(ifftOutput);
    }

    @Test
    public void testRFFT() {
        FFT fftObject = new FFT();
        double[] input = { 1, 2, 3, 4 };
        JComplex[] output = fftObject.rfft(input);
        assertNotNull(output);
        // rfft returns n/2 + 1 elements
        assertEquals(3, output.length);

        double[] recon = fftObject.irfft(output, 4);
        assertNotNull(recon);
        assertEquals(4, recon.length);
    }

    @Test
    public void testSTFT() {
        FFT fftObject = new FFT();
        double[] input = new double[100];
        for (int i = 0; i < 100; i++)
            input[i] = Math.sin(0.1 * i);

        // Default STFT
        JComplex[][] stft = fftObject.stft(input);
        assertNotNull(stft);

        // Custom STFT
        JComplex[][] stftCustom = fftObject.stft(input, 32, 16, 64, null, "zeros", true);
        assertNotNull(stftCustom);

        // ISTFT
        double[] output = fftObject.istft(stft);
        assertNotNull(output);
    }
}