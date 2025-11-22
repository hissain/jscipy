import os
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

script_dir = os.path.dirname(__file__)

def read_data_file(filename):
    absolute_path = os.path.join(script_dir, filename)
    with open(absolute_path, 'r') as f:
        data = [float(line.strip()) for line in f if line.strip()]
    return data

def plot_test(test_name, title):
    signal_in = read_data_file(f'../../datasets/{test_name}_input.txt')
    savgol_python = read_data_file(f'../../datasets/{test_name}_output.txt')
    savgol_java = read_data_file(f'../../datasets/{test_name}_output_java.txt')

    # Time axes for plotting
    t = np.linspace(0, 100, len(signal_in)) # Arbitrary time

    # Plotting
    plt.figure(figsize=(12, 6))
    plt.plot(t, signal_in, 'o-', alpha=0.3, label='Original Signal')
    plt.plot(t, savgol_python, 's-', label='Python SavGol')
    plt.plot(t, savgol_java, 'x--', label='Java SavGol')
    plt.title(title)
    plt.xlabel("Sample")
    plt.ylabel("Value")
    plt.legend()

    plt.tight_layout()
    save_path = os.path.join(script_dir, f"../figs/{test_name}.png")
    plt.savefig(save_path)
    plt.close()
    print(f"Generated plot: {save_path}")

if __name__ == '__main__':
    plot_test('savitzky_golay_smoothing', 'Savitzky-Golay Smoothing Comparison')
    plot_test('savitzky_golay_differentiation', 'Savitzky-Golay Differentiation Comparison')
