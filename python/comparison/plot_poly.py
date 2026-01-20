import os
import numpy as np
import matplotlib.pyplot as plt
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    if not os.path.exists(filename):
         # Try prepending datasets/poly if just filename given
         filename = os.path.join("datasets/poly", filename)
    
    with open(filename, 'r') as f:
        data = [float(line.strip()) for line in f if line.strip()]
    return data

def plot_polyfit(test_name):
    try:
        x = read_data_file(f'polyfit_{test_name}_x.txt')
        y = read_data_file(f'polyfit_{test_name}_y.txt')
        # We need the java output coefficients to evaluate and compare
        # Assuming we have them (need to check if java test saved them)
        # Actually, let's check what Java saves.
        # Usually Java tests save output. If not, we might not have 'java_coeffs'.
        # Let's plot the raw data and the fitted curve from Python coefficients for now.
        # If we have Java coefficients, we can plot that too.
        
        coeffs_py = read_data_file(f'polyfit_{test_name}_coeffs.txt')
        
        # Check for Java output
        java_coeffs_file = f'datasets/poly/polyfit_{test_name}_coeffs_java.txt'
        coeffs_java = []
        if os.path.exists(java_coeffs_file):
            coeffs_java = read_data_file(java_coeffs_file)
            
    except FileNotFoundError as e:
        print(f"Skipping {test_name}: {e}")
        return

    # Generate smooth curve for plotting
    x_smooth = np.linspace(min(x), max(x), 100)
    
    # Eval python poly
    y_smooth_py = np.polyval(coeffs_py, x_smooth)

    # Plotting
    fig, ax = plt.subplots(figsize=style_utils.FIG_SIZE_WIDE)
    ax.plot(x, y, 'o', label='Data Points', markersize=5, color='gray', alpha=0.6)
    ax.plot(x_smooth, y_smooth_py, '-', label='Python fit', linewidth=1.5)
    
    if coeffs_java:
         # Java coeffs might be reversed? Commons math returns low-to-high. 
         # My Poly.java implementation reverses them to match numpy (high-to-low).
         # So we can use np.polyval directly if my implementation is correct.
         y_smooth_java = np.polyval(coeffs_java, x_smooth)
         ax.plot(x_smooth, y_smooth_java, '--', label='Java fit', linewidth=1.5)

    ax.set_title(f"Polyfit Comparison ({test_name})")
    ax.set_xlabel("x")
    ax.set_ylabel("y")
    ax.legend()

    plt.tight_layout()
    os.makedirs('python/figs/poly', exist_ok=True)
    style_utils.save_plot(fig, f"poly/polyfit_{test_name}.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_polyfit('exact')  # Looking at file list: polyfit_exact_x.txt
    plot_polyfit('lstsq')  # Looking at file list: polyfit_lstsq_x.txt
