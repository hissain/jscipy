import numpy as np
import subprocess
import matplotlib.pyplot as plt
import seaborn as sns
from scipy.signal import find_peaks

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def run_java_peak_finder(input_filename):
    classpath = "java/build/libs/jscipy-1.0.0.jar:java/build/classes/java/test" # Main JAR and test classes
    process = subprocess.Popen(['java', '-cp', classpath, 'com.hissain.jscipy.signal.PeakFinderTest', input_filename], stdout=subprocess.PIPE)
    output, _ = process.communicate()
    if not output:
        return np.array([])
    return np.array([int(line) for line in output.decode().strip().splitlines()])

def run_test(input_filename):
    signal_in = read_data_file(input_filename)

    # Python implementation
    python_peaks, _ = find_peaks(signal_in)

    # Java implementation
    java_peaks = run_java_peak_finder(input_filename)

    # Compare outputs (simple comparison for now, can be improved)
    # For peak finding, direct RMSE on indices is not ideal.
    # We'll visually compare and check if the sets of peaks are similar.
    
    # Convert to sets for easier comparison of presence
    python_peaks_set = set(python_peaks)
    java_peaks_set = set(java_peaks)

    # Find common peaks
    common_peaks = list(python_peaks_set.intersection(java_peaks_set))
    
    # Find peaks only in Python
    only_in_python = list(python_peaks_set.difference(java_peaks_set))

    # Find peaks only in Java
    only_in_java = list(java_peaks_set.difference(python_peaks_set))

    print(f"Comparison for {input_filename}:")
    print(f"  Python peaks found: {len(python_peaks)}")
    print(f"  Java peaks found: {len(java_peaks)}")
    print(f"  Common peaks: {len(common_peaks)}")
    print(f"  Peaks only in Python: {len(only_in_python)}")
    print(f"  Peaks only in Java: {len(only_in_java)}")

    # Plotting
    plt.figure(figsize=(12, 6))
    plt.plot(signal_in, label='Input Signal', linewidth=0.5)
    plt.plot(python_peaks, signal_in[python_peaks], "x", label='Python Peaks')
    plt.plot(java_peaks, signal_in[java_peaks], "o", label='Java Peaks')
    plt.legend()
    plt.title(f"PeakFinder Comparison for {input_filename}")
    plt.savefig(f"python/figs/{input_filename.split('/')[-1]}_peaks.png")
    plt.close()

if __name__ == '__main__':
    run_test('datasets/findpeaks_input1.txt')
