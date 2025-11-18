import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def read_peaks_file(filename):
    with open(filename, 'r') as f:
        # Handle empty file
        content = f.read().strip()
        if not content:
            return np.array([], dtype=int)
        return np.array([int(line.strip()) for line in content.splitlines()])

def plot_test(input_filename, python_peaks_filename, java_peaks_filename):
    signal_in = read_data_file(input_filename)
    python_peaks = read_peaks_file(python_peaks_filename)
    java_peaks = read_peaks_file(java_peaks_filename)

    # Plotting
    plt.figure(figsize=(12, 6))
    plt.plot(signal_in, label='Input Signal', linewidth=0.5)
    plt.plot(python_peaks, signal_in[python_peaks], "x", label='Python Peaks')
    plt.plot(java_peaks, signal_in[java_peaks], "o", label='Java Peaks', markerfacecolor='none')
    plt.legend()
    plt.title(f"FindPeaks Comparison for {input_filename}")
    plt.savefig(f"python/figs/{input_filename.split('/')[-1]}_peaks.png")
    plt.close()

if __name__ == '__main__':
    for i in range(1, 9):
        plot_test(f'datasets/findpeaks_input{i}.txt', f'datasets/findpeaks_output{i}.txt', f'datasets/findpeaks_output{i}_java.txt')
