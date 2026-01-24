import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils
from scipy import signal

style_utils.apply_style()

DATA_DIR = "datasets/fir/"

def read_data(filename):
    path = os.path.join(DATA_DIR, filename)
    if not os.path.exists(path):
        return None
    with open(path, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_freq_response(ax, taps, label, linestyle='-'):
    w, h = signal.freqz(taps, fs=1000.0)
    ax.plot(w, 20 * np.log10(abs(h) + 1e-15), linestyle=linestyle, label=label)

def plot_fir_comparison():
    # Load Lowpass
    scipy_lp = read_data("fir_lowpass_31_100.txt")
    java_lp = read_data("fir_lowpass_31_java.txt")
    
    # Load Bandpass
    scipy_bp = read_data("fir_bandpass_51_100_200.txt")
    java_bp = read_data("fir_bandpass_51_java.txt")
    
    if java_lp is None or java_bp is None:
        print("Java output files not found. Run FIRTest first.")
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 10))
    
    # --- Lowpass ---
    # Impulse
    axes[0, 0].plot(scipy_lp, 'o-', label='SciPy Lowpass', alpha=0.7)
    axes[0, 0].plot(java_lp, 'x--', label='Java Lowpass', alpha=0.9)
    axes[0, 0].set_title('Lowpass Filter Coefficients (Impulse)')
    axes[0, 0].grid(True, alpha=0.3)
    axes[0, 0].legend()
    
    # Frequency Response
    plot_freq_response(axes[0, 1], scipy_lp, 'SciPy', '-')
    plot_freq_response(axes[0, 1], java_lp, 'Java', '--')
    axes[0, 1].set_title('Lowpass Frequency Response')
    axes[0, 1].set_ylabel('Magnitude (dB)')
    axes[0, 1].set_xlabel('Frequency (Hz)')
    axes[0, 1].grid(True, alpha=0.3)
    axes[0, 1].legend()
    
    # --- Bandpass ---
    # Impulse
    axes[1, 0].plot(scipy_bp, 'o-', label='SciPy Highpass', alpha=0.7) # Ah, bandpass file
    axes[1, 0].plot(java_bp, 'x--', label='Java Highpass', alpha=0.9)
    axes[1, 0].set_title('Bandpass Filter Coefficients')
    axes[1, 0].grid(True, alpha=0.3)
    axes[1, 0].legend()
    
    # Frequency Response
    plot_freq_response(axes[1, 1], scipy_bp, 'SciPy', '-')
    plot_freq_response(axes[1, 1], java_bp, 'Java', '--')
    axes[1, 1].set_title('Bandpass Frequency Response')
    axes[1, 1].set_ylabel('Magnitude (dB)')
    axes[1, 1].set_xlabel('Frequency (Hz)')
    axes[1, 1].grid(True, alpha=0.3)
    axes[1, 1].legend()
    
    plt.suptitle('FIR Filter Comparison (firwin)', fontsize=14)
    plt.tight_layout()
    
    os.makedirs('python/figs/fir', exist_ok=True)
    style_utils.save_plot(fig, "fir/fir_comparison.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_fir_comparison()
    print("FIR comparison plot generated.")
