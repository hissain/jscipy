import numpy as np
from scipy.interpolate import CubicSpline
import os

def generate_interpolation_test_data(test_id, num_samples, num_new_samples):
    """
    Generates a test signal and interpolates it using linear and natural cubic methods.
    """
    
    # Generate a sample signal
    x = np.linspace(0, 10, num_samples)
    y = np.sin(x)
    
    # Generate new x-coordinates for interpolation
    new_x = np.linspace(0, 10, num_new_samples)
    
    # Linear interpolation
    linear_y = np.interp(new_x, x, y)
    
    # Cubic interpolation with natural boundary conditions
    cs = CubicSpline(x, y, bc_type='natural')
    cubic_y = cs(new_x)
    
    # Create output directory if it doesn't exist
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/interpolation")
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save the data
    # Save the data
    with open(os.path.join(output_dir, f"interpolation_input_x_{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, x, fmt='%.18e')
    with open(os.path.join(output_dir, f"interpolation_input_y_{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, y, fmt='%.18e')
    with open(os.path.join(output_dir, f"interpolation_input_new_x_{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, new_x, fmt='%.18e')
    with open(os.path.join(output_dir, f"interpolation_output_linear_{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, linear_y, fmt='%.18e')
    with open(os.path.join(output_dir, f"interpolation_output_cubic_{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, cubic_y, fmt='%.18e')
    
    print(f"Generated test data for interpolation with num_samples={num_samples}, num_new_samples={num_new_samples}")

if __name__ == "__main__":
    generate_interpolation_test_data(1, 5, 20)
    generate_interpolation_test_data(2, 10, 40)
