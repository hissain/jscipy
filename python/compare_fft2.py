import numpy as np
import matplotlib.pyplot as plt
import os

ARTIFACT_DIR = "/Users/hissain/git/github/jscipy/python/figs"

def load_complex_matrix(real_file, imag_file):
    with open(real_file, 'r') as f:
        lines_real = f.readlines()[1:]
    with open(imag_file, 'r') as f:
        lines_imag = f.readlines()[1:]
        
    data_real = []
    for line in lines_real:
        data_real.append([float(x) for x in line.split()])
        
    data_imag = []
    for line in lines_imag:
        data_imag.append([float(x) for x in line.split()])
        
    return np.array(data_real) + 1j * np.array(data_imag)

def main():
    print("Loading data...")
    try:
        os.makedirs(ARTIFACT_DIR, exist_ok=True)
        
        java_res = load_complex_matrix("test_data/java_fft2_out_real.txt", "test_data/java_fft2_out_imag.txt")
        scipy_res = load_complex_matrix("test_data/fft2_out_real.txt", "test_data/fft2_out_imag.txt")
        
        # Compute Magnitude Spectrum
        java_mag = np.abs(java_res)
        scipy_mag = np.abs(scipy_res)
        
        diff = np.abs(java_res - scipy_res)
        rmse = np.sqrt(np.mean(diff**2)) # Complex RMSE
        max_diff = np.max(diff)

        print(f"RMSE (Complex): {rmse}")
        print(f"Max Diff (Magnitude): {np.max(np.abs(java_mag - scipy_mag))}")

        fig, axes = plt.subplots(1, 2, figsize=(12, 5))
        
        im0 = axes[0].imshow(np.log1p(java_mag), cmap='viridis')
        axes[0].set_title("Java FFT2 (Log Magnitude)")
        plt.colorbar(im0, ax=axes[0])
        
        im1 = axes[1].imshow(np.log1p(scipy_mag), cmap='viridis')
        axes[1].set_title("SciPy FFT2 (Log Magnitude)")
        plt.colorbar(im1, ax=axes[1])
        
        plt.tight_layout()
        
        output_path = os.path.join(ARTIFACT_DIR, "fft2_comparison.png")
        plt.savefig(output_path)
        print(f"Comparison saved to {output_path}")

    except Exception as e:
        import traceback
        traceback.print_exc()
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
