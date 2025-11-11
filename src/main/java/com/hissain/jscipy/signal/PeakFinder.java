package com.hissain.jscipy.signal;

import java.util.*;
import java.util.stream.IntStream;
import com.hissain.jscipy.signal.api.IPeakFinder;

public class PeakFinder implements IPeakFinder {

    public class PeakResult {
        public final int[] peaks;
        public final Map<String, double[]> properties;

        public PeakResult(int[] peaks, Map<String, double[]> properties) {
            this.peaks = peaks;
            this.properties = properties;
        }
    }

    public class PeakParams {
        public Double height = null;
        public Double prominence = null;
        public Integer distance = null;
    }

    public PeakResult findPeaks(double[] x, PeakParams params) {
        if (x == null || x.length == 0) {
            return new PeakResult(new int[0], new HashMap<>());
        }
        int[] peaks = findRawPeaks(x);

        if (params != null) {
            if (params.height != null) {
                peaks = filterByHeight(x, peaks, params.height);
            }
            if (params.distance != null) {
                peaks = filterByDistance(x, peaks, params.distance);
            }
            if (params.prominence != null) {
                peaks = filterByProminence(x, peaks, params.prominence);
            }
        }

        return new PeakResult(peaks, new HashMap<>());
    }

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

    private int[] filterByHeight(double[] x, int[] peaks, double height) {
        List<Integer> filteredPeaks = new ArrayList<>();
        for (int peak : peaks) {
            if (x[peak] >= height) {
                filteredPeaks.add(peak);
            }
        }
        return filteredPeaks.stream().mapToInt(Integer::intValue).toArray();
    }

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

    private class PeakWithHeight {
        int position;
        double height;

        PeakWithHeight(int position, double height) {
            this.position = position;
            this.height = height;
        }
    }
}
