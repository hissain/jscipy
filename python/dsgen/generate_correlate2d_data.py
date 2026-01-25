import numpy as np
from scipy import signal
import os

def save_matrix(filename, matrix):
    """Save a 2D matrix with dimensions on the first line."""
    with open(filename, 'w', newline='\n') as f:
        rows, cols = matrix.shape
        f.write(f"{rows} {cols}\n")
        np.savetxt(f, matrix, fmt='%.18e')

def generate_data():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    datasets_dir = os.path.join(script_dir, '../../datasets/correlate2d')
    os.makedirs(datasets_dir, exist_ok=True)
    
    # Test Case 1: Small deterministic inputs for all modes
    print("Generating Test Case 1: Small deterministic inputs")
    in1 = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]], dtype=float)
    in2 = np.array([[1, 0], [0, 1]], dtype=float)
    
    # Mode: FULL
    out_full = signal.correlate2d(in1, in2, mode='full')
    save_matrix(os.path.join(datasets_dir, "correlate2d_in1_1.txt"), in1)
    save_matrix(os.path.join(datasets_dir, "correlate2d_in2_1.txt"), in2)
    save_matrix(os.path.join(datasets_dir, "correlate2d_out_full_1.txt"), out_full)
    print(f"  FULL mode: in1 {in1.shape} * in2 {in2.shape} -> out {out_full.shape}")
    
    # Mode: SAME
    out_same = signal.correlate2d(in1, in2, mode='same')
    save_matrix(os.path.join(datasets_dir, "correlate2d_out_same_1.txt"), out_same)
    print(f"  SAME mode: in1 {in1.shape} * in2 {in2.shape} -> out {out_same.shape}")
    
    # Mode: VALID
    out_valid = signal.correlate2d(in1, in2, mode='valid')
    save_matrix(os.path.join(datasets_dir, "correlate2d_out_valid_1.txt"), out_valid)
    print(f"  VALID mode: in1 {in1.shape} * in2 {in2.shape} -> out {out_valid.shape}")
    
    # Test Case 2: Larger random inputs (FULL mode only for comprehensive test)
    print("\nGenerating Test Case 2: Larger random inputs")
    np.random.seed(42)
    in3 = np.random.rand(6, 6)
    in4 = np.random.rand(3, 3)
    out_full_2 = signal.correlate2d(in3, in4, mode='full')
    save_matrix(os.path.join(datasets_dir, "correlate2d_in1_2.txt"), in3)
    save_matrix(os.path.join(datasets_dir, "correlate2d_in2_2.txt"), in4)
    save_matrix(os.path.join(datasets_dir, "correlate2d_out_full_2.txt"), out_full_2)
    print(f"  FULL mode: in1 {in3.shape} * in2 {in4.shape} -> out {out_full_2.shape}")
    
    # Test Case 3: Different sizes for SAME mode
    print("\nGenerating Test Case 3: Different sizes for SAME mode")
    in5 = np.random.rand(5, 7)
    in6 = np.random.rand(3, 3)
    out_same_3 = signal.correlate2d(in5, in6, mode='same')
    save_matrix(os.path.join(datasets_dir, "correlate2d_in1_3.txt"), in5)
    save_matrix(os.path.join(datasets_dir, "correlate2d_in2_3.txt"), in6)
    save_matrix(os.path.join(datasets_dir, "correlate2d_out_same_3.txt"), out_same_3)
    print(f"  SAME mode: in1 {in5.shape} * in2 {in6.shape} -> out {out_same_3.shape}")
    
    print(f"\n[SUCCESS] Correlate2d test data generated in '{datasets_dir}'")

if __name__ == "__main__":
    generate_data()
