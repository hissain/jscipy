# jSciPy API Usage Examples

This document provides examples of how to use the public API of the jSciPy library.

## 1. FindPeaks

The `IFindPeaks` interface and its implementation `FindPeaks` allow you to detect peaks in a 1D signal.

```java
import com.hissain.jscipy.signal.FindPeaks;
import com.hissain.jscipy.signal.api.IFindPeaks;

public class FindPeaksExample {
    public static void main(String[] args) {
        // Sample signal data
        double[] signal = {0.0, 1.0, 0.5, 2.0, 1.5, 3.0, 2.5, 0.0};

        // Create an instance of the FindPeaks
        IFindPeaks findPeaks = new FindPeaks();

        // Define peak parameters (optional)
        FindPeaks.PeakParams params = new FindPeaks.PeakParams();
        params.distance = 1; // Minimum distance between peaks
        params.height = 1.0; // Minimum height of peaks
        params.prominence = 0.5; // Minimum prominence of peaks

        // Find peaks
        FindPeaks.PeakResult result = findPeaks.findPeaks(signal, params);

        System.out.println("Detected Peaks:");
        for (int peakIndex : result.peaks) {
            System.out.println("  Index: " + peakIndex + ", Value: " + signal[peakIndex]);
        }

        // You can also find peaks without specific parameters
        FindPeaks.PeakResult defaultResult = findPeaks.findPeaks(signal, null);
        System.out.println("\nDetected Peaks (default parameters):");
        for (int peakIndex : defaultResult.peaks) {
            System.out.println("  Index: " + peakIndex + ", Value: " + signal[peakIndex]);
        }
    }
}
```

## 2. ButterworthFilter

The `IButterworthFilter` interface and its implementation `ButterworthFilter` allow you to apply digital Butterworth filters to signals.

```java
import com.hissain.jscipy.signal.ButterworthFilter;
import com.hissain.jscipy.signal.api.IButterworthFilter;

public class ButterworthFilterExample {
    public static void main(String[] args) {
        // Sample signal data (e.g., a noisy signal)
        double[] signal = new double[100];
        double sampleRate = 1000.0; // Hz
        double cutoffFrequency = 50.0; // Hz
        int order = 4; // Filter order

        // Generate a sample signal (e.g., sine wave + noise)
        for (int i = 0; i < signal.length; i++) {
            signal[i] = Math.sin(2 * Math.PI * 10 * (i / sampleRate)) + // 10 Hz sine wave
                        0.5 * Math.sin(2 * Math.PI * 150 * (i / sampleRate)) + // 150 Hz noise
                        (Math.random() - 0.5) * 0.2; // Random noise
        }

        // Create an instance of the ButterworthFilter
        IButterworthFilter filter = new ButterworthFilter();

        // Apply a zero-phase (forward-backward) filter
        double[] filteredSignalFiltfilt = filter.filtfilt(signal, sampleRate, cutoffFrequency, order);
        System.out.println("Filtered signal (filtfilt) first 10 points:");
        for (int i = 0; i < 10; i++) {
            System.out.printf("%.4f ", filteredSignalFiltfilt[i]);
        }
        System.out.println();

        // Apply a forward-only filter
        double[] filteredSignal = filter.filter(signal, sampleRate, cutoffFrequency, order);
        System.out.println("Filtered signal (forward only) first 10 points:");
        for (int i = 0; i < 10; i++) {
            System.out.printf("%.4f ", filteredSignal[i]);
        }
        System.out.println();
    }
}
```

## 3. RK4Solver

The `IRK4Solver` interface and its implementation `RK4Solver` allow you to solve ordinary differential equations (ODEs) using the Runge-Kutta 4th Order method.

```java
import com.hissain.jscipy.signal.RK4Solver;
import com.hissain.jscipy.signal.api.IRK4Solver;

public class RK4SolverExample {
    public static void main(String[] args) {
        // Create an instance of the RK4Solver
        IRK4Solver solver = new RK4Solver();

        // Define the differential equation: dy/dt = -2*t*y (analytical solution: y = e^(-t^2))
        IRK4Solver.DifferentialEquation equation = (t, y) -> -2 * t * y;

        // Initial conditions
        double y0 = 1.0; // y(0) = 1
        double t0 = 0.0;
        double tf = 2.0;
        double h = 0.1; // Step size

        // Solve with fixed step size
        IRK4Solver.Solution solutionFixedStep = solver.solve(equation, y0, t0, tf, h);

        System.out.println("RK4 Solution (Fixed Step):");
        System.out.println("t\t\ty");
        System.out.println("------------------------");
        for (int i = 0; i < solutionFixedStep.t.length; i++) {
            System.out.printf("%.4f\t\t%.6f%n", solutionFixedStep.t[i], solutionFixedStep.y[i]);
        }

        // Solve with specified time points
        double[] tSpan = {0.0, 0.2, 0.5, 1.0, 1.5, 2.0};
        IRK4Solver.Solution solutionTimePoints = solver.solve(equation, y0, tSpan);

        System.out.println("\nRK4 Solution (Specified Time Points):");
        System.out.println("t\t\ty");
        System.out.println("------------------------");
        for (int i = 0; i < solutionTimePoints.t.length; i++) {
            System.out.printf("%.4f\t\t%.6f%n", solutionTimePoints.t[i], solutionTimePoints.y[i]);
        }
    }
}
