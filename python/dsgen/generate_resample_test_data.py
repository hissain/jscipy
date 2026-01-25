import numpy as np
from scipy import signal
import os

def generate_resample_data(test_name, data, num):
    resampled_data = signal.resample(data, num)
    
    script_dir = os.path.dirname(__file__)
    datasets_dir = os.path.abspath(os.path.join(script_dir, '../../datasets/resample'))
    
    input_filename = f'{test_name}_input.txt'
    output_filename = f'{test_name}_output.txt'
    
    os.makedirs(datasets_dir, exist_ok=True)
    with open(os.path.join(datasets_dir, input_filename), 'w', newline='\n') as f:
        np.savetxt(f, data)
    with open(os.path.join(datasets_dir, output_filename), 'w', newline='\n') as f:
        np.savetxt(f, resampled_data)

if __name__ == '__main__':
    # Test case 1: Upsampling
    x1 = np.linspace(0, 10, 20, endpoint=False)
    y1 = np.cos(-x1**2/6.0)
    generate_resample_data('resample_1', y1, 50)
    
    # Test case 2: Downsampling
    x2 = np.linspace(0, 10, 100, endpoint=False)
    y2 = np.sin(x2)
    generate_resample_data('resample_2', y2, 30)
