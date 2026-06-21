import numpy as np
from scipy import signal
import os

base_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets')
out_dir = os.path.join(base_dir, 'waveform')
os.makedirs(out_dir, exist_ok=True)

def save(name, arr):
    with open(os.path.join(out_dir, name), 'w', newline='\n') as f:
        np.savetxt(f, arr)

# Shared time array for chirp, square, sawtooth
fs = 1000.0
t = np.linspace(0, 1, int(fs), endpoint=False)
save('t.txt', t)

# --- chirp ---
for method in ['linear', 'quadratic', 'logarithmic', 'hyperbolic']:
    y = signal.chirp(t, f0=1.0, t1=1.0, f1=100.0, method=method)
    save(f'chirp_{method}.txt', y)

# --- square ---
t_rad = 2 * np.pi * np.linspace(0, 3, 600, endpoint=False)  # 3 full cycles
save('t_rad.txt', t_rad)
save('square_duty05.txt', signal.square(t_rad, duty=0.5))
save('square_duty025.txt', signal.square(t_rad, duty=0.25))
save('square_duty075.txt', signal.square(t_rad, duty=0.75))

# --- sawtooth ---
save('sawtooth_width1.txt', signal.sawtooth(t_rad, width=1.0))
save('sawtooth_width05.txt', signal.sawtooth(t_rad, width=0.5))
save('sawtooth_width0.txt', signal.sawtooth(t_rad, width=0.0))

# --- gausspulse ---
t_gauss = np.linspace(-1, 1, 200, endpoint=False)
save('t_gauss.txt', t_gauss)
save('gausspulse_fc1000_bw05.txt', signal.gausspulse(t_gauss, fc=1000, bw=0.5))
save('gausspulse_fc500_bw03.txt', signal.gausspulse(t_gauss, fc=500, bw=0.3))

# --- unit_impulse ---
save('unit_impulse_n100_idx0.txt', signal.unit_impulse(100, idx=0))
save('unit_impulse_n100_mid.txt', signal.unit_impulse(100, idx='mid'))
save('unit_impulse_n100_idx25.txt', signal.unit_impulse(100, idx=25))

print("Waveform test data generated successfully.")
