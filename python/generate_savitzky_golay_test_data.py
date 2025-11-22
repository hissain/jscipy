import numpy as np
from scipy import signal
import os

def generate_savgol_data(test_name, data, window_length, polyorder, deriv=0, delta=1.0):
    filtered_data = signal.savgol_filter(data, window_length, polyorder, deriv=deriv, delta=delta, mode='interp')
    
    script_dir = os.path.dirname(__file__)
    datasets_dir = os.path.abspath(os.path.join(script_dir, '../datasets'))
    
    input_filename = f'{test_name}_input.txt'
    output_filename = f'{test_name}_output.txt'
    
    os.makedirs(datasets_dir, exist_ok=True)
    np.savetxt(os.path.join(datasets_dir, input_filename), data)
    np.savetxt(os.path.join(datasets_dir, output_filename), filtered_data)
    print(f"Generated test data for {test_name}")

if __name__ == '__main__':
    # Test 1: Smoothing (deriv=0)
    # Generate noisy signal
    x = np.linspace(0, 2*np.pi, 100)
    y = np.sin(x) + np.random.normal(0, 0.1, 100)
    generate_savgol_data('savitzky_golay_smoothing', y, 11, 3)

    # Test 2: Differentiation (deriv=1)
    y2 = np.sin(x) # Clean signal for deriv check
    generate_savgol_data('savitzky_golay_differentiation', y2, 11, 3, deriv=1)
