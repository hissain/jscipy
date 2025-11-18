import numpy as np
from scipy.fft import fft, rfft, ifft, irfft
import os

def generate_fft_test_data(test_id, num_samples):
    """
    Generates a test signal and computes its FFT, RFFT, IFFT, and IRFFT.
    """
    
    # Generate a sample signal
    t = np.linspace(0, 1, num_samples, False)
    signal_data = np.sin(2 * np.pi * 10 * t) + 0.5 * np.sin(2 * np.pi * 20 * t)
    
    # Compute FFT
    fft_result = fft(signal_data)
    
    # Compute RFFT
    rfft_result = rfft(signal_data)

    # Compute IFFT
    ifft_result = ifft(fft_result)

    # Compute IRFFT
    irfft_result = irfft(rfft_result)
    
    # Create output directory if it doesn't exist
    output_dir = "datasets"
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save the data
    np.savetxt(os.path.join(output_dir, f"fft_input_{test_id}.txt"), signal_data, fmt='%.18e')
    
    # Save complex FFT result as two columns (real, imag)
    np.savetxt(os.path.join(output_dir, f"fft_output_{test_id}.txt"), np.column_stack((fft_result.real, fft_result.imag)), fmt='%.18e')
    
    # Save complex RFFT result as two columns (real, imag)
    np.savetxt(os.path.join(output_dir, f"rfft_output_{test_id}.txt"), np.column_stack((rfft_result.real, rfft_result.imag)), fmt='%.18e')

    # Save complex IFFT result as two columns (real, imag)
    np.savetxt(os.path.join(output_dir, f"ifft_output_{test_id}.txt"), np.column_stack((ifft_result.real, ifft_result.imag)), fmt='%.18e')

    # Save real IRFFT result
    np.savetxt(os.path.join(output_dir, f"irfft_output_{test_id}.txt"), irfft_result, fmt='%.18e')
    
    print(f"Generated test data for FFT with num_samples={num_samples}")

if __name__ == "__main__":
    generate_fft_test_data(1, 64)
    generate_fft_test_data(2, 128)
