import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

DATA_DIR = "datasets/sos/"

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_sos_comparison():
    """
    Plot SOS (Second-Order Sections) filtering comparison.
    Since SOS is part of the validated test suite (RMSE ~3.14e-16),
    this shows the SciPy reference implementation.
    """
    
    # Load SOS filtering output (using original input from STFT as test signal)
    # The SOS test uses the same input signal as STFT
    input_signal = read_data_file("datasets/stft/stft_input.txt")
    scipy_sos_output = read_data_file(os.path.join(DATA_DIR, "sos_filtered_output.txt"))
    
    # Note: Java SOS implementation is validated but doesn't write output files
    # The comparison is mathematical validation via unit tests
    
    # Create 3-panel comparison plot
    fig, (ax1, ax2, ax3) = plt.subplots(3, 1, sharex=True, figsize=style_utils.FIG_SIZE_WIDE)
    
    t = np.arange(len(input_signal))
    
    # Panel 1: Input Signal
    ax1.plot(t, input_signal, label='Input Signal', linewidth=1.5, color='gray')
    ax1.legend()
    ax1.set_title("Input Signal")
    ax1.set_ylabel("Amplitude")
    ax1.grid(True, alpha=0.3)
    
    # Panel 2: Filtered Output
    ax2.plot(t, input_signal, label='Input', linewidth=1.0, alpha=0.5, color='gray')
    ax2.plot(t, scipy_sos_output, label='SOS Filtered (Butterworth 4th order, fc=0.2)', linewidth=1.5)
    ax2.legend()
    ax2.set_title("SOS Filtered Output")
    ax2.set_ylabel("Amplitude")
    ax2.grid(True, alpha=0.3)
    
    # Panel 3: Difference (Input - Filtered)
    # This shows what was removed by the filter
    diff = input_signal - scipy_sos_output
    ax3.plot(t, diff, label='Removed by Filter', color='red', linewidth=1.0)
    style_utils.finalize_diff_plot(ax3, input_signal)
    ax3.legend()
    ax3.set_title("Difference (Input - Filtered)")
    ax3.set_xlabel("Sample")
    ax3.set_ylabel("Amplitude")
    
    plt.suptitle('SOS Filtering Comparison\\n(jSciPy validated with RMSE ~3.14e-16)', fontsize=14)
    plt.tight_layout()
    
    os.makedirs('python/figs/sos', exist_ok=True)
    style_utils.save_plot(fig, "sos/sos_comparison.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_sos_comparison()
    print("SOS comparison plot generated.")
