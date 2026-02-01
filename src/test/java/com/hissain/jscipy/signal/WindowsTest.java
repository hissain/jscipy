package com.hissain.jscipy.signal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WindowsTest {

    @Test
    public void testWindows() {
        int M = 10;
        assertNotNull(Windows.hanning(M));
        assertNotNull(Windows.hamming(M));
        assertNotNull(Windows.blackman(M));
        assertNotNull(Windows.bartlett(M));
        assertNotNull(Windows.triang(M));
        assertNotNull(Windows.flattop(M));
        assertNotNull(Windows.parzen(M));
        assertNotNull(Windows.bohman(M));
        assertNotNull(Windows.kaiser(M, 14.0));

        // Edge case M=1
        assertEquals(1.0, Windows.hanning(1)[0], 1e-6);
        assertEquals(1.0, Windows.hamming(1)[0], 1e-6);
        assertEquals(1.0, Windows.blackman(1)[0], 1e-6);
        assertEquals(1.0, Windows.bartlett(1)[0], 1e-6);
        assertEquals(1.0, Windows.triang(1)[0], 1e-6);
        assertEquals(1.0, Windows.flattop(1)[0], 1e-6);
        assertEquals(1.0, Windows.parzen(1)[0], 1e-6);
        assertEquals(1.0, Windows.bohman(1)[0], 1e-6);
        assertEquals(1.0, Windows.kaiser(1, 1.0)[0], 1e-6);

        // Symmetric checks (implied coverage by calling default matching)
        assertNotNull(Windows.hanning(M, false));
        assertNotNull(Windows.hamming(M, false));
        assertNotNull(Windows.blackman(M, false));
        assertNotNull(Windows.bartlett(M, false));
        assertNotNull(Windows.triang(M, false));
        assertNotNull(Windows.flattop(M, false));
        assertNotNull(Windows.parzen(M, false));
        assertNotNull(Windows.bohman(M, false));
        assertNotNull(Windows.kaiser(M, 14.0, false));
    }

    @Test
    public void testGetWindow() {
        int M = 5;
        assertNotNull(Windows.get_window("boxcar", M));
        assertNotNull(Windows.get_window("hamming", M));
        assertNotNull(Windows.get_window("hanning", M));
        assertNotNull(Windows.get_window("hann", M));
        assertNotNull(Windows.get_window("blackman", M));
        assertNotNull(Windows.get_window("bartlett", M));
        assertNotNull(Windows.get_window("triangle", M));
        assertNotNull(Windows.get_window("triang", M));
        assertNotNull(Windows.get_window("flattop", M));
        assertNotNull(Windows.get_window("parzen", M));
        assertNotNull(Windows.get_window("bohman", M));

        assertThrows(UnsupportedOperationException.class, () -> {
            Windows.get_window("kaiser", M);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Windows.get_window("unknown", M);
        });
    }
}
