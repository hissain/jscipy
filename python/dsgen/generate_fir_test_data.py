import numpy as np
from scipy import signal
import os

# Set seed for reproducibility (though firwin is deterministic)
np.random.seed(42)

DATA_DIR = "datasets/fir"
os.makedirs(DATA_DIR, exist_ok=True)

def save_data(filename, data):
    filepath = os.path.join(DATA_DIR, filename)
    with open(filepath, 'w', newline='\n') as f:
        np.savetxt(f, data, fmt='%.16e')
    print(f"Saved {filepath}")

def generate_fir_test_data():
    fs = 1000.0
    
    # Case 1: Lowpass, 31 taps, 100 Hz
    # pass_zero=True (default for single cutoff)
    taps1 = signal.firwin(31, 100, fs=fs, pass_zero=True, window='hamming', scale=True)
    save_data("fir_lowpass_31_100.txt", taps1)
    
    # Case 2: Highpass, 31 taps, 100 Hz
    # pass_zero=False
    taps2 = signal.firwin(31, 100, fs=fs, pass_zero=False, window='hamming', scale=True)
    save_data("fir_highpass_31_100.txt", taps2)
    
    # Case 3: Bandpass, 51 taps, [100, 200] Hz
    taps3 = signal.firwin(51, [100, 200], fs=fs, pass_zero=False, window='hamming', scale=True)
    save_data("fir_bandpass_51_100_200.txt", taps3)
    
    # Case 4: Bandstop, 51 taps, [100, 200] Hz
    taps4 = signal.firwin(51, [100, 200], fs=fs, pass_zero=True, window='hamming', scale=True)
    save_data("fir_bandstop_51_100_200.txt", taps4)
    
    # Case 5: Even length Lowpass, 32 taps, 100 Hz
    taps5 = signal.firwin(32, 100, fs=fs, pass_zero=True, window='hamming', scale=True)
    save_data("fir_lowpass_32_100.txt", taps5)

    print("FIR filter test data generated.")

if __name__ == "__main__":
    generate_fir_test_data()
