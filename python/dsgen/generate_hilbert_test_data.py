import numpy as np
import scipy.signal as signal
import os

# Ensure the datasets directory exists
output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/hilbert")
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

# Dataset 1: Simple Sine Wave
# Analytic signal of cos(wt) is exp(iwt) = cos(wt) + i sin(wt).
# Analytic signal of sin(wt) is -i exp(iwt) = sin(wt) - i cos(wt).
t = np.linspace(0, 1, 20) # Small N for readibility if needed, but lets use typical size
N = 32
t = np.arange(N)
input_1 = np.cos(2 * np.pi * t / 10.0)

# Dataset 2: Chirp signal
N2 = 33 # Odd length
t2 = np.arange(N2)
input_2 = np.sin(2 * np.pi * t2 / 10.0)

# Save inputs
# Save inputs
with open(os.path.join(output_dir, "hilbert_input_1.txt"), 'w', newline='\n') as f:
    np.savetxt(f, input_1)
with open(os.path.join(output_dir, "hilbert_input_2.txt"), 'w', newline='\n') as f:
    np.savetxt(f, input_2)

# Apply hilbert
output_1 = signal.hilbert(input_1)
output_2 = signal.hilbert(input_2)

# Save outputs (Real and Imaginary parts separately or interleaving)
# I'll save as Real then Imaginary columns if possible, but existing readDataFile usually reads 1D.
# So I will save real part in one file, imaginary in another, or complex format?
# The library seems to use separate files or specific formats. 
# FFT tests use "fft_output_1.txt" but let's see how they are formatted.
# Looking at FFTTest.java, it reads expected output. 
# But wait, FFT returns Complex[]. 
# Let's check datasets/fft_output_1.txt format. 

# If I can't check, I'll define my own format.
# I'll save real part in _real.txt and imag part in _imag.txt

with open(os.path.join(output_dir, "hilbert_output_1_real.txt"), 'w', newline='\n') as f:
    np.savetxt(f, output_1.real)
with open(os.path.join(output_dir, "hilbert_output_1_imag.txt"), 'w', newline='\n') as f:
    np.savetxt(f, output_1.imag)

with open(os.path.join(output_dir, "hilbert_output_2_real.txt"), 'w', newline='\n') as f:
    np.savetxt(f, output_2.real)
with open(os.path.join(output_dir, "hilbert_output_2_imag.txt"), 'w', newline='\n') as f:
    np.savetxt(f, output_2.imag)

print("Hilbert test data generated.")
