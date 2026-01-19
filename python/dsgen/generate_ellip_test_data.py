#!/usr/bin/env python3
"""
Generate test data for elliptic filter validation.
This script creates input signals and expected outputs using SciPy's ellip filter.
"""

import numpy as np
from scipy import signal
import numpy as np
from scipy import signal
import os

# Create output directory if it doesn't exist
OUTPUT_DIR = "../../datasets"
os.makedirs(OUTPUT_DIR, exist_ok=True)
np.random.seed(42)

def generate_test_signal(length=1000, freq1=5, freq2=20, sample_rate=250):
    """Generate a test signal with two frequency components plus noise."""
    t = np.arange(length) / sample_rate
    signal_data = (np.sin(2 * np.pi * freq1 * t) + 
                   0.5 * np.sin(2 * np.pi * freq2 * t) + 
                   0.1 * np.random.randn(length))
    return signal_data

def apply_elliptic_filter(signal_data, sample_rate, cutoff, order, rp, rs):
    """Apply elliptic filter using scipy.signal.ellip"""
    # Design the filter
    sos = signal.ellip(order, rp, rs, cutoff, btype='low', fs=sample_rate, output='sos')
    
    # Apply zero-phase filtering (filtfilt)
    filtered = signal.sosfiltfilt(sos, signal_data)
    
    return filtered

def save_signal(filename, data):
    """Save signal to text file."""
    filepath = os.path.join(OUTPUT_DIR, filename)
    np.savetxt(filepath, data, fmt='%.18e')
    print(f"Saved: {filepath}")



def main():
    """Generate test data for elliptic filter."""
    
    # Test case 1: Order 4, rp=1dB, rs=20dB, cutoff=20Hz, sr=250Hz
    print("Generating test case 1: Order 4, rp=1dB, rs=20dB, cutoff=20Hz, sr=250Hz")
    
    # Use the same input as Chebyshev tests for consistency
    input_file = os.path.join(OUTPUT_DIR, "ellip_input1.txt")
    if os.path.exists(input_file):
        test_signal = np.loadtxt(input_file)
        print(f"Loaded existing input: {input_file}")
    else:
        # Should normally be there from previous runs, but fallback:
        test_signal = generate_test_signal(length=1000, freq1=5, freq2=35, sample_rate=250)
        save_signal("ellip_input1.txt", test_signal)
    
    # Apply elliptic filter (SciPy)
    order = 4
    ripple_db = 1.0  # Passband ripple
    stopband_db = 20.0  # Stopband attenuation
    cutoff = 20.0
    sample_rate = 250.0
    
    scipy_filtered = apply_elliptic_filter(test_signal, sample_rate, cutoff, 
                                           order, ripple_db, stopband_db)
    


    
    print("\nTest data generation complete!")
    print(f"Input samples: {len(test_signal)}")
    print(f"Output samples: {len(scipy_filtered)}")
    print(f"Input RMS: {np.sqrt(np.mean(test_signal**2)):.6f}")
    print(f"Output RMS: {np.sqrt(np.mean(scipy_filtered**2)):.6f}")

if __name__ == "__main__":
    main()
