import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(test_id):
    try:
        x = read_data_file(f'datasets/interpolation_input_x_{test_id}.txt')
        y = read_data_file(f'datasets/interpolation_input_y_{test_id}.txt')
        new_x = read_data_file(f'datasets/interpolation_input_new_x_{test_id}.txt')
        
        linear_y_python = read_data_file(f'datasets/interpolation_output_linear_{test_id}.txt')
        cubic_y_python = read_data_file(f'datasets/interpolation_output_cubic_{test_id}.txt')
        
        linear_y_java = read_data_file(f'datasets/interpolation_output_linear_java_{test_id}.txt')
        cubic_y_java = read_data_file(f'datasets/interpolation_output_cubic_java_{test_id}.txt')
    except FileNotFoundError as e:
        print(f"Skipping interpolation {test_id}: {e}")
        return

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=style_utils.FIG_SIZE_WIDE, sharex=True)
    
    # Linear
    ax1.plot(x, y, 'o', label='Original Data', color='gray')
    ax1.plot(new_x, linear_y_python, label='Python Linear', linewidth=1.5, alpha=0.8)
    ax1.plot(new_x, linear_y_java, '--', label='Java Linear', linewidth=2.0)
    ax1.legend()
    ax1.set_title(f"Linear Interpolation (Test {test_id})")

    # Cubic
    ax2.plot(x, y, 'o', label='Original Data', color='gray')
    ax2.plot(new_x, cubic_y_python, label='Python Cubic', linewidth=1.5, alpha=0.8)
    ax2.plot(new_x, cubic_y_java, '--', label='Java Cubic', linewidth=2.0)
    ax2.legend()
    ax2.set_title(f"Cubic Interpolation (Test {test_id})")

    plt.tight_layout()
    style_utils.save_plot(fig, f"interpolation_comparison_{test_id}.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_test(1)
    plot_test(2)
