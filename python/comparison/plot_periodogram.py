import numpy as np
import matplotlib.pyplot as plt
from scipy import signal
import os

def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

def load_txt(filename):
    return np.loadtxt(filename)

def plot_comparison(theme='light'):
    # Set theme
    if theme == 'dark':
        plt.style.use('dark_background')
        scipy_color = '#00BFFF'  # Deep Sky Blue
        jscipy_color = '#FF6B6B'  # Light Red
        title_color = 'white'
    else:
        plt.style.use('default')
        scipy_color = '#1f77b4'  # Blue
        jscipy_color = '#ff7f0e'  # Orange
        title_color = 'black'

    # Load data
    data_dir = "datasets/periodogram"
    input_signal = load_txt(f"{data_dir}/periodogram_input.txt")
    scipy_freq = load_txt(f"{data_dir}/periodogram_frequencies.txt")
    scipy_psd = load_txt(f"{data_dir}/periodogram_psd.txt")

    # Load Java output (Golden Master comparison)
    java_freq = load_txt(f"{data_dir}/periodogram_java_frequencies.txt")
    java_psd = load_txt(f"{data_dir}/periodogram_java_psd.txt")

    # Create figure
    fig, axes = plt.subplots(2, 1, figsize=(10, 8))

    # Plot 1: Full PSD comparison
    ax1 = axes[0]
    ax1.semilogy(scipy_freq, scipy_psd, label='SciPy', color=scipy_color, linewidth=2)
    ax1.semilogy(java_freq, java_psd, '--', label='jSciPy', color=jscipy_color, linewidth=2, alpha=0.8)
    ax1.set_xlabel('Frequency (Hz)')
    ax1.set_ylabel('PSD (V²/Hz)')
    ax1.set_title('Periodogram Comparison: SciPy vs jSciPy', color=title_color)
    ax1.legend()
    ax1.grid(True, alpha=0.3)

    # Plot 2: Zoomed to peak region (40-130 Hz)
    ax2 = axes[1]
    mask = (scipy_freq >= 40) & (scipy_freq <= 130)
    ax2.semilogy(scipy_freq[mask], scipy_psd[mask], label='SciPy', color=scipy_color, linewidth=2)
    ax2.semilogy(java_freq[mask], java_psd[mask], '--', label='jSciPy', color=jscipy_color, linewidth=2, alpha=0.8)
    ax2.set_xlabel('Frequency (Hz)')
    ax2.set_ylabel('PSD (V²/Hz)')
    ax2.set_title('Zoomed: Peak Frequencies (50 Hz and 120 Hz)', color=title_color)
    ax2.legend()
    ax2.grid(True, alpha=0.3)
    ax2.axvline(x=50, color='gray', linestyle=':', alpha=0.5, label='50 Hz')
    ax2.axvline(x=120, color='gray', linestyle=':', alpha=0.5, label='120 Hz')

    plt.tight_layout()

    # Save
    output_dir = "python/figs/periodogram"
    ensure_dir(output_dir)
    filename = f"{output_dir}/periodogram_comparison_{theme}.png"
    plt.savefig(filename, dpi=150, bbox_inches='tight')
    print(f"Saved {filename}")
    plt.close()

if __name__ == "__main__":
    plot_comparison('light')
    plot_comparison('dark')
    print("Periodogram plots generated.")
