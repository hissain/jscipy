import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

# --- 1D Setup ---
def read_data_file_1d(filename):
    if not os.path.exists(filename):
        print(f"File not found: {filename}")
        return None
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_correlate_1d(test_id):
    input1_file = f"datasets/correlate/{test_id}_input1.txt"
    input2_file = f"datasets/correlate/{test_id}_input2.txt"
    expected_file = f"datasets/correlate/{test_id}_output.txt"
    actual_file = f"datasets/correlate/{test_id}_output_java.txt"

    in1 = read_data_file_1d(input1_file)
    in2 = read_data_file_1d(input2_file)
    expected = read_data_file_1d(expected_file)
    actual = read_data_file_1d(actual_file)

    if in1 is None or in2 is None or expected is None or actual is None:
        return

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=False, figsize=style_utils.FIG_SIZE_WIDE)
    
    # Subplot 1: Inputs
    ax1.plot(in1, label='Input 1 (Signal)', linewidth=1.5, alpha=0.9)
    ax1.plot(in2, label='Input 2 (Kernel)', linewidth=1.5, alpha=0.9, linestyle='--')
    ax1.legend()
    ax1.set_title(f"1D Correlation Inputs: {test_id}")
    
    # Subplot 2: Output Comparison
    ax2.plot(expected, label='Python SciPy', linewidth=2.5, alpha=0.6)
    ax2.plot(actual, label='Java jSciPy', linestyle=':', linewidth=2.0)
    ax2.legend()
    ax2.set_title(f"Output Comparison")
    
    plt.tight_layout()
    os.makedirs('python/figs/correlate', exist_ok=True)
    style_utils.save_plot(fig, f"correlate/correlate_comparison.png")
    plt.close(fig)

# --- 2D Setup ---
def load_matrix_2d(filename):
    """Load a 2D matrix saved with dimensions on first line."""
    if not os.path.exists(filename):
        print(f"File not found: {filename}")
        return None
    with open(filename, 'r') as f:
        dims_line = f.readline().strip().split()
        rows, cols = int(dims_line[0]), int(dims_line[1])
        matrix = []
        for _ in range(rows):
            row = [float(x) for x in f.readline().strip().split()]
            matrix.append(row)
        return np.array(matrix)

def plot_correlate_2d(test_name, in1_file, in2_file, scipy_out, java_out):
    """Create a 3-panel comparison plot for correlate2d results."""
    in1 = load_matrix_2d(in1_file)
    in2 = load_matrix_2d(in2_file)
    scipy_result = load_matrix_2d(scipy_out)
    java_result = load_matrix_2d(java_out)

    if scipy_result is None or java_result is None:
        print(f"Skipping {test_name}: missing output files")
        return

    # Create figure with 3 panels
    fig, axes = plt.subplots(1, 3, figsize=style_utils.FIG_SIZE_2D)

    # Panel 1: SciPy result
    im1 = axes[0].imshow(scipy_result, cmap='viridis', aspect='auto')
    axes[0].set_title(f'Python SciPy\\n{test_name}')
    axes[0].set_xlabel('Column')
    axes[0].set_ylabel('Row')
    plt.colorbar(im1, ax=axes[0])

    # Panel 2: Java result
    im2 = axes[1].imshow(java_result, cmap='viridis', aspect='auto')
    axes[1].set_title(f'Java jSciPy\\n{test_name}')
    axes[1].set_xlabel('Column')
    axes[1].set_ylabel('Row')
    plt.colorbar(im2, ax=axes[1])

    # Panel 3: Difference
    diff = scipy_result - java_result
    rmse = np.sqrt(np.mean(diff**2))
    
    vmin, vmax = style_utils.finalize_diff_plot(axes[2], scipy_result, is_2d=True)
    
    im3 = axes[2].imshow(diff, cmap='RdBu_r', aspect='auto', vmin=vmin, vmax=vmax)
    axes[2].set_title(f'Difference (RMSE={rmse:.2e})')
    axes[2].set_xlabel('Column')
    axes[2].set_ylabel('Row')
    plt.colorbar(im3, ax=axes[2])

    plt.tight_layout()
    
    output_name = f"correlate2d/{test_name.lower().replace(' ', '_')}_comparison.png"
    style_utils.save_plot(fig, output_name)
    plt.close(fig)

if __name__ == '__main__':
    # 1D comparison
    print("Generating 1D plots...")
    plot_correlate_1d('correlate_random_full')
    
    # 2D comparison
    print("Generating 2D plots...")
    dataset_dir = 'datasets/correlate2d/'
    plot_correlate_2d('Correlate2d',
        dataset_dir + 'correlate2d_in1_2.txt',
        dataset_dir + 'correlate2d_in2_2.txt',
        dataset_dir + 'correlate2d_out_full_2.txt',
        dataset_dir + 'correlate2d_out_full_2_java.txt'
    )
    print("Plots generated.")
