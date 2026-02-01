package com.hissain.jscipy.math;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class RK4SolverMainTest {

    @Test
    public void testMain() {
        // Capture stdout to verify output and suppress console noise
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            RK4Solver.main(new String[] {});
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();
        // Verify some output presence
        assertTrue(output.contains("Example 1"));
        assertTrue(output.contains("Example 4"));
    }
}
