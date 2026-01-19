import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    if not os.path.exists(filename):
        print(f"File not found: {filename}")
        return None
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_correlate_test(test_id):
    input1_file = f"datasets/correlate/{test_id}_input1.txt"
    input2_file = f"datasets/correlate/{test_id}_input2.txt"
    expected_file = f"datasets/correlate/{test_id}_output.txt"
    actual_file = f"datasets/correlate/{test_id}_output_java.txt"

    in1 = read_data_file(input1_file)
    in2 = read_data_file(input2_file)
    expected = read_data_file(expected_file)
    actual = read_data_file(actual_file)

    if in1 is None or in2 is None or expected is None or actual is None:
        return

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=False, figsize=style_utils.FIG_SIZE_WIDE)
    
    # Subplot 1: Inputs
    ax1.plot(in1, label='Input 1 (Signal)', linewidth=1.5, alpha=0.9)
    ax1.plot(in2, label='Input 2 (Kernel)', linewidth=1.5, alpha=0.9, linestyle='--')
    ax1.legend()
    ax1.set_title(f"Cross-Correlation Inputs")
    
    # Subplot 2: Output Comparison
    ax2.plot(expected, label='Python SciPy', linewidth=2.5, alpha=0.6)
    ax2.plot(actual, label='Java jSciPy', linestyle=':', linewidth=2.0)
    ax2.legend()
    ax2.set_title(f"Correlation Output Comparison")
    
    plt.tight_layout()
    os.makedirs('python/figs/correlate', exist_ok=True)
    # The style_utils.save_plot will append _light.png or _dark.png automatically
    os.makedirs('python/figs/correlate', exist_ok=True)
    style_utils.save_plot(fig, f"correlate/correlate_comparison.png")
    plt.close(fig)

if __name__ == '__main__':
    # Focus on the most representative test case
    plot_correlate_test('correlate_random_full')
