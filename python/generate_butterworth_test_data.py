import numpy as np
from scipy import signal
import os

def generate_butterworth_test_data(test_id, order, cutoff, sample_rate, num_samples):
    """
    Generates a test signal and applies a Butterworth filter.
    """
    
    # Generate a sample signal (e.g., a sine wave with some noise)
    t = np.linspace(0, 1, num_samples, False)
    signal_data = np.sin(2 * np.pi * 10 * t) + 0.5 * np.random.randn(num_samples)
    
    # Create a Butterworth filter
    b, a = signal.butter(order, cutoff, fs=sample_rate, btype='low', analog=False)
    
    # Apply the filter
    filtered_signal = signal.filtfilt(b, a, signal_data)
    
    # Create output directory if it doesn't exist
    output_dir = "datasets"
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save the data
    np.savetxt(os.path.join(output_dir, f"butterworth_input{test_id}.txt"), signal_data, fmt='%.18e')
    np.savetxt(os.path.join(output_dir, f"butterworth_output{test_id}.txt"), filtered_signal, fmt='%.18e')
    
    print(f"Generated test data for Butterworth filter with order={order}, cutoff={cutoff}")

if __name__ == "__main__":
    sample_rate = 250
    num_samples = 500
    
    generate_butterworth_test_data(1, 2, 20, sample_rate, num_samples)
    generate_butterworth_test_data(2, 3, 20, sample_rate, num_samples)
    generate_butterworth_test_data(3, 4, 20, sample_rate, num_samples)
