import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

# Use the style utility
style_utils.apply_style()

# File paths
DATA_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/dct")
FIGS_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../figs/dct")
os.makedirs(FIGS_DIR, exist_ok=True)

def load_data(filename):
    return np.loadtxt(os.path.join(DATA_DIR, filename))

def plot_dct_comparison(test_id):
    # Load inputs and outputs
    x_input = load_data(f"{test_id}_input.txt")
    y_java = load_data(f"{test_id}_output_java.txt")
    y_scipy = load_data(f"{test_id}_output.txt") # Ground truth

    mse = np.mean((y_java - y_scipy)**2)
    print(f"MSE for {test_id}: {mse}")

    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=style_utils.FIG_SIZE_WIDE)
    
    # Plot 1: Overlay of coefficients
    ax1.plot(y_scipy, label='SciPy (Reference)', alpha=0.8, linewidth=2)
    ax1.plot(y_java, '--', label='Java (jSciPy)', alpha=0.9, linewidth=2)
    ax1.set_title(f"DCT Coefficients Comparison ({test_id})")
    ax1.legend()
    ax1.set_ylabel("Amplitude")

    # Plot 2: Difference (Error)
    diff = y_java - y_scipy
    ax2.plot(diff, color='red', label='Difference (Java - SciPy)')
    ax2.set_title("Difference (Error)")
    ax2.set_ylabel("Error")
    ax2.set_xlabel("Bin Index")
    ax2.legend()
    
    # Save using style_utils to handle theme suffix
    style_utils.save_plot(fig, "dct_comparison.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_dct_comparison("dct_random_even")
