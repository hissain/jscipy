#!/usr/bin/env python3
"""
Generate test data for elliptic filter validation.
This script creates input signals and expected outputs using SciPy's ellip filter.
"""

import numpy as np
from scipy import signal
import matplotlib.pyplot as plt
import os

# Create output directory if it doesn't exist
OUTPUT_DIR = "../datasets"
FIGS_DIR = "figs"
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(FIGS_DIR, exist_ok=True)

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

def plot_comparison(input_signal, scipy_output, java_output, title, filename):
    """Create a comparison plot."""
    plt.figure(figsize=(12, 6))
    
    # Time domain
    plt.subplot(2, 1, 1)
    plt.plot(input_signal, label='Input', alpha=0.3, color='gray')
    plt.plot(scipy_output, label='SciPy (Reference)', linewidth=3, alpha=0.7, color='C0')
    if java_output is not None:
        plt.plot(java_output, label='Java (Implementation)', linewidth=1.5, linestyle='--', color='C1')
        
    plt.xlabel('Sample')
    plt.ylabel('Amplitude')
    plt.title(f'{title} - Time Domain')
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    # Frequency domain
    plt.subplot(2, 1, 2)
    freqs_in = np.fft.rfftfreq(len(input_signal), 1/250.0)
    fft_in = np.abs(np.fft.rfft(input_signal))
    fft_scipy = np.abs(np.fft.rfft(scipy_output))
    
    plt.plot(freqs_in, fft_in, label='Input', alpha=0.3, color='gray')
    plt.plot(freqs_in, fft_scipy, label='SciPy (Reference)', linewidth=3, alpha=0.7, color='C0')
    
    if java_output is not None:
        fft_java = np.abs(np.fft.rfft(java_output))
        plt.plot(freqs_in, fft_java, label='Java (Implementation)', linewidth=1.5, linestyle='--', color='C1')
        
        # Calculate error
        error = np.sqrt(np.mean((scipy_output - java_output)**2))
        plt.text(0.02, 0.95, f'RMSE (Time Domain): {error:.2e}', transform=plt.gca().transAxes, 
                 bbox=dict(facecolor='white', alpha=0.8))

    plt.xlabel('Frequency (Hz)')
    plt.ylabel('Magnitude')
    plt.title(f'{title} - Frequency Domain')
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.xlim(0, 60)
    
    plt.tight_layout()
    filepath = os.path.join(FIGS_DIR, filename)
    plt.savefig(filepath, dpi=150, bbox_inches='tight')
    print(f"Plot saved: {filepath}")
    plt.close()

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
    
    save_signal("ellip_output1.txt", scipy_filtered)
    
    # Load Java filtered output if available
    java_file = os.path.join(OUTPUT_DIR, "ellip_output1_java.txt")
    java_filtered = None
    if os.path.exists(java_file):
        java_filtered = np.loadtxt(java_file)
        print(f"Loaded Java output: {java_file}")
    else:
        print(f"Warning: Java output not found at {java_file}")

    # Create comparison plot
    plot_comparison(test_signal, scipy_filtered, java_filtered,
                   f"Elliptic Filter (order={order}, rp={ripple_db}dB, rs={stopband_db}dB, fc={cutoff}Hz)",
                   "ellip_comparison.png")

    
    print("\nTest data generation complete!")
    print(f"Input samples: {len(test_signal)}")
    print(f"Output samples: {len(scipy_filtered)}")
    print(f"Input RMS: {np.sqrt(np.mean(test_signal**2)):.6f}")
    print(f"Output RMS: {np.sqrt(np.mean(scipy_filtered**2)):.6f}")

if __name__ == "__main__":
    main()
