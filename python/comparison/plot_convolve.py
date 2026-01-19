import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_convolve_test(input_filename, window_filename, python_output_filename, java_output_filename):
    if not os.path.exists(java_output_filename):
        print(f"File not found: {java_output_filename}")
        return

    signal_in = read_data_file(input_filename)
    # window = read_data_file(window_filename) # Unused in plot
    python_output = read_data_file(python_output_filename)
    java_output = read_data_file(java_output_filename)

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=style_utils.FIG_SIZE_WIDE)
    ax1.plot(signal_in, label='Input Signal', linewidth=1.5, color='gray')
    ax1.legend()
    ax1.set_title(f"Input Signal (Convolution)")
    
    ax2.plot(python_output, label='Python SciPy', linewidth=1.5, alpha=0.8)
    ax2.plot(java_output, label='Java jSciPy', linestyle='--', linewidth=2.0)
    ax2.legend()
    ax2.set_title(f"Convolution Output Comparison")
    
    plt.tight_layout()
    os.makedirs('python/figs/convolve', exist_ok=True)
    style_utils.save_plot(fig, "convolve/convolve_comparison.png")
    plt.close(fig)

if __name__ == '__main__':
    if os.path.exists('datasets/convolve/convolve_output_java.txt'):
        plot_convolve_test('datasets/convolve/convolve_input_signal.txt', 'datasets/convolve/convolve_input_window.txt', 'datasets/convolve/convolve_output.txt', 'datasets/convolve/convolve_output_java.txt')
    else:
        print("datasets/convolve/convolve_output_java.txt not found. Run tests first.")
