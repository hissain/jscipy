import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f if line.strip()])

def plot_comparison(input_filename, py_real, java_real, py_imag, java_imag, output_image):
    signal_in = read_data_file(input_filename)
    
    py_r = read_data_file(py_real)
    java_r = read_data_file(java_real)
    py_i = read_data_file(py_imag)
    java_i = read_data_file(java_imag)
    
    # Calculate Envelopes
    py_env = np.abs(py_r + 1j * py_i)
    java_env = np.abs(java_r + 1j * java_i)

    fig, axes = plt.subplots(3, 1, figsize=(10, 12), sharex=True)
    
    # Real Part
    axes[0].plot(signal_in, label='Input', color='gray', linestyle=':', linewidth=1)
    axes[0].plot(py_r, label='Python Real', color='blue', alpha=0.7)
    axes[0].plot(java_r, label='Java Real', color='orange', linestyle='--')
    axes[0].set_title("Real Part Comparison")
    axes[0].legend()
    axes[0].grid(True)

    # Imaginary Part
    axes[1].plot(py_i, label='Python Imag', color='green', alpha=0.7)
    axes[1].plot(java_i, label='Java Imag', color='red', linestyle='--')
    axes[1].set_title("Imaginary Part Comparison")
    axes[1].legend()
    axes[1].grid(True)
    
    # Envelope
    axes[2].plot(py_env, label='Python Envelope', color='purple', alpha=0.7)
    axes[2].plot(java_env, label='Java Envelope', color='brown', linestyle='--')
    axes[2].set_title("Envelope Comparison")
    axes[2].legend()
    axes[2].grid(True)

    plt.tight_layout()
    
    os.makedirs(os.path.dirname(output_image), exist_ok=True)
    plt.savefig(output_image)
    print(f"Saved {output_image}")
    plt.close(fig)

if __name__ == '__main__':
    plot_comparison(
        'datasets/hilbert_input_1.txt',
        'datasets/hilbert_output_1_real.txt',
        'datasets/hilbert_output_1_real_java.txt',
        'datasets/hilbert_output_1_imag.txt',
        'datasets/hilbert_output_1_imag_java.txt',
        'python/figs/hilbert_comparison_1.png'
    )
    
    plot_comparison(
        'datasets/hilbert_input_2.txt',
        'datasets/hilbert_output_2_real.txt',
        'datasets/hilbert_output_2_real_java.txt',
        'datasets/hilbert_output_2_imag.txt',
        'datasets/hilbert_output_2_imag_java.txt',
        'python/figs/hilbert_comparison_2.png'
    )
