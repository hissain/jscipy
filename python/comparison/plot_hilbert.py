import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_comparison(input_file, py_real, java_real, py_imag, java_imag, output_image):
    if not os.path.exists(input_file) or not os.path.exists(java_real):
        print(f"Skipping {output_image}: files not found")
        return

    signal_in = read_data_file(input_file)
    
    py_r = read_data_file(py_real)
    java_r = read_data_file(java_real)
    py_i = read_data_file(py_imag)
    java_i = read_data_file(java_imag)
    
    # Calculate Envelopes
    py_env = np.abs(py_r + 1j * py_i)
    java_env = np.abs(java_r + 1j * java_i)

    fig, axes = plt.subplots(3, 1, figsize=(10, 12), sharex=True)
    
    # Real Part
    axes[0].plot(signal_in, label='Input', color='gray', linestyle=':', linewidth=1.5)
    axes[0].plot(py_r, label='Python Real', color='C0', alpha=0.7, linewidth=1.5)
    axes[0].plot(java_r, label='Java Real', color='C1', linestyle='--', linewidth=2.0)
    axes[0].set_title("Real Part Comparison")
    axes[0].legend()
    
    # Imaginary Part
    axes[1].plot(py_i, label='Python Imag', color='C2', alpha=0.7, linewidth=1.5)
    axes[1].plot(java_i, label='Java Imag', color='C3', linestyle='--', linewidth=2.0)
    axes[1].set_title("Imaginary Part Comparison")
    axes[1].legend()
    
    # Envelope
    axes[2].plot(py_env, label='Python Envelope', color='C4', alpha=0.7, linewidth=1.5)
    axes[2].plot(java_env, label='Java Envelope', color='C5', linestyle='--', linewidth=2.0)
    axes[2].set_title("Envelope Comparison")
    axes[2].legend()

    plt.tight_layout()
    os.makedirs('python/figs/hilbert', exist_ok=True)
    style_utils.save_plot(fig, f"hilbert/{output_image.split('/')[-1]}") # Correcting path handling
    plt.close(fig)

if __name__ == '__main__':
    plot_comparison(
        'datasets/hilbert/hilbert_input_1.txt',
        'datasets/hilbert/hilbert_output_1_real.txt',
        'datasets/hilbert/hilbert_output_1_real_java.txt',
        'datasets/hilbert/hilbert_output_1_imag.txt',
        'datasets/hilbert/hilbert_output_1_imag_java.txt',
        'hilbert_comparison_1.png'
    )
    
    plot_comparison(
        'datasets/hilbert/hilbert_input_2.txt',
        'datasets/hilbert/hilbert_output_2_real.txt',
        'datasets/hilbert/hilbert_output_2_real_java.txt',
        'datasets/hilbert/hilbert_output_2_imag.txt',
        'datasets/hilbert/hilbert_output_2_imag_java.txt',
        'hilbert_comparison_2.png'
    )
