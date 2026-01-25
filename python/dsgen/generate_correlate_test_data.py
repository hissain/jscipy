import numpy as np
from scipy import signal
import os

def save_data(test_id, input1, input2, expected_output):
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/correlate")
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    with open(os.path.join(output_dir, f"{test_id}_input1.txt"), 'w', newline='\n') as f:
        np.savetxt(f, input1, fmt='%.18e')
    with open(os.path.join(output_dir, f"{test_id}_input2.txt"), 'w', newline='\n') as f:
        np.savetxt(f, input2, fmt='%.18e')
    with open(os.path.join(output_dir, f"{test_id}_output.txt"), 'w', newline='\n') as f:
        np.savetxt(f, expected_output, fmt='%.18e')

if __name__ == "__main__":
    np.random.seed(42)

    # 1. Basic small inputs
    x1 = np.array([1.0, 2.0, 3.0])
    x2 = np.array([0.0, 1.0, 0.5])

    # Mode: full
    out_full = signal.correlate(x1, x2, mode='full')
    save_data("correlate_basic_full", x1, x2, out_full)

    # Mode: same
    out_same = signal.correlate(x1, x2, mode='same')
    save_data("correlate_basic_same", x1, x2, out_same)

    # Mode: valid
    out_valid = signal.correlate(x1, x2, mode='valid')
    save_data("correlate_basic_valid", x1, x2, out_valid)

    # 2. Random inputs
    r1 = np.random.rand(50)
    r2 = np.random.rand(20)

    # Mode: full
    out_rand_full = signal.correlate(r1, r2, mode='full')
    save_data("correlate_random_full", r1, r2, out_rand_full)
    
    # Mode: same
    out_rand_same = signal.correlate(r1, r2, mode='same')
    save_data("correlate_random_same", r1, r2, out_rand_same)
    
    # Mode: valid
    out_rand_valid = signal.correlate(r1, r2, mode='valid')
    save_data("correlate_random_valid", r1, r2, out_rand_valid)
