import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_complex_matrix(real_file, imag_file):
    if not os.path.exists(real_file) or not os.path.exists(imag_file):
        raise FileNotFoundError(f"Missing data files: {real_file} or {imag_file}")
    real = np.loadtxt(real_file)
    imag = np.loadtxt(imag_file)
    return real + 1j * imag

def plot_comparison(input_file, py_real_file, py_imag_file, java_real_file, java_imag_file, output_name):
    # Load data
    try:
        input_data = np.loadtxt(input_file)
        # Input is likely complex too for general FFT, but for this test it's real
        # If it's stored as real matrix:
        if input_data.ndim == 2:
            pass # already matrix
    except Exception as e:
        print(f"Error loading inputs: {e}")
        return

    try:
        py_out = load_complex_matrix(py_real_file, py_imag_file)
        java_out = load_complex_matrix(java_real_file, java_imag_file)
    except FileNotFoundError as e:
        print(e)
        return

    # Compute Magnitude
    mag_py = np.abs(py_out)
    mag_java = np.abs(java_out)
    
    # Shift for better visualization
    mag_py_shifted = np.fft.fftshift(mag_py)
    mag_java_shifted = np.fft.fftshift(mag_java)

    # Calculate RMSE
    rmse = np.sqrt(np.mean(np.abs(py_out - java_out)**2))

    # Plot
    fig, axes = plt.subplots(1, 3, figsize=(15, 5))
    
    im0 = axes[0].imshow(np.log1p(mag_py_shifted), cmap='viridis')
    axes[0].set_title('Python FFT2 (Log Mag)')
    plt.colorbar(im0, ax=axes[0])

    im1 = axes[1].imshow(np.log1p(mag_java_shifted), cmap='viridis')
    axes[1].set_title('Java FFT2 (Log Mag)')
    plt.colorbar(im1, ax=axes[1])

    # Difference
    diff = np.abs(mag_py_shifted - mag_java_shifted)
    im2 = axes[2].imshow(diff, cmap='inferno')
    axes[2].set_title(f'Difference (RMSE={rmse:.2e})')
    plt.colorbar(im2, ax=axes[2])
    
    plt.tight_layout()
    style_utils.save_plot(fig, output_name)
    plt.close()

if __name__ == '__main__':
    plot_comparison(
        'datasets/fft2_input.txt',
        'datasets/fft2_output_real.txt',
        'datasets/fft2_output_imag.txt',
        'datasets/fft2_output_java_real.txt',
        'datasets/fft2_output_java_imag.txt',
        'fft2_comparison.png'
    )
