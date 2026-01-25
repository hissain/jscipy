import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_matrix(filename):
    if not os.path.exists(filename):
        print(f"File not found: {filename}")
        return None
    with open(filename, 'r') as f:
        # Skip header line (rows cols)
        lines = f.readlines()[1:]
    return np.loadtxt(lines)

def plot_all_modes():
    modes = [
        ('FULL', 'datasets/convolve2d/conv2d_out_full_1.txt', 'datasets/convolve2d/conv2d_out_full_1_java.txt'),
        ('SAME', 'datasets/convolve2d/conv2d_out_same_1.txt', 'datasets/convolve2d/conv2d_out_same_1_java.txt'),
        ('VALID', 'datasets/convolve2d/conv2d_out_valid_1.txt', 'datasets/convolve2d/conv2d_out_valid_1_java.txt')
    ]

    # Create 3x3 grid
    # Increase height to accommodate 3 rows
    fig, axes = plt.subplots(3, 3, figsize=(12, 12), constrained_layout=True)
    
    for i, (mode_name, py_file, java_file) in enumerate(modes):
        py_data = load_matrix(py_file)
        java_data = load_matrix(java_file)
        
        if py_data is None or java_data is None:
            continue

        rmse = np.sqrt(np.mean((py_data - java_data)**2))

        # SciPy
        im0 = axes[i, 0].imshow(py_data, aspect='auto')
        axes[i, 0].set_title(f'SciPy {mode_name}')
        plt.colorbar(im0, ax=axes[i, 0])

        # jSciPy
        im1 = axes[i, 1].imshow(java_data, aspect='auto')
        axes[i, 1].set_title(f'jSciPy {mode_name}')
        plt.colorbar(im1, ax=axes[i, 1])

        # Diff
        diff = py_data - java_data
        vmin, vmax = style_utils.finalize_diff_plot(None, py_data, is_2d=True)
        im2 = axes[i, 2].imshow(diff, cmap='coolwarm', aspect='auto', vmin=vmin, vmax=vmax)
        axes[i, 2].set_title(f'Diff (RMSE={rmse:.2e})')
        plt.colorbar(im2, ax=axes[i, 2])

    plt.suptitle("2D Convolution Comparison (FULL, SAME, VALID)")
    
    os.makedirs('python/figs/convolve2d', exist_ok=True)
    # User asked for "all three in new rows".
    # I will save as convolve2d_comparison_all.png to avoid confusion, but update README.
    style_utils.save_plot(fig, "convolve2d/convolve2d_comparison_all.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_all_modes()
