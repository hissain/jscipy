import numpy as np
from scipy import signal
import os

def generate_filter_test_data(filter_type, test_id, order, sample_rate, num_samples, **kwargs):
    # Generate signal
    t = np.linspace(0, 1, num_samples, False)
    # A mix of 5Hz (pass) and 50Hz (stop) with some noise
    signal_data = np.sin(2 * np.pi * 5 * t) + 0.5 * np.sin(2 * np.pi * 50 * t)

    b, a = None, None
    cutoff = kwargs.get('cutoff')
    
    if filter_type == 'cheby1':
        # rp is ripple in passband (decibels)
        rp = kwargs.get('rp', 1)
        b, a = signal.cheby1(order, rp, cutoff, fs=sample_rate, btype='low', analog=False)
    elif filter_type == 'cheby2':
        # rs is attenuation in stopband (decibels)
        rs = kwargs.get('rs', 20)
        b, a = signal.cheby2(order, rs, cutoff, fs=sample_rate, btype='low', analog=False)
    elif filter_type == 'ellip':
        # rp passband ripple, rs stopband attenuation
        rp = kwargs.get('rp', 1)
        rs = kwargs.get('rs', 20)
        b, a = signal.ellip(order, rp, rs, cutoff, fs=sample_rate, btype='low', analog=False)

    filtered_signal = signal.filtfilt(b, a, signal_data)

    output_dir_base = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets")
    
    subdir_map = {
        'cheby1': 'chebyshev',
        'cheby2': 'chebyshev',
        'ellip': 'elliptic'
    }
    subdir = subdir_map.get(filter_type, filter_type)
    output_dir = os.path.join(output_dir_base, subdir)

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    prefix = f"{filter_type}_"
    with open(os.path.join(output_dir, f"{prefix}input{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, signal_data, fmt='%.18e')
    with open(os.path.join(output_dir, f"{prefix}output{test_id}.txt"), 'w', newline='\n') as f:
        np.savetxt(f, filtered_signal, fmt='%.18e')
    print(f"Generated {filter_type} test data {test_id}")

if __name__ == "__main__":
    sr = 250
    n = 100
    cutoff = 20

    # Cheby1: order 4, rp 1dB
    generate_filter_test_data('cheby1', 1, 4, sr, n, cutoff=cutoff, rp=1)
    
    # Cheby2: order 4, rs 20dB
    generate_filter_test_data('cheby2', 1, 4, sr, n, cutoff=cutoff, rs=20)

    # Ellip: order 4, rp 1dB, rs 20dB
    generate_filter_test_data('ellip', 1, 4, sr, n, cutoff=cutoff, rp=1, rs=20)
