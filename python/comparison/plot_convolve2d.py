import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_matrix(filename):
    with open(filename, 'r') as f:
        # Skip header line (rows cols)
        lines = f.readlines()[1:]
    return np.loadtxt(lines)

def plot_comparison(py_file, java_file, title_suffix):
    if not os.path.exists(java_file):
        print(f"Skipping {title_suffix}: {java_file} not found")
        return

    # Load data
    try:
        py_data = load_matrix(py_file)
        java_data = load_matrix(java_file)
    except Exception as e:
        print(f"Error loading files: {e}")
        return

    # RMSE
    rmse = np.sqrt(np.mean((py_data - java_data)**2))

    # Plot
    fig, axes = plt.subplots(1, 3, figsize=style_utils.FIG_SIZE_2D, constrained_layout=True)
    
    im0 = axes[0].imshow(py_data, aspect='auto')
    axes[0].set_title(f'SciPy {title_suffix}')
    plt.colorbar(im0, ax=axes[0])

    im1 = axes[1].imshow(java_data, aspect='auto')
    axes[1].set_title(f'jSciPy {title_suffix}')
    plt.colorbar(im1, ax=axes[1])

    diff = py_data - java_data
    im2 = axes[2].imshow(diff, cmap='coolwarm', aspect='auto')
    axes[2].set_title(f'Diff (RMSE={rmse:.2e})')
    plt.colorbar(im2, ax=axes[2])

    plt.suptitle(f"2D Convolution Comparison ({title_suffix})")
    
    style_utils.save_plot(fig, f"convolve2d_comparison_{title_suffix.lower().replace(' ', '_')}.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_comparison('datasets/conv2d_out_full_1.txt', 'datasets/conv2d_out_full_1_java.txt', 'FULL')
    plot_comparison('datasets/conv2d_out_same_1.txt', 'datasets/conv2d_out_same_1_java.txt', 'SAME')
    plot_comparison('datasets/conv2d_out_valid_1.txt', 'datasets/conv2d_out_valid_1_java.txt', 'VALID')
