import numpy as np
from scipy import signal
import os

def generate_welch_test_data(test_id, sample_rate, num_samples, nperseg):
    t = np.linspace(0, num_samples / sample_rate, num_samples, False)
    # Signal: 50Hz and 80Hz components
    sig = np.sin(2 * np.pi * 50 * t) + 0.5 * np.sin(2 * np.pi * 80 * t)
    
    # Welch's method (default: window='hann', scaling='density', average='mean')
    # nperseg: Length of each segment
    # noverlap: nperseg // 2 (default)
    # detrend: False (no detrending) to match Java
    f, Pxx = signal.welch(sig, fs=sample_rate, nperseg=nperseg, detrend=False)
    
    
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/welch")
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        
    with open(os.path.join(output_dir, f"welch_input{test_id}.txt"), 'w', newline='\n') as f_out:
        np.savetxt(f_out, sig, fmt='%.18e')
    with open(os.path.join(output_dir, f"welch_output_freq{test_id}.txt"), 'w', newline='\n') as f_out:
        np.savetxt(f_out, f, fmt='%.18e')
    with open(os.path.join(output_dir, f"welch_output_psd{test_id}.txt"), 'w', newline='\n') as f_out:
        np.savetxt(f_out, Pxx, fmt='%.18e')
    
    print(f"Generated welch test data {test_id} (nperseg={nperseg})")

if __name__ == "__main__":
    sr = 1000
    n = 2000
    nperseg = 256
    generate_welch_test_data(1, sr, n, nperseg)
