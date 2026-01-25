import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_matrix(filename):
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

def plot_correlate2d_comparison(test_name, in1_file, in2_file, scipy_out, java_out):
    """Create a 3-panel comparison plot for correlate2d results."""
    in1 = load_matrix(in1_file)
    in2 = load_matrix(in2_file)
    scipy_result = load_matrix(scipy_out)
    java_result = load_matrix(java_out)

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
    
    # Use finalize_diff_plot to get appropriate vmin/vmax for difference
    vmin, vmax = style_utils.finalize_diff_plot(axes[2], scipy_result, is_2d=True)
    
    im3 = axes[2].imshow(diff, cmap='RdBu_r', aspect='auto', vmin=vmin, vmax=vmax)
    axes[2].set_title(f'Difference (RMSE={rmse:.2e})')
    axes[2].set_xlabel('Column')
    axes[2].set_ylabel('Row')
    plt.colorbar(im3, ax=axes[2])

    plt.tight_layout()
    
    # Save with theme suffix
    output_name = f"correlate2d/{test_name.lower().replace(' ', '_')}_comparison.png"
    style_utils.save_plot(fig, output_name)
    plt.close(fig)

if __name__ == '__main__':
    dataset_dir = 'datasets/correlate2d/'
    
    # Test 1: Basic FULL mode
    plot_correlate2d_comparison(
        'Basic FULL Mode',
        dataset_dir + 'correlate2d_in1_1.txt',
        dataset_dir + 'correlate2d_in2_1.txt',
        dataset_dir + 'correlate2d_out_full_1.txt',
        dataset_dir + 'correlate2d_out_full_1_java.txt'
    )
    
    # Test 2: Basic SAME mode
    plot_correlate2d_comparison(
        'Basic SAME Mode',
        dataset_dir + 'correlate2d_in1_1.txt',
        dataset_dir + 'correlate2d_in2_1.txt',
        dataset_dir + 'correlate2d_out_same_1.txt',
        dataset_dir + 'correlate2d_out_same_1_java.txt'
    )
    
    # Test 3: Basic VALID mode
    plot_correlate2d_comparison(
        'Basic VALID Mode',
        dataset_dir + 'correlate2d_in1_1.txt',
        dataset_dir + 'correlate2d_in2_1.txt',
        dataset_dir + 'correlate2d_out_valid_1.txt',
        dataset_dir + 'correlate2d_out_valid_1_java.txt'
    )
    
    # Test 4: Random FULL mode (main comparison graph for README)
    plot_correlate2d_comparison(
        'Correlate2D Comparison',
        dataset_dir + 'correlate2d_in1_2.txt',
        dataset_dir + 'correlate2d_in2_2.txt',
        dataset_dir + 'correlate2d_out_full_2.txt',
        dataset_dir + 'correlate2d_out_full_2_java.txt'
    )
    
    print("Correlate2d comparison plots generated successfully!")
