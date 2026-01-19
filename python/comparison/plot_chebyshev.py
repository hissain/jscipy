import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(input_filename, python_output_filename, java_output_filename, title):
    try:
        if not os.path.exists(java_output_filename):
            print(f"File not found: {java_output_filename}")
            return
            
        signal_in = read_data_file(input_filename)
        python_output = read_data_file(python_output_filename)
        java_output = read_data_file(java_output_filename)

        # Plotting
        fig, (ax1, ax2, ax3) = plt.subplots(3, 1, sharex=True, figsize=style_utils.FIG_SIZE_WIDE)
        
        ax1.plot(signal_in, label='Input', linewidth=1.5, color='gray')
        ax1.legend()
        ax1.set_title(f"Input Signal ({title})")
        
        ax2.plot(python_output, label='Python SciPy', linewidth=1.5, alpha=0.8)
        ax2.plot(java_output, label='Java jSciPy', linestyle='--', linewidth=2.0)
        ax2.legend()
        ax2.set_title("Filtered Output Comparison")
        
        # Difference
        diff = python_output - java_output
        rmse = np.sqrt(np.mean(diff**2))
        ax3.plot(diff, label=f'Diff (RMSE={rmse:.2e})', color='red', linewidth=1.0)
        style_utils.finalize_diff_plot(ax3, python_output)
        ax3.legend()
        ax3.set_title("Difference (Python - Java)")
        
        plt.tight_layout()
        os.makedirs('python/figs/chebyshev', exist_ok=True)
        output_filename = f"{input_filename.split('/')[-1]}.png"
        style_utils.save_plot(fig, f"chebyshev/{output_filename}")
        plt.close(fig)
    except Exception as e:
        print(f"Error plotting {input_filename}: {e}")

if __name__ == '__main__':
    plot_test('datasets/chebyshev/cheby1_input1.txt', 'datasets/chebyshev/cheby1_output1.txt', 'datasets/chebyshev/cheby1_output1_java.txt', 'Chebyshev Type I')
    plot_test('datasets/chebyshev/cheby2_input1.txt', 'datasets/chebyshev/cheby2_output1.txt', 'datasets/chebyshev/cheby2_output1_java.txt', 'Chebyshev Type II')
