import numpy as np
import scipy.signal
import os

def generate_test_data(signal_length, test_id, distance, height=None, prominence=None):
    """
    Generates a test signal with specified parameters and saves the peaks.
    """
    
    # Generate random signal
    np.random.seed(test_id)
    signal = np.random.rand(signal_length)
    
    # Find peaks using scipy.signal.find_peaks
    peak_indices = scipy.signal.find_peaks(signal, distance=distance, height=height, prominence=prominence)[0]
    
    # Create output directory if it doesn't exist
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets")
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save signal to file
    signal_filename = os.path.join(output_dir, f"findpeaks_input{test_id}.txt")
    np.savetxt(signal_filename, signal, fmt='%.18e')
    
    # Save peak indices to file
    peak_indices_filename = os.path.join(output_dir, f"findpeaks_output{test_id}.txt")
    np.savetxt(peak_indices_filename, peak_indices, fmt='%d')
    
    print(f"Generated test data with {len(peak_indices)} peaks and saved to:\n- Signal: {signal_filename}\n- Peak indices: {peak_indices_filename}")


if __name__ == "__main__":
    # Test data parameters
    signal_length = 500
    
    # Generate test datasets
    generate_test_data(signal_length=signal_length, test_id=1, distance=1)
    generate_test_data(signal_length=signal_length, test_id=2, distance=20)
    generate_test_data(signal_length=signal_length, test_id=3, distance=30)
    generate_test_data(signal_length=signal_length, test_id=4, distance=20)
    generate_test_data(signal_length=signal_length, test_id=5, distance=70)
    generate_test_data(signal_length=signal_length, test_id=6, distance=20, height=0.7)
    generate_test_data(signal_length=signal_length, test_id=7, distance=20, prominence=0.7)
    generate_test_data(signal_length=signal_length, test_id=8, distance=20, height=0.7, prominence=0.7)