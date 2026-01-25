import numpy as np
from scipy import signal
import os

# Create datasets directory if it doesn't exist
if not os.path.exists('datasets'):
    os.makedirs(os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets"))

# --- medfilt ---
# 1D median filter
np.random.seed(0)
x = np.linspace(0, 10, 101)
y = np.sin(x) + np.random.randn(len(x)) * 0.1
medfilt_y = signal.medfilt(y, kernel_size=3)

base_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets')

# Medfilt output
medfilt_dir = os.path.join(base_dir, 'medfilt')
os.makedirs(medfilt_dir, exist_ok=True)
with open(os.path.join(medfilt_dir, 'medfilt_input.txt'), 'w', newline='\n') as f:
    np.savetxt(f, y)
with open(os.path.join(medfilt_dir, 'medfilt_kernel.txt'), 'w', newline='\n') as f:
    np.savetxt(f, [3])
with open(os.path.join(medfilt_dir, 'medfilt_output.txt'), 'w', newline='\n') as f:
    np.savetxt(f, medfilt_y)

# --- convolve ---
# 1D convolution
sig = np.repeat([0., 1., 0.], 100)
win = signal.windows.hann(50)
filtered = signal.convolve(sig, win, mode='same')

# Convolve output
convolve_dir = os.path.join(base_dir, 'convolve')
os.makedirs(convolve_dir, exist_ok=True)
with open(os.path.join(convolve_dir, 'convolve_input_signal.txt'), 'w', newline='\n') as f:
    np.savetxt(f, sig)
with open(os.path.join(convolve_dir, 'convolve_input_window.txt'), 'w', newline='\n') as f:
    np.savetxt(f, win)
with open(os.path.join(convolve_dir, 'convolve_output.txt'), 'w', newline='\n') as f:
    np.savetxt(f, filtered)

print("Test data for medfilt and convolve generated successfully.")
