import numpy as np
from scipy import signal
import os

def generate_spectrogram_data(test_name, data, fs):
    # Compute spectrogram using SciPy default settings (nperseg=256, noverlap=nperseg//8)
    f, t, Sxx = signal.spectrogram(data, fs=fs, nperseg=256, noverlap=32, window='hann', scaling='density', mode='psd')
    
    # Debug: Compare with STFT
    f_stft, t_stft, Zxx = signal.stft(data, fs=fs, nperseg=256, noverlap=32, window='hann', boundary='zeros', padded=True)
    print(f"Spectrogram shape: {Sxx.shape}, Time len: {len(t)}")
    print(f"STFT shape: {Zxx.shape}, Time len: {len(t_stft)}")

    
    script_dir = os.path.dirname(__file__)
    datasets_dir = os.path.abspath(os.path.join(script_dir, '../../datasets/spectrogram'))
    
    input_filename = f'{test_name}_input.txt'
    output_freqs_filename = f'{test_name}_freqs.txt'
    output_times_filename = f'{test_name}_times.txt'
    output_Sxx_filename = f'{test_name}_Sxx.txt'
    
    os.makedirs(datasets_dir, exist_ok=True)
    with open(os.path.join(datasets_dir, input_filename), 'w', newline='\n') as f_out:
        np.savetxt(f_out, data)
    with open(os.path.join(datasets_dir, output_freqs_filename), 'w', newline='\n') as f_out:
        np.savetxt(f_out, f)
    with open(os.path.join(datasets_dir, output_times_filename), 'w', newline='\n') as f_out:
        np.savetxt(f_out, t)
    with open(os.path.join(datasets_dir, output_Sxx_filename), 'w', newline='\n') as f_out:
        np.savetxt(f_out, Sxx)
    
    # Also save shape for Java reading convenience
    with open(os.path.join(datasets_dir, f'{test_name}_Sxx_shape.txt'), 'w', newline='\n') as f_shape:
        f_shape.write(f"{Sxx.shape[0]}\n{Sxx.shape[1]}")

if __name__ == '__main__':
    # Test case 1: Chirp signal
    fs = 1000.0
    N = 10000
    time = np.arange(N) / fs
    # Chirp from 100Hz to 200Hz
    chirp = signal.chirp(time, f0=100, t1=10, f1=200, method='linear')
    generate_spectrogram_data('spectrogram_chirp', chirp, fs)
    
    # Test case 2: Simple sine wave sum
    sine_sum = np.sin(2 * np.pi * 50 * time) + np.sin(2 * np.pi * 150 * time)
    generate_spectrogram_data('spectrogram_sine', sine_sum, fs)
