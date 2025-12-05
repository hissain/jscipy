import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_convolve_test(input_filename, window_filename, python_output_filename, java_output_filename):
    signal_in = read_data_file(input_filename)
    window = read_data_file(window_filename)
    python_output = read_data_file(python_output_filename)
    java_output = read_data_file(java_output_filename)

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=(10, 8))
    ax1.plot(signal_in, label='Input Signal', linewidth=0.5)
    # Scale window for visibility or just plot on separate axis if needed, but let's keep it simple
    # ax1.plot(window, label='Window', linewidth=0.5) 
    ax1.legend()
    ax1.set_title(f"Input signal for {input_filename}")
    
    ax2.plot(python_output, label='Python', linewidth=1.5, alpha=0.7)
    ax2.plot(java_output, label='Java', linestyle='--', linewidth=1.5, alpha=0.7)
    ax2.legend()
    ax2.set_title(f"Comparison for {input_filename}")
    
    output_path = f"python/figs/convolve_comparison.png"
    plt.savefig(output_path)
    print(f"Saved plot to {output_path}")
    plt.close(fig)

if __name__ == '__main__':
    if os.path.exists('datasets/convolve_output_java.txt'):
        plot_convolve_test('datasets/convolve_input_signal.txt', 'datasets/convolve_input_window.txt', 'datasets/convolve_output.txt', 'datasets/convolve_output_java.txt')
    else:
        print("datasets/convolve_output_java.txt not found. Run tests first.")
