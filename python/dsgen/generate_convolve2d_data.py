import numpy as np
from scipy import signal
import os

def save_matrix(filename, matrix):
    with open(filename, 'w') as f:
        rows, cols = matrix.shape
        f.write(f"{rows} {cols}\n")
        np.savetxt(f, matrix, fmt='%.18e')

def generate_data():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    datasets_dir = os.path.join(script_dir, '../../datasets')
    os.makedirs(datasets_dir, exist_ok=True)
    
    # Test Case 1: Small Inputs, Mode FULL
    in1 = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]])
    in2 = np.array([[1, 0], [0, 1]])
    out_full = signal.convolve2d(in1, in2, mode='full')
    save_matrix(os.path.join(datasets_dir, "conv2d_in1_1.txt"), in1)
    save_matrix(os.path.join(datasets_dir, "conv2d_in2_1.txt"), in2)
    save_matrix(os.path.join(datasets_dir, "conv2d_out_full_1.txt"), out_full)
    
    # Test Case 2: Mode SAME
    out_same = signal.convolve2d(in1, in2, mode='same')
    save_matrix(os.path.join(datasets_dir, "conv2d_out_same_1.txt"), out_same)
    
    # Test Case 3: Mode VALID
    out_valid = signal.convolve2d(in1, in2, mode='valid')
    save_matrix(os.path.join(datasets_dir, "conv2d_out_valid_1.txt"), out_valid)

    # Test Case 4: Larger Random Inputs
    np.random.seed(42)
    in3 = np.random.rand(5, 5)
    in4 = np.random.rand(3, 3)
    out_full_2 = signal.convolve2d(in3, in4, mode='full')
    save_matrix(os.path.join(datasets_dir, "conv2d_in1_2.txt"), in3)
    save_matrix(os.path.join(datasets_dir, "conv2d_in2_2.txt"), in4)
    save_matrix(os.path.join(datasets_dir, "conv2d_out_full_2.txt"), out_full_2)

    print(f"Convolve2d test data generated in '{datasets_dir}'")

if __name__ == "__main__":
    generate_data()
