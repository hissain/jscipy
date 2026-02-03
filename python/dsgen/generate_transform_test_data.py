import numpy as np
from scipy import signal
import os

def save_data(directory, filename, data):
    """Save test data to file."""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = os.path.join(script_dir, f"../../datasets/{directory}")
    os.makedirs(output_dir, exist_ok=True)
    
    filepath = os.path.join(output_dir, filename)
    with open(filepath, 'w', newline='\n') as f:
        np.savetxt(f, data, fmt='%.18e')
    print(f"Saved: {filepath}")

def generate_transform_test_data():
    """Generate test data for filter transform coverage tests."""
    print("=" * 70)
    print("Generating filter transform test data")
    print("=" * 70)
    
    np.random.seed(42)
    
    # Generate test signal with multiple frequency components
    fs = 250.0  # Sample rate (Hz)
    duration = 1.0  # 1 second
    t = np.linspace(0, duration, int(fs * duration), endpoint=False)
    
    # Create signal with multiple frequency components
    # - 10 Hz low frequency component
    # - 60 Hz mid-frequency (power line noise simulation)
    # - 100 Hz high frequency component
    # - Small noise
    signal_input = (1.0 * np.sin(2 * np.pi * 10 * t) + 
                   0.5 * np.sin(2 * np.pi * 60 * t) +
                   0.3 * np.sin(2 * np.pi * 100 * t) +
                   0.05 * np.random.randn(len(t)))
    
    print(f"\nGenerated input signal: {len(signal_input)} samples at {fs} Hz")
    print(f"  - 10 Hz component (amplitude 1.0)")
    print(f"  - 60 Hz component (amplitude 0.5)")
    print(f"  - 100 Hz component (amplitude 0.3)")
    
    # ========================================================================
    # Test 1: Highpass filter (remove frequencies below 50 Hz)
    # ========================================================================
    print("\n[1/3] Highpass Filter (cutoff = 50 Hz)")
    print("  Purpose: Remove low frequencies, keep 60 Hz and 100 Hz")
    
    b_hp, a_hp = signal.butter(4, 50, fs=fs, btype='high', analog=False)
    highpass_output = signal.filtfilt(b_hp, a_hp, signal_input)
    
    save_data("filters", "transform_highpass_input.txt", signal_input)
    save_data("filters", "transform_highpass_output.txt", highpass_output)
    
    # ========================================================================
    # Test 2: Bandpass filter (keep only 50-70 Hz range, centered at 60 Hz)
    # ========================================================================
    print("\n[2/3] Bandpass Filter (50-70 Hz, centered at 60 Hz)")
    print("  Purpose: Keep only 60 Hz component, remove 10 Hz and 100 Hz")
    
    b_bp, a_bp = signal.butter(4, [50, 70], fs=fs, btype='band', analog=False)
    bandpass_output = signal.filtfilt(b_bp, a_bp, signal_input)
    
    save_data("filters", "transform_bandpass_input.txt", signal_input)
    save_data("filters", "transform_bandpass_output.txt", bandpass_output)
    
    # ========================================================================
    # Test 3: Bandstop/notch filter (remove 55-65 Hz, notch out 60 Hz)
    # ========================================================================
    print("\n[3/3] Bandstop Filter (55-65 Hz, notch at 60 Hz)")
    print("  Purpose: Remove 60 Hz power line noise, keep 10 Hz and 100 Hz")
    
    b_bs, a_bs = signal.butter(4, [55, 65], fs=fs, btype='stop', analog=False)
    bandstop_output = signal.filtfilt(b_bs, a_bs, signal_input)
    
    save_data("filters", "transform_bandstop_input.txt", signal_input)
    save_data("filters", "transform_bandstop_output.txt", bandstop_output)
    
    print("\n" + "=" * 70)
    print("Transform test data generation complete!")
    print("=" * 70)
    print("\nFiles created in datasets/filters/:")
    print("  - transform_highpass_input.txt / output.txt")
    print("  - transform_bandpass_input.txt / output.txt")
    print("  - transform_bandstop_input.txt / output.txt")
    print("\nThese files will be used by FilterTransformTest.java")

if __name__ == "__main__":
    generate_transform_test_data()
