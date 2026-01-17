import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_matrix(filename):
    with open(filename, 'r') as f:
        lines = f.readlines()
        # Check if first line is header
        if len(lines[0].split()) <= 2:
             lines = lines[1:]
    return np.loadtxt(lines)

def load_complex_matrix(real_file, imag_file):
    real = load_matrix(real_file)
    imag = load_matrix(imag_file)
    return real + 1j * imag

def plot_fft2_comparison(py_real, py_imag, java_real, java_imag, title_suffix):
    if not os.path.exists(java_real):
        print(f"Skipping {title_suffix}: {java_real} not found")
        return

    try:
        py_data = load_complex_matrix(py_real, py_imag)
        java_data = load_complex_matrix(java_real, java_imag)
    except Exception as e:
        print(f"Error loading files: {e}")
        return

    # RMSE on magnitude
    rmse = np.sqrt(np.mean(np.abs(py_data - java_data)**2))
    
    # Use log magnitude for better visualization of FFT
    py_plot = np.log10(np.abs(py_data) + 1e-10)
    java_plot = np.log10(np.abs(java_data) + 1e-10)
    
    # Plot
    fig, axes = plt.subplots(1, 3, figsize=style_utils.FIG_SIZE_2D, constrained_layout=True)
    
    im0 = axes[0].imshow(py_plot, aspect='auto')
    axes[0].set_title(f'SciPy {title_suffix} (Log Mag)')
    plt.colorbar(im0, ax=axes[0])

    im1 = axes[1].imshow(java_plot, aspect='auto')
    axes[1].set_title(f'jSciPy {title_suffix} (Log Mag)')
    plt.colorbar(im1, ax=axes[1])

    # Difference in magnitude
    diff = np.abs(py_data) - np.abs(java_data)
    vmin, vmax = style_utils.finalize_diff_plot(None, np.abs(py_data), is_2d=True)
    im2 = axes[2].imshow(diff, cmap='coolwarm', aspect='auto', vmin=vmin, vmax=vmax)
    axes[2].set_title(f'Diff Mag (RMSE={rmse:.2e})')
    plt.colorbar(im2, ax=axes[2])

    plt.suptitle(f"2D FFT Comparison ({title_suffix})")
    
    style_utils.save_plot(fig, f"fft2_comparison_{title_suffix.lower().replace(' ', '_')}.png")
    plt.close(fig)

if __name__ == "__main__":
    plot_fft2_comparison(
        'datasets/fft2_out_real.txt', 'datasets/fft2_out_imag.txt',
        'datasets/java_fft2_out_real.txt', 'datasets/java_fft2_out_imag.txt',
        'Forward'
    )
    # Note: Inverse test data is currently just output check, might need update if IFFT comparison desired
    # For now, just Forward is enough as per datasets availability
