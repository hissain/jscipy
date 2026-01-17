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

np.savetxt(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets', 'medfilt_input.txt'), y)
np.savetxt(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets', 'medfilt_kernel.txt'), [3])
np.savetxt(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets', 'medfilt_output.txt'), medfilt_y)

# --- convolve ---
# 1D convolution
sig = np.repeat([0., 1., 0.], 100)
win = signal.windows.hann(50)
filtered = signal.convolve(sig, win, mode='same') / sum(win)

np.savetxt(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets', 'convolve_input_signal.txt'), sig)
np.savetxt(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets', 'convolve_input_window.txt'), win)
np.savetxt(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets', 'convolve_output.txt'), filtered)

print("Test data for medfilt and convolve generated successfully.")
