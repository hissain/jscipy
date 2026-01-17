import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_data(path):
    return np.loadtxt(path)

def plot_comparison():
    try:
        java_out = load_data('datasets/bessel_output_java.txt')
        scipy_out = load_data('datasets/bessel_output_scipy.txt')
        signal_in = load_data('datasets/bessel_input.txt')
    except Exception as e:
        print(f"Error loading data: {e}")
        return

    t = np.linspace(0, 1.0, len(signal_in))

    fig, (ax1, ax2, ax3) = plt.subplots(3, 1, sharex=True, figsize=style_utils.FIG_SIZE_WIDE)
    
    ax1.plot(t, signal_in, label='Input Signal', linewidth=1.5, color='gray')
    ax1.legend()
    ax1.set_title('Input Signal (Bessel)')
    
    ax2.plot(t, scipy_out, label='SciPy Reference', linewidth=1.5, alpha=0.8)
    ax2.plot(t, java_out, label='Java Implementation', linestyle='--', linewidth=2.0)
    ax2.legend()
    ax2.set_title('Filtered Output Comparison')
    
    # Difference
    diff = scipy_out - java_out
    rmse = np.sqrt(np.mean(diff**2))
    ax3.plot(t, diff, label=f'Diff (RMSE={rmse:.2e})', color='red', linewidth=1.0)
    ax3.legend()
    ax3.set_title("Difference (Python - Java)")

    plt.tight_layout()
    style_utils.save_plot(fig, 'bessel_comparison.png')
    plt.close(fig)

if __name__ == "__main__":
    plot_comparison()
