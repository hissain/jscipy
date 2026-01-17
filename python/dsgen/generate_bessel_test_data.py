import numpy as np
import scipy.signal as signal
import os

def generate_data():
    # Parameters
    fs = 1000.0
    t = np.linspace(0, 1.0, int(fs))
    # Signal: Step function + sine wave
    x = np.heaviside(t - 0.5, 1) + 0.2 * np.sin(2 * np.pi * 50 * t)
    
    # Filter parameters
    cutoff = 10.0
    order = 4
    
    # SciPy Bessel Filter (default norm='phase')
    # Use sos for stability matching java logic
    sos = signal.bessel(order, cutoff, btype='low', fs=fs, output='sos', norm='phase')
    
    # Apply filter using filtfilt (zero-phase)
    y_scipy = signal.sosfiltfilt(sos, x)
    
    # Save data
    script_dir = os.path.dirname(os.path.abspath(__file__))
    datasets_dir = os.path.join(script_dir, '../../datasets')
    os.makedirs(datasets_dir, exist_ok=True)
    np.savetxt(os.path.join(datasets_dir, 'bessel_input.txt'), x)
    np.savetxt(os.path.join(datasets_dir, 'bessel_output_scipy.txt'), y_scipy)
    
    print("Bessel test data generated.")

if __name__ == "__main__":
    generate_data()
