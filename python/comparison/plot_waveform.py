import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

DIR = 'datasets/waveform/'

def read(name):
    with open(os.path.join(DIR, name), 'r') as f:
        return np.array([float(line.strip()) for line in f if line.strip()])

def plot_waveform_comparison():
    t = read('t.txt')
    t_rad = read('t_rad.txt')
    t_gauss = read('t_gauss.txt')

    fig, axes = plt.subplots(5, 2, figsize=(14, 18))
    fig.suptitle('jSciPy vs SciPy — Waveform Generation', fontsize=16, fontweight='bold')

    panels = [
        # (row, col, title, t_key, scipy_file, java_file)
        (0, 0, 'Chirp (linear)', t, 'chirp_linear.txt',       'chirp_linear_java.txt'),
        (0, 1, 'Chirp (logarithmic)', t, 'chirp_logarithmic.txt', 'chirp_logarithmic_java.txt'),
        (1, 0, 'Chirp (quadratic)', t, 'chirp_quadratic.txt',  'chirp_quadratic_java.txt'),
        (1, 1, 'Chirp (hyperbolic)', t, 'chirp_hyperbolic.txt', 'chirp_hyperbolic_java.txt'),
        (2, 0, 'Square (duty=0.5)', t_rad, 'square_duty05.txt',  'square_duty05_java.txt'),
        (2, 1, 'Square (duty=0.25)', t_rad, 'square_duty025.txt', 'square_duty025_java.txt'),
        (3, 0, 'Sawtooth (width=1)', t_rad, 'sawtooth_width1.txt', 'sawtooth_width1_java.txt'),
        (3, 1, 'Sawtooth (width=0.5)', t_rad, 'sawtooth_width05.txt', 'sawtooth_width05_java.txt'),
        (4, 0, 'Gausspulse (fc=1000)', t_gauss, 'gausspulse_fc1000_bw05.txt', 'gausspulse_fc1000_bw05_java.txt'),
        (4, 1, 'Gausspulse (fc=500)',  t_gauss, 'gausspulse_fc500_bw03.txt',  'gausspulse_fc500_bw03_java.txt'),
    ]

    for row, col, title, t_arr, scipy_file, java_file in panels:
        ax = axes[row][col]
        scipy_out = read(scipy_file)
        java_file_path = os.path.join(DIR, java_file)
        if not os.path.exists(java_file_path):
            ax.set_title(f'{title}\n(Java output missing)')
            continue
        java_out = read(java_file)

        # For chirp/gauss use first 200 pts for clarity; square/sawtooth show all
        n = min(200, len(t_arr)) if 'chirp' in title.lower() or 'gauss' in title.lower() else len(t_arr)
        ax.plot(t_arr[:n], scipy_out[:n], label='SciPy', linewidth=2.0, alpha=0.85)
        ax.plot(t_arr[:n], java_out[:n], label='jSciPy', linestyle='--', linewidth=1.5)
        ax.set_title(title)
        ax.legend(fontsize=9)

    plt.tight_layout()
    style_utils.save_plot(fig, 'waveform/waveform_comparison.png')
    plt.close(fig)
    print('Waveform comparison plot saved.')

if __name__ == '__main__':
    plot_waveform_comparison()
