import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def read_peaks_file(filename):
    with open(filename, 'r') as f:
        content = f.read().strip()
        if not content:
            return np.array([], dtype=int)
        return np.array([int(line.strip()) for line in content.splitlines()])

def plot_test(input_filename, python_peaks_filename, java_peaks_filename):
    if not os.path.exists(java_peaks_filename):
        print(f"Skipping FindPeaks: {java_peaks_filename} not found")
        return

    signal_in = read_data_file(input_filename)
    python_peaks = read_peaks_file(python_peaks_filename)
    java_peaks = read_peaks_file(java_peaks_filename)

    # Plotting
    fig, ax = plt.subplots(figsize=style_utils.FIG_SIZE_WIDE)
    ax.plot(signal_in, label='Input Signal', linewidth=1.0, color='gray')
    
    # Plot peaks
    if len(python_peaks) > 0:
        ax.plot(python_peaks, signal_in[python_peaks], "x", label='Python Peaks', markersize=8, markeredgewidth=2)
    if len(java_peaks) > 0:
        ax.plot(java_peaks, signal_in[java_peaks], "o", label='Java Peaks', markerfacecolor='none', markersize=12, markeredgewidth=2)
    
    ax.legend()
    ax.set_title(f"Peak Detection Comparison")
    
    plt.tight_layout()
    output_filename = f"{input_filename.split('/')[-1]}_peaks.png"
    style_utils.save_plot(fig, output_filename)
    plt.close(fig)

if __name__ == '__main__':
    plot_test('datasets/findpeaks_input1.txt', 'datasets/findpeaks_output1.txt', 'datasets/findpeaks_output1_java.txt')
