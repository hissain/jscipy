import numpy as np
from scipy import fft
import os

def save_matrix(filename, matrix):
    rows, cols = matrix.shape
    with open(filename, 'w') as f:
        f.write(f"{rows} {cols}\n")
        np.savetxt(f, matrix, fmt='%.18e')

def generate_data():
    os.makedirs("test_data", exist_ok=True)
    
    np.random.seed(42)

    # 1. Test fft2 (Real Input)
    rows, cols = 5, 4
    input_real = np.random.rand(rows, cols)
    
    fft_result = fft.fft2(input_real)
    
    save_matrix("test_data/fft2_in.txt", input_real)
    save_matrix("test_data/fft2_out_real.txt", fft_result.real)
    save_matrix("test_data/fft2_out_imag.txt", fft_result.imag)
    
    # 2. Test ifft2 (Complex Input)
    rows, cols = 4, 3
    input_complex = np.random.rand(rows, cols) + 1j * np.random.rand(rows, cols)
    
    ifft_result = fft.ifft2(input_complex)
    
    save_matrix("test_data/ifft2_in_real.txt", input_complex.real)
    save_matrix("test_data/ifft2_in_imag.txt", input_complex.imag)
    save_matrix("test_data/ifft2_out_real.txt", ifft_result.real)
    save_matrix("test_data/ifft2_out_imag.txt", ifft_result.imag)
    
    print("FFT2 test data generated in 'test_data'")

if __name__ == "__main__":
    generate_data()
