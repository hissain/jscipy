import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f if line.strip()])

def plot_comparison(input_filename, py_linear, java_linear, py_constant, java_constant, output_image):
    signal_in = read_data_file(input_filename)
    py_lin = read_data_file(py_linear)
    java_lin = read_data_file(java_linear)
    py_const = read_data_file(py_constant)
    java_const = read_data_file(java_constant)

    fig, axes = plt.subplots(3, 1, figsize=(10, 12), sharex=True)
    
    # Input
    axes[0].plot(signal_in, label='Original Signal', color='black', linewidth=1)
    axes[0].set_title("Original Signal")
    axes[0].legend()
    axes[0].grid(True)

    # Linear Detrend
    axes[1].plot(py_lin, label='Python (Linear)', color='blue', linewidth=2, alpha=0.7)
    axes[1].plot(java_lin, label='Java (Linear)', color='orange', linestyle='--', linewidth=2)
    axes[1].set_title("Linear Detrending Comparison")
    axes[1].legend()
    axes[1].grid(True)

    # Constant Detrend
    axes[2].plot(py_const, label='Python (Constant)', color='green', linewidth=2, alpha=0.7)
    axes[2].plot(java_const, label='Java (Constant)', color='red', linestyle='--', linewidth=2)
    axes[2].set_title("Constant Detrending Comparison")
    axes[2].legend()
    axes[2].grid(True)

    plt.tight_layout()
    
    # Ensure directory exists
    os.makedirs(os.path.dirname(output_image), exist_ok=True)
    plt.savefig(output_image)
    print(f"Saved {output_image}")
    plt.close(fig)

if __name__ == '__main__':
    plot_comparison(
        'datasets/detrend_input_1.txt',
        'datasets/detrend_output_linear_1.txt',
        'datasets/detrend_output_linear_1_java.txt',
        'datasets/detrend_output_constant_1.txt',
        'datasets/detrend_output_constant_1_java.txt',
        'python/figs/detrend_comparison_1.png'
    )
    
    plot_comparison(
        'datasets/detrend_input_2.txt',
        'datasets/detrend_output_linear_2.txt',
        'datasets/detrend_output_linear_2_java.txt',
        'datasets/detrend_output_constant_2.txt',
        'datasets/detrend_output_constant_2_java.txt',
        'python/figs/detrend_comparison_2.png'
    )
