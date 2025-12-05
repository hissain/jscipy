package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import com.hissain.jscipy.signal.DetrendType;
import com.hissain.jscipy.signal.ConvolutionMode;

public class SignalFacadeTest {

    @Test
    void testMedfilt() {
        double[] input = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] expected = {1.0, 2.0, 3.0, 4.0, 4.0}; // Assuming kernel 3 and zero padding logic: [0,1,2]->1, [1,2,3]->2, [2,3,4]->3, [3,4,5]->4, [4,5,0]->4 (sorted 0,4,5 -> 4)
        
        double[] actual = Signal.medfilt(input, 3);
        assertArrayEquals(expected, actual, 1e-6);
    }

    @Test
    void testConvolve() {
        double[] signal = {1.0, 2.0, 3.0};
        double[] window = {0.5, 0.5}; // Sum = 1.0
        // Window center index for length 2 is 2/2 = 1.
        // i=0: center at 0. indices: 0-1=-1 (out), 0-1+1=0. val = s[0]*w[1] = 1*0.5 = 0.5.
        // i=1: center at 1. indices: 1-1=0, 1-1+1=1. val = s[0]*w[0] + s[1]*w[1] = 1*0.5 + 2*0.5 = 1.5.
        // i=2: center at 2. indices: 2-1=1, 2-1+1=2. val = s[1]*w[0] + s[2]*w[1] = 2*0.5 + 3*0.5 = 2.5.
        
        double[] expected = {0.5, 1.5, 2.5};
        double[] actual = Signal.convolve(signal, window, ConvolutionMode.SAME);
        
        assertArrayEquals(expected, actual, 1e-6);
    }

    @Test
    void testDetrend() {
        double[] signal = {1.0, 2.0, 3.0};
        double[] expected = {0.0, 0.0, 0.0};
        double[] actual = Signal.detrend(signal, DetrendType.LINEAR);
        
        assertArrayEquals(expected, actual, 1e-6);
    }
}
