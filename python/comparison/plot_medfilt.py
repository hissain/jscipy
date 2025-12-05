import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_medfilt_test(input_filename, python_output_filename, java_output_filename):
    signal_in = read_data_file(input_filename)
    python_output = read_data_file(python_output_filename)
    java_output = read_data_file(java_output_filename)

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=(10, 8))
    ax1.plot(signal_in, label='Input', linewidth=0.5)
    ax1.legend()
    ax1.set_title(f"Input signal for {input_filename}")
    ax2.plot(python_output, label='Python', linewidth=1.5, alpha=0.7)
    ax2.plot(java_output, label='Java', linestyle='--', linewidth=1.5, alpha=0.7)
    ax2.legend()
    ax2.set_title(f"Comparison for {input_filename}")
    
    output_path = f"python/figs/medfilt_comparison.png"
    plt.savefig(output_path)
    print(f"Saved plot to {output_path}")
    plt.close(fig)

if __name__ == '__main__':
    if os.path.exists('datasets/medfilt_output_java.txt'):
        plot_medfilt_test('datasets/medfilt_input.txt', 'datasets/medfilt_output.txt', 'datasets/medfilt_output_java.txt')
    else:
        print("datasets/medfilt_output_java.txt not found. Run tests first.")
