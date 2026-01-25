import numpy as np
from scipy import signal
import os

def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

def save_txt(filename, data, fmt='%.18e'):
    with open(filename, 'w', newline='\n') as f:
        np.savetxt(f, data, fmt=fmt, delimiter=',')
    print(f"Saved {filename}")

def generate_data():
    output_dir = "datasets/periodogram"
    ensure_dir(output_dir)

    # 1. Generate Input Signal (Sine wave mixture + noise)
    fs = 1000.0  # Sampling frequency
    T = 1.0       # Duration in seconds
    t = np.linspace(0, T, int(T*fs), endpoint=False)
    
    # 50 Hz and 120 Hz sine waves + random noise
    np.random.seed(42)  # For reproducibility
    data = np.sin(2*np.pi*50*t) + 0.5*np.sin(2*np.pi*120*t) + 0.2*np.random.normal(size=len(t))
    
    save_txt(f"{output_dir}/periodogram_input.txt", data)

    # 2. Compute Periodogram with default parameters (Hann window)
    # scipy.signal.periodogram returns (frequencies, psd)
    frequencies, psd = signal.periodogram(data, fs, window='hann', scaling='density')
    
    save_txt(f"{output_dir}/periodogram_frequencies.txt", frequencies)
    save_txt(f"{output_dir}/periodogram_psd.txt", psd)

    print(f"Number of frequency bins: {len(frequencies)}")
    print(f"Frequency range: {frequencies[0]} to {frequencies[-1]} Hz")
    print("Periodogram Data Generation Complete.")

if __name__ == "__main__":
    generate_data()
