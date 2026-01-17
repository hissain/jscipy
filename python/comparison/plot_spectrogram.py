import numpy as np
import matplotlib.pyplot as plt
import os

ARTIFACT_DIR = "python/figs"
DATASET_DIR = "datasets"

def read_data_file(filename):
    with open(filename, 'r') as f:
        data = [float(line.strip()) for line in f]
    return np.array(data)

def read_matrix_file(filename, shape_filename):
    # Read shape (for verification, though np.loadtxt finds it automatically)
    with open(shape_filename, 'r') as f:
        rows = int(f.readline().strip())
        cols = int(f.readline().strip())
    
    # Read data using np.loadtxt which handles 2D space-delimited files
    data = np.loadtxt(filename)
    
    if data.shape != (rows, cols):
        # Fallback for flattened data (Java output might be flattened)
        if data.ndim == 1 and data.size == rows * cols:
            return data.reshape((rows, cols))
        print(f"Warning: Loaded shape {data.shape} does not match expected {(rows, cols)}")
        return data.reshape((rows, cols))
        
    return data

def plot_spectrogram_comparison(test_name):
    print(f"Generating comparison for {test_name}...")
    
    # Load Python (Ground Truth) and Java results
    try:
        # Load Frequencies and Times (using Java output for axis, assuming they match)
        freqs = read_data_file(os.path.join(DATASET_DIR, f"{test_name}_java_freqs.txt"))
        times = read_data_file(os.path.join(DATASET_DIR, f"{test_name}_java_times.txt"))
        
        # Load Sxx
        sxx_java = read_matrix_file(
            os.path.join(DATASET_DIR, f"{test_name}_java_Sxx.txt"),
            os.path.join(DATASET_DIR, f"{test_name}_java_Sxx_shape.txt")
        )
        
        sxx_py = read_matrix_file(
            os.path.join(DATASET_DIR, f"{test_name}_Sxx.txt"),
            os.path.join(DATASET_DIR, f"{test_name}_Sxx_shape.txt")
        )
        
        # Calculate RMSE
        rmse = np.sqrt(np.mean((sxx_py - sxx_java)**2))
        print(f"RMSE for {test_name}: {rmse}")
        
        # Plot
        fig, axes = plt.subplots(1, 2, figsize=(14, 6), sharey=True)
        
        # Python Plot
        im0 = axes[0].pcolormesh(times, freqs, 10 * np.log10(sxx_py + 1e-10), shading='gouraud', cmap='viridis')
        axes[0].set_title(f'SciPy Spectrogram (dB)\n{test_name}')
        axes[0].set_ylabel('Frequency [Hz]')
        axes[0].set_xlabel('Time [sec]')
        plt.colorbar(im0, ax=axes[0], label='Power/Frequency (dB/Hz)')
        
        # Java Plot
        im1 = axes[1].pcolormesh(times, freqs, 10 * np.log10(sxx_java + 1e-10), shading='gouraud', cmap='viridis')
        axes[1].set_title(f'jSciPy Spectrogram (dB)\n{test_name}')
        axes[1].set_xlabel('Time [sec]')
        plt.colorbar(im1, ax=axes[1], label='Power/Frequency (dB/Hz)')
        
        plt.tight_layout()
        
        os.makedirs(ARTIFACT_DIR, exist_ok=True)
        output_path = os.path.join(ARTIFACT_DIR, f"{test_name}_comparison.png")
        plt.savefig(output_path)
        print(f"Saved plot to {output_path}")
        plt.close(fig)
        
    except FileNotFoundError as e:
        print(f"Error loading files for {test_name}: {e}")
        print("Ensure you have run the Java tests to generate the output files.")

if __name__ == "__main__":
    plot_spectrogram_comparison("spectrogram_chirp")
    plot_spectrogram_comparison("spectrogram_sine")
