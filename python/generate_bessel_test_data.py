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
    os.makedirs('python/data', exist_ok=True)
    np.savetxt('python/data/bessel_input.txt', x)
    np.savetxt('python/data/bessel_output_scipy.txt', y_scipy)
    
    print("Bessel test data generated.")

if __name__ == "__main__":
    generate_data()
