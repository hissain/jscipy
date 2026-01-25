import numpy as np
import scipy.fft
import os

# Set seed for reproducibility
np.random.seed(42)

DATA_DIR = "datasets/dct"
os.makedirs(DATA_DIR, exist_ok=True)

def save_data(filename, data):
    filepath = os.path.join(DATA_DIR, filename)
    with open(filepath, 'w', newline='\n') as f:
        np.savetxt(f, data, fmt='%.16e')
    print(f"Saved {filepath}")

def generate_dct_test_data():
    # Case 1: Simple known signal (Length 8, Power of 2)
    x1 = np.array([1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0])
    y1 = scipy.fft.dct(x1, type=2, norm=None)
    z1 = scipy.fft.idct(y1, type=2, norm=None) # Should match x1
    
    save_data("dct_basic_input.txt", x1)
    save_data("dct_basic_output.txt", y1)
    save_data("dct_basic_idct.txt", z1)

    # Case 2: Random signal (Length 50, even)
    x2 = np.random.randn(50)
    y2 = scipy.fft.dct(x2, type=2, norm=None)
    z2 = scipy.fft.idct(y2, type=2, norm=None)
    
    save_data("dct_random_even_input.txt", x2)
    save_data("dct_random_even_output.txt", y2)
    save_data("dct_random_even_idct.txt", z2)
    
    # Case 3: Random signal (Length 33, odd)
    x3 = np.random.randn(33)
    y3 = scipy.fft.dct(x3, type=2, norm=None)
    z3 = scipy.fft.idct(y3, type=2, norm=None)
    
    save_data("dct_random_odd_input.txt", x3)
    save_data("dct_random_odd_output.txt", y3)
    save_data("dct_random_odd_idct.txt", z3)

    # Case 4: Ortho norm (Length 16)
    x4 = np.random.randn(16)
    y4 = scipy.fft.dct(x4, type=2, norm='ortho')
    z4 = scipy.fft.idct(y4, type=2, norm='ortho')
    
    save_data("dct_ortho_input.txt", x4)
    save_data("dct_ortho_output.txt", y4)
    save_data("dct_ortho_idct.txt", z4)

    print("DCT & IDCT test data generated.")

if __name__ == "__main__":
    generate_dct_test_data()
