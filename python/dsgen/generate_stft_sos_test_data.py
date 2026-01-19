"""
Generate test data for STFT/ISTFT and SOS Filtering.
Golden Master approach using scipy.
"""
import numpy as np
import scipy.signal as signal
import os

output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets")
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

print("Generating STFT/SOS test data...")

# ============================================================================
# STFT / ISTFT
# ============================================================================
# Parameters matching Java defaults: nperseg=256, noverlap=128, Hann window
fs = 1000.0
t = np.linspace(0, 1, int(fs), endpoint=False)
x = np.sin(2 * np.pi * 50 * t) + 0.5 * np.sin(2 * np.pi * 120 * t)

np.savetxt(os.path.join(output_dir, "stft_input.txt"), x)

# STFT
f, t_spec, Zxx = signal.stft(x, fs=1.0, window='hann', nperseg=256, noverlap=128, nfft=256, boundary='zeros', padded=True)

# Note: scipy.signal.stft returns Zxx with shape (n_freqs, n_frames)
# Java implementation should match this shape and values
np.savetxt(os.path.join(output_dir, "stft_output_real.txt"), Zxx.real.flatten())
np.savetxt(os.path.join(output_dir, "stft_output_imag.txt"), Zxx.imag.flatten())

# Save dimensions
with open(os.path.join(output_dir, "stft_dims.txt"), 'w') as f:
    f.write(f"{Zxx.shape[0]}\n{Zxx.shape[1]}\n")

# ISTFT
t_out, x_rec = signal.istft(Zxx, fs=1.0, window='hann', nperseg=256, noverlap=128, nfft=256, boundary='zeros')
# Note: istft might return slightly different length depending on padding, usually trimmed to match input if known,
# or full length. Java implementation returns partial match. 
# We'll save the full reconstructed signal.
np.savetxt(os.path.join(output_dir, "istft_output.txt"), x_rec)


# ============================================================================
# SOS Filtering
# ============================================================================
# Design a Butterworth filter in SOS format
# Lowpass, Order 4, Cutoff 0.2 (normalized 0-1, so 0.2*Nyquist)
sos = signal.butter(4, 0.2, output='sos')

np.savetxt(os.path.join(output_dir, "sos_coeffs.txt"), sos.flatten()) # Flattened 2D array
with open(os.path.join(output_dir, "sos_dims.txt"), 'w') as f:
    f.write(f"{sos.shape[0]}\n{sos.shape[1]}\n")

# Filter the signal
# Using same input x
sos_filtered = signal.sosfilt(sos, x)

np.savetxt(os.path.join(output_dir, "sos_filtered_output.txt"), sos_filtered)

print("Done generating STFT and SOS data.")
