package com.hissain.jscipy.signal.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FilterDesignTest {

    @Test
    public void testChebyshev1() {
        Chebyshev1Design design = new Chebyshev1Design();

        // LowPass
        design.lowPass(4, 1.0, 1000, 100);
        assertTrue(design.getNumBiquads() > 0);
        assertNotNull(design.getBiquad(0));
        assertEquals(0.0, design.filter(0.0), 1e-6);

        // HighPass
        design.highPass(4, 1.0, 1000, 100);
        assertTrue(design.getNumBiquads() > 0);

        // BandPass
        design.bandPass(2, 1.0, 1000, 200, 50);
        assertTrue(design.getNumBiquads() > 0);

        // BandStop
        design.bandStop(2, 1.0, 1000, 200, 50);
        assertTrue(design.getNumBiquads() > 0);
    }

    @Test
    public void testButterworth() {
        ButterworthDesign design = new ButterworthDesign();

        // LowPass
        design.lowPass(4, 1000, 100);
        assertTrue(design.getNumBiquads() > 0);
        assertNotNull(design.getBiquad(0));

        // HighPass
        design.highPass(4, 1000, 100);
        assertTrue(design.getNumBiquads() > 0);

        // BandPass
        design.bandPass(2, 1000, 200, 50);
        assertTrue(design.getNumBiquads() > 0);

        // BandStop
        design.bandStop(2, 1000, 200, 50);
        assertTrue(design.getNumBiquads() > 0);
    }
}
