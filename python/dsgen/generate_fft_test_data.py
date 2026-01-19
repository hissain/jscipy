import numpy as np
from scipy.fft import fft, rfft, ifft, irfft
from scipy.signal import stft, istft
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
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/fft")
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
    
    print(f"Generated FFT test data {test_id} with num_samples={num_samples}")

def generate_stft_test_data(test_id, num_samples, nperseg=256, noverlap=None, nfft=None):
    """
    Generates a test signal and computes its STFT and ISTFT.
    Matches scipy.signal.stft default parameters.
    """
    
    if noverlap is None:
        noverlap = nperseg // 2
    if nfft is None:
        nfft = nperseg
    
    # Generate a more complex test signal with multiple frequencies
    t = np.linspace(0, 2, num_samples, False)
    signal_data = (np.sin(2 * np.pi * 10 * t) + 
                   0.5 * np.sin(2 * np.pi * 25 * t) + 
                   0.3 * np.sin(2 * np.pi * 50 * t))
    
    # Compute STFT with default parameters matching scipy
    # boundary='zeros' and padded=True are defaults
    f, t_stft, Zxx = stft(signal_data, 
                          nperseg=nperseg, 
                          noverlap=noverlap,
                          nfft=nfft,
                          boundary='zeros',
                          padded=True)
    
    # Compute ISTFT to verify round-trip
    t_istft, reconstructed = istft(Zxx, 
                                    nperseg=nperseg, 
                                    noverlap=noverlap,
                                    nfft=nfft,
                                    boundary='zeros')
    
    # Create output directory if it doesn't exist
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/fft")
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save input signal
    np.savetxt(os.path.join(output_dir, f"stft_input_{test_id}.txt"), 
               signal_data, fmt='%.18e')
    
    # Save STFT parameters
    with open(os.path.join(output_dir, f"stft_params_{test_id}.txt"), 'w') as f:
        f.write(f"nperseg={nperseg}\n")
        f.write(f"noverlap={noverlap}\n")
        f.write(f"nfft={nfft}\n")
        f.write(f"num_freq_bins={Zxx.shape[0]}\n")
        f.write(f"num_time_frames={Zxx.shape[1]}\n")
    
    # Save STFT result (frequency x time matrix)
    # Save as flattened array with dimensions
    stft_shape = Zxx.shape
    stft_flattened = Zxx.flatten('F')  # Flatten in Fortran order (column-major)
    
    # Save shape info
    with open(os.path.join(output_dir, f"stft_output_{test_id}_shape.txt"), 'w') as f:
        f.write(f"{stft_shape[0]} {stft_shape[1]}\n")
    
    # Save complex STFT data
    np.savetxt(os.path.join(output_dir, f"stft_output_{test_id}.txt"), 
               np.column_stack((stft_flattened.real, stft_flattened.imag)), 
               fmt='%.18e')
    
    # Save ISTFT result
    np.savetxt(os.path.join(output_dir, f"istft_output_{test_id}.txt"), 
               reconstructed, fmt='%.18e')
    
    # Calculate reconstruction error
    # Trim to match lengths if needed
    min_len = min(len(signal_data), len(reconstructed))
    rmse = np.sqrt(np.mean((signal_data[:min_len] - reconstructed[:min_len])**2))
    
    print(f"Generated STFT test data {test_id}:")
    print(f"  num_samples={num_samples}, nperseg={nperseg}, noverlap={noverlap}, nfft={nfft}")
    print(f"  STFT shape: {stft_shape} (freq x time)")
    print(f"  Original signal length: {len(signal_data)}")
    print(f"  Reconstructed length: {len(reconstructed)}")
    print(f"  Reconstruction RMSE: {rmse:.2e}")

if __name__ == "__main__":
    # Generate FFT test data
    print("=" * 60)
    print("Generating FFT test data...")
    print("=" * 60)
    generate_fft_test_data(1, 64)
    generate_fft_test_data(2, 128)
    
    # Generate STFT test data
    print("\n" + "=" * 60)
    print("Generating STFT test data...")
    print("=" * 60)
    
    # Test 1: Short signal with default parameters
    generate_stft_test_data(1, 512, nperseg=256, noverlap=128, nfft=256)
    
    # Test 2: Longer signal with default parameters
    generate_stft_test_data(2, 1024, nperseg=256, noverlap=128, nfft=256)
    
    # Test 3: Custom parameters - smaller window
    generate_stft_test_data(3, 800, nperseg=128, noverlap=64, nfft=128)
    
    # Test 4: Custom parameters - larger FFT size (zero-padded)
    generate_stft_test_data(4, 1000, nperseg=256, noverlap=192, nfft=512)
    
    print("\n" + "=" * 60)
    print("All test data generated successfully!")
    print("=" * 60)