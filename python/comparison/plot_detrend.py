import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_comparison(input_file, py_lin, java_lin, py_const, java_const, output_image):
    if not os.path.exists(input_file) or not os.path.exists(java_lin):
        print(f"Skipping {output_image}: files not found")
        return

    signal_in = read_data_file(input_file)
    
    # Linear Detrend
    py_l = read_data_file(py_lin)
    java_l = read_data_file(java_lin)
    
    # Constant Detrend
    py_c = read_data_file(py_const)
    java_c = read_data_file(java_const)

    fig, axes = plt.subplots(3, 1, figsize=(10, 10), sharex=True)
    
    # Input
    axes[0].plot(signal_in, label='Input', color='gray', linewidth=1.5)
    axes[0].set_title("Input Signal")
    axes[0].legend()
    
    # Linear Detrend
    axes[1].plot(py_l, label='Python Linear', linewidth=1.5, alpha=0.8)
    axes[1].plot(java_l, label='Java Linear', linestyle='--', linewidth=2.0)
    axes[1].set_title("Linear Detrend Comparison")
    axes[1].legend()

    # Constant Detrend
    axes[2].plot(py_c, label='Python Constant', linewidth=1.5, alpha=0.8)
    axes[2].plot(java_c, label='Java Constant', linestyle='--', linewidth=2.0)
    axes[2].set_title("Constant Detrend Comparison")
    axes[2].legend()

    plt.tight_layout()
    style_utils.save_plot(fig, output_image.split('/')[-1])
    plt.close(fig)

if __name__ == '__main__':
    plot_comparison(
        'datasets/detrend_input_1.txt',
        'datasets/detrend_output_linear_1.txt',
        'datasets/detrend_output_linear_1_java.txt',
        'datasets/detrend_output_constant_1.txt',
        'datasets/detrend_output_constant_1_java.txt',
        'detrend_comparison_1.png'
    )
    
    plot_comparison(
        'datasets/detrend_input_2.txt',
        'datasets/detrend_output_linear_2.txt',
        'datasets/detrend_output_linear_2_java.txt',
        'datasets/detrend_output_constant_2.txt',
        'datasets/detrend_output_constant_2_java.txt',
        'detrend_comparison_2.png'
    )
