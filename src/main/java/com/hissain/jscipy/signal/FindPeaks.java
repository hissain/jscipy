package com.hissain.jscipy.signal;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A utility class for finding peaks in a signal.
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

        /**
         * Constructs a new PeakResult.
         * 
         * @param peaks      The indices of the detected peaks.
         * @param properties Additional properties of the peaks.
         */
        public PeakResult(int[] peaks, Map<String, double[]> properties) {
            this.peaks = peaks;
            this.properties = properties;
        }
    }

    /**
     * Parameters for customizing the peak finding algorithm.
     */
    public static class PeakParams {
        /** Required height of peaks. */
        public Double height = null;
        /** Required prominence of peaks. */
        public Double prominence = null;
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
            if (params.height != null) {
                peaks = filterByHeight(x, peaks, params.height);
            }
            if (params.distance > 0) {
                peaks = filterByDistance(x, peaks, params.distance);
            }
            if (params.prominence != null) {
                peaks = filterByProminence(x, peaks, params.prominence);
            }
        }

        return new PeakResult(peaks, new HashMap<>());
    }

    /**
     * Finds raw peaks in a signal without applying any filtering.
     * A peak is defined as a point greater than its immediate neighbors.
     * 
     * @param signal The input signal.
     * @return An array of indices where peaks are found.
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

        int[] result = new int[peaks.size()];
        for (int i = 0; i < peaks.size(); i++) {
            result[i] = peaks.get(i);
        }

        return result;
    }

    /**
     * Filters peaks based on a minimum height requirement.
     * 
     * @param x      The input signal.
     * @param peaks  The array of peak indices to filter.
     * @param height The minimum height for a peak to be considered.
     * @return An array of filtered peak indices.
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
     * Filters peaks based on a minimum prominence requirement.
     * 
     * @param x          The input signal.
     * @param peaks      The array of peak indices to filter.
     * @param prominence The minimum prominence for a peak to be considered.
     * @return An array of filtered peak indices.
     */
    private int[] filterByProminence(double[] x, int[] peaks, double prominence) {
        double[] prominences = calculateProminences(x, peaks);
        List<Integer> filteredPeaks = new ArrayList<>();
        for (int i = 0; i < peaks.length; i++) {
            if (prominences[i] >= prominence) {
                filteredPeaks.add(peaks[i]);
            }
        }
        return filteredPeaks.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Calculates the prominence of each peak.
     * Prominence of a peak is the vertical distance between the peak and its lowest
     * contour line.
     * 
     * @param x     The input signal.
     * @param peaks The array of peak indices.
     * @return An array of prominence values corresponding to each peak.
     */
    private double[] calculateProminences(double[] x, int[] peaks) {
        double[] prominences = new double[peaks.length];
        for (int i = 0; i < peaks.length; i++) {
            int peak = peaks[i];
            double peakHeight = x[peak];
            double leftMin = peakHeight;
            for (int j = peak - 1; j >= 0; j--) {
                if (x[j] > peakHeight) {
                    break;
                }
                if (x[j] < leftMin) {
                    leftMin = x[j];
                }
            }
            double rightMin = peakHeight;
            for (int j = peak + 1; j < x.length; j++) {
                if (x[j] > peakHeight) {
                    break;
                }
                if (x[j] < rightMin) {
                    rightMin = x[j];
                }
            }
            prominences[i] = peakHeight - Math.max(leftMin, rightMin);
        }
        return prominences;
    }

    /**
     * Filters peaks based on a minimum distance requirement between them.
     * If two peaks are closer than the specified distance, the smaller one is
     * removed.
     * 
     * @param x        The input signal.
     * @param peaks    The array of peak indices to filter.
     * @param distance The minimum required distance between peaks.
     * @return An array of filtered peak indices.
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

    /**
     * Helper class to store a peak's position and its corresponding height.
     */
    private static class PeakWithHeight {
        /** The index (position) of the peak in the signal. */
        int position;
        /** The height (value) of the signal at the peak's position. */
        double height;

        /**
         * Constructs a new PeakWithHeight object.
         * 
         * @param position The index of the peak.
         * @param height   The height of the peak.
         */
        PeakWithHeight(int position, double height) {
            this.position = position;
            this.height = height;
        }
    }
}
