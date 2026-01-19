import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_medfilt_comparison(input_file, py_file, java_file):
    if not os.path.exists(java_file):
        print(f"Warning: {java_file} not found")
        return

    signal_in = read_data_file(input_file)
    py_out = read_data_file(py_file)
    java_out = read_data_file(java_file)

    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=style_utils.FIG_SIZE_WIDE)
    
    ax1.plot(signal_in, label='Input', linewidth=1.5, color='gray')
    ax1.legend()
    ax1.set_title("Input Signal (MedFilt)")
    
    ax2.plot(py_out, label='SciPy medfilt', linewidth=1.5, alpha=0.8)
    ax2.plot(java_out, label='jSciPy medfilt', linestyle='--', linewidth=2.0)
    ax2.legend()
    ax2.set_title("Filtered Output Comparison")
    
    plt.tight_layout()
    os.makedirs('python/figs/medfilt', exist_ok=True)
    style_utils.save_plot(fig, "medfilt/medfilt_comparison.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_medfilt_comparison(
        'datasets/medfilt/medfilt_input.txt',
        'datasets/medfilt/medfilt_output.txt',
        'datasets/medfilt/medfilt_output_java.txt'
    )
