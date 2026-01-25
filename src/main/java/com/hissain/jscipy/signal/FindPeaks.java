package com.hissain.jscipy.signal;

import java.util.*;

/**
 * A utility class for finding peaks in a signal.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/peak_properties/test1_analysis_light.png"
 * alt="FindPeaks Comparison" style="width: 600px; max-width: 90%; display:
 * block; margin: 0 auto;">
 */
public class FindPeaks {

    /**
     * Represents the result of a peak finding operation.
     */
    public static class PeakResult {
        /** The indices of the detected peaks. */
        public final int[] peaks;
        /** Additional properties of the peaks, such as prominence or height. */
        public final Map<String, double[]> properties;

        public PeakResult(int[] peaks, Map<String, double[]> properties) {
            this.peaks = peaks;
            this.properties = properties;
        }
    }

    /**
     * Result structure for peak prominences calculation.
     */
    public static class ProminenceResult {
        /** The calculated prominences for each peak. */
        public final double[] prominences;
        /** The left bases indices for each peak. */
        public final int[] leftBases;
        /** The right bases indices for each peak. */
        public final int[] rightBases;

        public ProminenceResult(double[] prominences, int[] leftBases, int[] rightBases) {
            this.prominences = prominences;
            this.leftBases = leftBases;
            this.rightBases = rightBases;
        }
    }

    /**
     * Result structure for peak width calculation.
     */
    public static class WidthResult {
        /** The widths of each peak. */
        public final double[] widths;
        /** The height at which the width was evaluated. */
        public final double[] widthHeights;
        /** The left intersection points (interpolated indices). */
        public final double[] leftIps;
        /** The right intersection points (interpolated indices). */
        public final double[] rightIps;

        public WidthResult(double[] widths, double[] widthHeights, double[] leftIps, double[] rightIps) {
            this.widths = widths;
            this.widthHeights = widthHeights;
            this.leftIps = leftIps;
            this.rightIps = rightIps;
        }
    }

    /**
     * Parameters for customizing the peak finding algorithm.
     */
    public static class PeakParams {
        /** Required height of peaks. Use {@code Double.NaN} for no filter (default). */
        public double height = Double.NaN;
        /**
         * Required prominence of peaks. Use {@code Double.NaN} for no filter (default).
         */
        public double prominence = Double.NaN;
        /**
         * Required minimal distance between peaks. Use -1 for default (no distance
         * filter).
         */
        public int distance = -1;
    }

    /**
     * Finds peaks in a given signal based on specified parameters.
     * 
     * @param x      The input signal (array of double values).
     * @param params Parameters for peak detection, can be null for default
     *               behavior.
     * @return A PeakResult object containing the peak indices and properties.
     */
    public PeakResult findPeaks(double[] x, PeakParams params) {
        if (x == null || x.length == 0) {
            return new PeakResult(new int[0], new HashMap<>());
        }
        int[] peaks = findRawPeaks(x);

        if (params != null) {
            if (!Double.isNaN(params.height)) {
                peaks = filterByHeight(x, peaks, params.height);
            }
            if (params.distance > 0) {
                peaks = filterByDistance(x, peaks, params.distance);
            }
            if (!Double.isNaN(params.prominence)) {
                peaks = filterByProminence(x, peaks, params.prominence);
            }
        }

        return new PeakResult(peaks, new HashMap<>());
    }

    /**
     * Finds raw peaks in a signal.
     */
    private int[] findRawPeaks(double[] signal) {
        if (signal == null || signal.length < 3) {
            return new int[0];
        }
        List<Integer> peaks = new ArrayList<>();

        for (int i = 1; i < signal.length - 1; i++) {
            if (signal[i] > signal[i - 1] && signal[i] > signal[i + 1]) {
                peaks.add(i);
            }
        }

        return peaks.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Filters peaks based on height.
     */
    private int[] filterByHeight(double[] x, int[] peaks, double height) {
        List<Integer> filteredPeaks = new ArrayList<>();
        for (int peak : peaks) {
            if (x[peak] >= height) {
                filteredPeaks.add(peak);
            }
        }
        return filteredPeaks.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Filters peaks based on prominence.
     */
    private int[] filterByProminence(double[] x, int[] peaks, double minProminence) {
        // Calculate prominences using the newly refactored public method
        ProminenceResult result = peakProminences(x, peaks, -1);
        double[] prominences = result.prominences;

        List<Integer> filteredPeaks = new ArrayList<>();
        for (int i = 0; i < peaks.length; i++) {
            if (prominences[i] >= minProminence) {
                filteredPeaks.add(peaks[i]);
            }
        }
        return filteredPeaks.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Calculate the prominence of each peak in a signal.
     * <p>
     * The prominence of a peak measures how much a peak stands out from the
     * surrounding baseline of the signal and is defined as the vertical distance
     * between the peak and its lowest contour line.
     *
     * @param x     The signal data.
     * @param peaks Indices of peaks in x.
     * @param wlen  A window length in samples that optionally limits the evaluated
     *              area for each peak to a subset of x. The subset is centered at
     *              the peak. Use -1 for no window (search entire signal).
     * @return A ProminenceResult containing prominences and bases.
     */
    public static ProminenceResult peakProminences(double[] x, int[] peaks, int wlen) {
        int nPeaks = peaks.length;
        double[] prominences = new double[nPeaks];
        int[] leftBases = new int[nPeaks];
        int[] rightBases = new int[nPeaks];

        for (int i = 0; i < nPeaks; i++) {
            int peak = peaks[i];
            double peakHeight = x[peak];

            // Define search range based on wlen
            int iMin = 0;
            int iMax = x.length - 1;
            if (wlen > 1) {
                int halfWin = wlen / 2;
                iMin = Math.max(0, peak - halfWin);
                iMax = Math.min(x.length - 1, peak + halfWin);
            }

            // Find left base
            double leftMin = peakHeight; // Start with peak height
            int leftBase = peak;

            // Search left
            for (int j = peak - 1; j >= iMin; j--) {
                if (x[j] > peakHeight) {
                    break;
                }
                if (x[j] <= leftMin) {
                    leftMin = x[j];
                    leftBase = j;
                }
            }

            // Find right base
            double rightMin = peakHeight;
            int rightBase = peak;

            for (int j = peak + 1; j <= iMax; j++) {
                if (x[j] > peakHeight) {
                    break;
                }
                if (x[j] <= rightMin) {
                    rightMin = x[j];
                    rightBase = j;
                }
            }

            // Calculate prominence
            double refLevel = Math.max(leftMin, rightMin);

            prominences[i] = peakHeight - refLevel;
            leftBases[i] = leftBase;
            rightBases[i] = rightBase;
        }

        return new ProminenceResult(prominences, leftBases, rightBases);
    }

    /**
     * Calculate the width of each peak.
     * <p>
     * Returns the width of each peak in samples at a relative height to the peak's
     * prominence.
     *
     * @param x           The signal data.
     * @param peaks       Indices of peaks in x.
     * @param relHeight   The relative height at which to measure width (0.0 - 1.0).
     *                    Default 0.5.
     * @param prominences The prominences of the peaks (optional, if null calculated
     *                    internally).
     * @param leftBases   Left bases of peaks (optional).
     * @param rightBases  Right bases of peaks (optional).
     * @param wlen        Window length used for prominence calculation (if needed).
     * @return A WidthResult containing widths and intersection points.
     */
    public static WidthResult peakWidths(double[] x, int[] peaks, double relHeight,
            double[] prominences, int[] leftBases, int[] rightBases, int wlen) {

        int nPeaks = peaks.length;
        if (prominences == null || leftBases == null || rightBases == null) {
            ProminenceResult pr = peakProminences(x, peaks, wlen);
            prominences = pr.prominences;
            leftBases = pr.leftBases;
            rightBases = pr.rightBases;
        }

        double[] widths = new double[nPeaks];
        double[] widthHeights = new double[nPeaks];
        double[] leftIps = new double[nPeaks];
        double[] rightIps = new double[nPeaks];

        for (int i = 0; i < nPeaks; i++) {
            int peak = peaks[i];
            double p = prominences[i];
            double height = x[peak] - p * relHeight;
            widthHeights[i] = height;

            int iMin = leftBases[i];
            int iMax = rightBases[i];

            // Search Left
            int j = peak;
            while (j > iMin && x[j] > height) {
                j--;
            }
            leftIps[i] = interpolate(x, j, j + 1, height);

            // Search Right
            j = peak;
            while (j < iMax && x[j] > height) {
                j++;
            }
            rightIps[i] = interpolate(x, j, j - 1, height);

            widths[i] = rightIps[i] - leftIps[i];
        }

        return new WidthResult(widths, widthHeights, leftIps, rightIps);
    }

    /**
     * Linear interpolation to find the index where the signal crosses yVal.
     * Assumes yVal is between y[i1] and y[i2].
     */
    private static double interpolate(double[] x, int i1, int i2, double yVal) {
        double y1 = x[i1];
        double y2 = x[i2];
        if (y1 == y2) {
            return i1;
        }
        double frac = (yVal - y1) / (y2 - y1);
        return i1 + frac * (i2 - i1);
    }

    /**
     * Filters peaks based on minimum distance.
     */
    private int[] filterByDistance(double[] x, int[] peaks, int distance) {
        List<PeakWithHeight> peaksWithHeights = new ArrayList<>();
        for (int peak : peaks) {
            peaksWithHeights.add(new PeakWithHeight(peak, x[peak]));
        }

        peaksWithHeights.sort((a, b) -> Double.compare(b.height, a.height));

        List<Integer> finalPeaks = new ArrayList<>();
        boolean[] removed = new boolean[x.length];

        for (PeakWithHeight pwh : peaksWithHeights) {
            if (!removed[pwh.position]) {
                finalPeaks.add(pwh.position);
                for (int i = pwh.position - distance + 1; i < pwh.position + distance; i++) {
                    if (i >= 0 && i < x.length) {
                        removed[i] = true;
                    }
                }
            }
        }

        finalPeaks.sort(Comparator.naturalOrder());
        return finalPeaks.stream().mapToInt(Integer::intValue).toArray();
    }

    private static class PeakWithHeight {
        int position;
        double height;

        PeakWithHeight(int position, double height) {
            this.position = position;
            this.height = height;
        }
    }
}
