import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def read_complex_data_file(filename):
    data = np.loadtxt(filename)
    return data[:, 0] + 1j * data[:, 1]

def plot_test(test_id):
    signal_in = read_data_file(f'datasets/fft_input_{test_id}.txt')
    
    fft_python = read_complex_data_file(f'datasets/fft_output_{test_id}.txt')
    rfft_python = read_complex_data_file(f'datasets/rfft_output_{test_id}.txt')
    
    fft_java = read_complex_data_file(f'datasets/fft_output_java_{test_id}.txt')
    rfft_java = read_complex_data_file(f'datasets/rfft_output_java_{test_id}.txt')

    # Frequencies for plotting
    n = len(signal_in)
    fft_freq = np.fft.fftfreq(n)
    rfft_freq = np.fft.rfftfreq(n)

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 8))
    
    # FFT plot
    ax1.plot(fft_freq, np.abs(fft_python), label='Python FFT')
    ax1.plot(fft_freq, np.abs(fft_java), '--', label='Java FFT')
    ax1.set_title(f"FFT Magnitude Spectrum Comparison for test {test_id}")
    ax1.set_xlabel("Frequency")
    ax1.set_ylabel("Magnitude")
    ax1.legend()

    # RFFT plot
    ax2.plot(rfft_freq, np.abs(rfft_python), label='Python RFFT')
    ax2.plot(rfft_freq, np.abs(rfft_java), '--', label='Java RFFT')
    ax2.set_title(f"RFFT Magnitude Spectrum Comparison for test {test_id}")
    ax2.set_xlabel("Frequency")
    ax2.set_ylabel("Magnitude")
    ax2.legend()

    plt.tight_layout()
    plt.savefig(f"python/figs/fft_comparison_{test_id}.png")
    plt.close()

if __name__ == '__main__':
    plot_test(1)
    plot_test(2)
