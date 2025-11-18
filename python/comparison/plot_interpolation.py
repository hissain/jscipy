import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(test_id):
    x = read_data_file(f'datasets/interpolation_input_x_{test_id}.txt')
    y = read_data_file(f'datasets/interpolation_input_y_{test_id}.txt')
    new_x = read_data_file(f'datasets/interpolation_input_new_x_{test_id}.txt')
    
    linear_y_python = read_data_file(f'datasets/interpolation_output_linear_{test_id}.txt')
    cubic_y_python = read_data_file(f'datasets/interpolation_output_cubic_{test_id}.txt')
    
    linear_y_java = read_data_file(f'datasets/interpolation_output_linear_java_{test_id}.txt')
    cubic_y_java = read_data_file(f'datasets/interpolation_output_cubic_java_{test_id}.txt')

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 8), sharex=True)
    
    ax1.plot(x, y, 'o', label='Original Data')
    ax1.plot(new_x, linear_y_python, label='Python Linear')
    ax1.plot(new_x, linear_y_java, '--', label='Java Linear')
    ax1.legend()
    ax1.set_title(f"Linear Interpolation Comparison for test {test_id}")

    ax2.plot(x, y, 'o', label='Original Data')
    ax2.plot(new_x, cubic_y_python, label='Python Cubic')
    ax2.plot(new_x, cubic_y_java, '--', label='Java Cubic')
    ax2.legend()
    ax2.set_title(f"Cubic Interpolation Comparison for test {test_id}")

    plt.tight_layout()
    plt.savefig(f"python/figs/interpolation_comparison_{test_id}.png")
    plt.close()

if __name__ == '__main__':
    plot_test(1)
    plot_test(2)
