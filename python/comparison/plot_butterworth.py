import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(input_filename, python_output_filename, java_output_filename):
    signal_in = read_data_file(input_filename)
    python_output = read_data_file(python_output_filename)
    java_output = read_data_file(java_output_filename)

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True)
    ax1.plot(signal_in, label='Input', linewidth=0.5)
    ax1.legend()
    ax1.set_title(f"Input signal for {input_filename}")
    ax2.plot(python_output, label='Python', linewidth=0.5)
    ax2.plot(java_output, label='Java', linestyle='--', linewidth=0.5)
    ax2.legend()
    ax2.set_title(f"Comparison for {input_filename}")
    plt.savefig(f"python/figs/{input_filename.split('/')[-1]}.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_test('datasets/butterworth_input1.txt', 'datasets/butterworth_output1.txt', 'datasets/butterworth_output1_java.txt')
    plot_test('datasets/butterworth_input2.txt', 'datasets/butterworth_output2.txt', 'datasets/butterworth_output2_java.txt')
    plot_test('datasets/butterworth_input3.txt', 'datasets/butterworth_output3.txt', 'datasets/butterworth_output3_java.txt')
