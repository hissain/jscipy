import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(input_filename, python_output_filename, java_output_filename):
    if not os.path.exists(java_output_filename):
        print(f"Skipping RK4: {java_output_filename} not found")
        return

    t_span = read_data_file(input_filename)
    python_output = read_data_file(python_output_filename)
    java_output = read_data_file(java_output_filename)

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=style_utils.FIG_SIZE_WIDE)
    
    ax1.plot(t_span, python_output, label='Python (Analytical)', linewidth=1.5, alpha=0.8)
    ax1.plot(t_span, java_output, label='jSciPy RK4', linestyle='--', linewidth=2.0)
    ax1.legend()
    ax1.set_title(f"RK4 Solver: dy/dt = -2ty, y(0)=1")
    ax1.set_ylabel("y")
    ax1.grid(True, alpha=0.3)
    
    diff = python_output - java_output
    rmse = np.sqrt(np.mean(diff**2))
    ax2.plot(t_span, diff, label=f'Diff (RMSE={rmse:.2e})', color='red')
    ax2.legend()
    ax2.set_title("Difference (Analytical - jSciPy)")
    ax2.set_xlabel("t")
    ax2.set_ylabel("Error")
    ax2.ticklabel_format(style='scientific', axis='y', scilimits=(-2,2))
    ax2.grid(True, alpha=0.3)

    plt.tight_layout()
    os.makedirs('python/figs/rk4', exist_ok=True)
    style_utils.save_plot(fig, f"rk4/rk4_comparison.png")
    plt.close(fig)

if __name__ == '__main__':
    # Generate for both themes
    for theme in ['light', 'dark']:
        os.environ['JSCIPY_PLOT_THEME'] = theme
        style_utils.apply_style()
        plot_test('datasets/rk4/rk4_input.txt', 'datasets/rk4/rk4_output.txt', 'datasets/rk4/rk4_output_java.txt')
    print("RK4 plots generated.")
