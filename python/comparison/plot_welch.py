import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_welch(freq_file, psd_py_file, psd_java_file):
    if not os.path.exists(psd_java_file):
        print(f"Warning: {psd_java_file} not found.")
        return

    f = read_data_file(freq_file)
    psd_py = read_data_file(psd_py_file)
    psd_java = read_data_file(psd_java_file)

    fig, ax = plt.subplots(figsize=style_utils.FIG_SIZE_WIDE)
    ax.semilogy(f, psd_py, label='SciPy Welch', linewidth=2.0, alpha=0.8)
    ax.semilogy(f, psd_java, label='jSciPy Welch', linestyle='--', linewidth=2.0)
    ax.set_title("Welch PSD Estimate Comparison")
    ax.set_xlabel('Frequency [Hz]')
    ax.set_ylabel('PSD [V**2/Hz]')
    ax.legend()
    
    plt.tight_layout()
    style_utils.save_plot(fig, "welch_comparison.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_welch(
        'datasets/welch_output_freq1.txt',
        'datasets/welch_output_psd1.txt',
        'datasets/welch_output_psd1_java.txt'
    )
