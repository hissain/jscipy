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
        x = read_data_file(f'datasets/interpolation/interpolation_input_x_{test_id}.txt')
        y = read_data_file(f'datasets/interpolation/interpolation_input_y_{test_id}.txt')
        new_x = read_data_file(f'datasets/interpolation/interpolation_input_new_x_{test_id}.txt')
        
        linear_y_python = read_data_file(f'datasets/interpolation/interpolation_output_linear_{test_id}.txt')
        cubic_y_python = read_data_file(f'datasets/interpolation/interpolation_output_cubic_{test_id}.txt')
        
        linear_y_java = read_data_file(f'datasets/interpolation/interpolation_output_linear_java_{test_id}.txt')
        cubic_y_java = read_data_file(f'datasets/interpolation/interpolation_output_cubic_java_{test_id}.txt')
        
        quadratic_y_python = read_data_file(f'datasets/interpolation/interpolation_output_quadratic_{test_id}.txt')
        quadratic_y_java = read_data_file(f'datasets/interpolation/interpolation_output_quadratic_java_{test_id}.txt')
    except FileNotFoundError as e:
        print(f"Skipping interpolation {test_id}: {e}")
        return

    # Plotting
    fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(10, 12), sharex=True)
    
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

    # Quadratic
    ax3.plot(x, y, 'o', label='Original Data', color='gray')
    ax3.plot(new_x, quadratic_y_python, label='Python Quadratic', linewidth=1.5, alpha=0.8)
    ax3.plot(new_x, quadratic_y_java, '--', label='Java Quadratic', linewidth=2.0)
    ax3.legend()
    ax3.set_title(f"Quadratic Interpolation (Test {test_id})")

    plt.tight_layout()
    os.makedirs('python/figs/interpolation', exist_ok=True)
    style_utils.save_plot(fig, f"interpolation/interpolation_comparison_{test_id}.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_test(1)
    plot_test(2)
