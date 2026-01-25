import numpy as np
from scipy import signal
import os

def save_data_1d(test_id, input1, input2, expected_output):
    """Save 1D correlation data."""
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/correlate")
    os.makedirs(output_dir, exist_ok=True)
    
    with open(os.path.join(output_dir, f"{test_id}_input1.txt"), 'w', newline='\n') as f:
        np.savetxt(f, input1, fmt='%.18e')
    with open(os.path.join(output_dir, f"{test_id}_input2.txt"), 'w', newline='\n') as f:
        np.savetxt(f, input2, fmt='%.18e')
    with open(os.path.join(output_dir, f"{test_id}_output.txt"), 'w', newline='\n') as f:
        np.savetxt(f, expected_output, fmt='%.18e')

def save_matrix_2d(filename, matrix):
    """Save a 2D matrix with dimensions on the first line."""
    output_dir = os.path.dirname(filename)
    os.makedirs(output_dir, exist_ok=True)
    
    with open(filename, 'w', newline='\n') as f:
        rows, cols = matrix.shape
        f.write(f"{rows} {cols}\n")
        np.savetxt(f, matrix, fmt='%.18e')

def generate_1d_data():
    print("Generating 1D Correlation Data...")
    np.random.seed(42)

    # 1. Basic small inputs
    x1 = np.array([1.0, 2.0, 3.0])
    x2 = np.array([0.0, 1.0, 0.5])

    # Mode: full
    out_full = signal.correlate(x1, x2, mode='full')
    save_data_1d("correlate_basic_full", x1, x2, out_full)

    # Mode: same
    out_same = signal.correlate(x1, x2, mode='same')
    save_data_1d("correlate_basic_same", x1, x2, out_same)

    # Mode: valid
    out_valid = signal.correlate(x1, x2, mode='valid')
    save_data_1d("correlate_basic_valid", x1, x2, out_valid)

    # 2. Random inputs
    r1 = np.random.rand(50)
    r2 = np.random.rand(20)

    # Mode: full
    out_rand_full = signal.correlate(r1, r2, mode='full')
    save_data_1d("correlate_random_full", r1, r2, out_rand_full)
    
    # Mode: same
    out_rand_same = signal.correlate(r1, r2, mode='same')
    save_data_1d("correlate_random_same", r1, r2, out_rand_same)
    
    # Mode: valid
    out_rand_valid = signal.correlate(r1, r2, mode='valid')
    save_data_1d("correlate_random_valid", r1, r2, out_rand_valid)
    print("  [SUCCESS] 1D data generated.")

def generate_2d_data():
    print("\nGenerating 2D Correlation Data...")
    script_dir = os.path.dirname(os.path.abspath(__file__))
    datasets_dir = os.path.join(script_dir, '../../datasets/correlate2d')
    
    # Test Case 1: Small deterministic inputs for all modes
    in1 = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]], dtype=float)
    in2 = np.array([[1, 0], [0, 1]], dtype=float)
    
    # Mode: FULL
    out_full = signal.correlate2d(in1, in2, mode='full')
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_in1_1.txt"), in1)
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_in2_1.txt"), in2)
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_out_full_1.txt"), out_full)
    
    # Mode: SAME
    out_same = signal.correlate2d(in1, in2, mode='same')
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_out_same_1.txt"), out_same)
    
    # Mode: VALID
    out_valid = signal.correlate2d(in1, in2, mode='valid')
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_out_valid_1.txt"), out_valid)
    
    # Test Case 2: Larger random inputs (FULL mode only for comprehensive test)
    np.random.seed(42)
    in3 = np.random.rand(6, 6)
    in4 = np.random.rand(3, 3)
    out_full_2 = signal.correlate2d(in3, in4, mode='full')
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_in1_2.txt"), in3)
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_in2_2.txt"), in4)
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_out_full_2.txt"), out_full_2)
    
    # Test Case 3: Different sizes for SAME mode
    in5 = np.random.rand(5, 7)
    in6 = np.random.rand(3, 3)
    out_same_3 = signal.correlate2d(in5, in6, mode='same')
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_in1_3.txt"), in5)
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_in2_3.txt"), in6)
    save_matrix_2d(os.path.join(datasets_dir, "correlate2d_out_same_3.txt"), out_same_3)
    
    print("  [SUCCESS] 2D data generated.")

if __name__ == "__main__":
    generate_1d_data()
    generate_2d_data()
