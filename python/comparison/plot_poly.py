import os
import numpy as np
import matplotlib.pyplot as plt
import style_utils

def load_txt(filename):
    """Load data from a text file."""
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f if line.strip()])

def plot_polyfit(test_name):
    """Plot polyfit comparison between Python and Java."""
    data_dir = "datasets/poly"
    
    # Load data
    x = load_txt(f'{data_dir}/polyfit_{test_name}_x.txt')
    y = load_txt(f'{data_dir}/polyfit_{test_name}_y.txt')
    scipy_coeffs = load_txt(f'{data_dir}/polyfit_{test_name}_coeffs.txt')
    
    # Load Java output
    java_coeffs_file = f'{data_dir}/polyfit_{test_name}_coeffs_java.txt'
    if not os.path.exists(java_coeffs_file):
        print(f"Java output not found: {java_coeffs_file}")
        print("Run Java tests first to generate output.")
        return
    java_coeffs = load_txt(java_coeffs_file)
    
    # Generate smooth curve for plotting
    x_smooth = np.linspace(min(x), max(x), 100)
    y_scipy = np.polyval(scipy_coeffs, x_smooth)
    y_java = np.polyval(java_coeffs, x_smooth)
    
    # Create figure with 2 subplots: fit comparison and difference
    fig, axes = plt.subplots(2, 1, figsize=(10, 8))
    
    # Plot 1: Fit comparison
    axes[0].plot(x, y, 'o', label='Data Points', markersize=6, alpha=0.6, color='gray')
    axes[0].plot(x_smooth, y_scipy, '-', label='SciPy (numpy.polyfit)', linewidth=2)
    axes[0].plot(x_smooth, y_java, '--', label='jSciPy (Poly.polyfit)', linewidth=2)
    axes[0].set_title(f"Polynomial Fit Comparison ({test_name})")
    axes[0].set_xlabel("x")
    axes[0].set_ylabel("y")
    axes[0].legend()
    axes[0].grid(True, alpha=0.3)
    
    # Plot 2: Difference (error)
    diff = y_java - y_scipy
    axes[1].plot(x_smooth, diff, '-', linewidth=1.5)
    axes[1].set_title("Difference (jSciPy - SciPy)")
    axes[1].set_xlabel("x")
    axes[1].set_ylabel("Difference")
    axes[1].ticklabel_format(style='scientific', axis='y', scilimits=(-2,2))
    axes[1].grid(True, alpha=0.3)
    
    plt.tight_layout()
    
    # Save plot
    os.makedirs('python/figs/poly', exist_ok=True)
    style_utils.save_plot(fig, f"poly/polyfit_{test_name}_comparison.png")
    plt.close(fig)

if __name__ == '__main__':
    # Generate plots for both light and dark themes
    for theme in ['light', 'dark']:
        os.environ['JSCIPY_PLOT_THEME'] = theme
        style_utils.apply_style()
        
        plot_polyfit('exact')
        plot_polyfit('lstsq')
    
    print("Poly comparison plots generated.")
