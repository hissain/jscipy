#!/bin/bash

# Function to update tests using absolute paths
fix_test() {
    file=$1
    subdir=$2
    # Replace "/datasets/" with "/datasets/subdir/"
    sed -i '' "s|\"/datasets/\"|\"/datasets/$subdir/\"|g" "$file"
}

TEST_DIR="src/test/java/com/hissain/jscipy"

fix_test "$TEST_DIR/math/RK4SolverTest.java" "rk4"
fix_test "$TEST_DIR/math/ResampleTest.java" "resample"
fix_test "$TEST_DIR/signal/Chebyshev2FilterTest.java" "chebyshev"
fix_test "$TEST_DIR/signal/HilbertTest.java" "hilbert"
fix_test "$TEST_DIR/signal/EllipticFilterTest.java" "elliptic"
fix_test "$TEST_DIR/signal/ButterworthFilterTest.java" "butterworth"
fix_test "$TEST_DIR/signal/Chebyshev1FilterTest.java" "chebyshev"
fix_test "$TEST_DIR/signal/FindPeaksTest.java" "findpeaks"
fix_test "$TEST_DIR/signal/fft/WelchTest.java" "welch"
fix_test "$TEST_DIR/signal/fft/SpectrogramTest.java" "spectrogram"
fix_test "$TEST_DIR/signal/fft/STFTTest.java" "stft"
fix_test "$TEST_DIR/signal/DetrendTest.java" "detrend"
fix_test "$TEST_DIR/signal/WindowsTest.java" "windows"
fix_test "$TEST_DIR/signal/filter/SOSFilterTest.java" "sos"
fix_test "$TEST_DIR/signal/SavitzkyGolayFilterTest.java" "savgol"

echo "Absolute path tests fixed."
