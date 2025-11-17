import numpy as np
from scipy import signal
import subprocess
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def run_java_filter(input_filename, order, cutoff, sample_rate):
    classpath = "java/build/libs/jscipy-1.0.0.jar"
    process = subprocess.Popen(['java', '-cp', classpath, 'com.hissain.jscipy.signal.ButterworthFilter', input_filename, str(order), str(cutoff), str(sample_rate)], stdout=subprocess.PIPE)
    output, _ = process.communicate()
    if not output:
        return np.array([])
    return np.array([float(line) for line in output.decode().strip().splitlines()])

def run_test(input_filename, expected_output_filename, order, cutoff, sample_rate):
    signal_in = read_data_file(input_filename)
    expected_output = read_data_file(expected_output_filename)

    # Python implementation
    b, a = signal.butter(order, cutoff / (sample_rate / 2), btype='low')
    python_output = signal.filtfilt(b, a, signal_in)

    # Java implementation
    java_output = run_java_filter(input_filename, order, cutoff, sample_rate)

    # Compare outputs
    rmse = np.sqrt(np.mean((python_output - java_output)**2))
    print(f"RMSE for {input_filename}: {rmse}")

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True)
    ax1.plot(signal_in[:100], label='Input', linewidth=0.5)
    ax1.legend()
    ax1.set_title(f"Input signal for {input_filename}")
    ax2.plot(python_output[:100], label='Python', linewidth=0.5)
    ax2.plot(java_output[:100], label='Java', linewidth=0.5)
    ax2.legend()
    ax2.set_title(f"Comparison for {input_filename}")
    plt.savefig(f"python/figs/{input_filename.split('/')[-1]}.png")
    plt.close(fig)

if __name__ == '__main__':
    run_test('datasets/butterworth_input1.txt', 'datasets/butterworth_output1.txt', 2, 20, 250)
    run_test('datasets/butterworth_input2.txt', 'datasets/butterworth_output2.txt', 3, 20, 250)
    run_test('datasets/butterworth_input3.txt', 'datasets/butterworth_output3.txt', 4, 20, 250)
