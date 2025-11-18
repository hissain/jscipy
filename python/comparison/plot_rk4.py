import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(input_filename, python_output_filename, java_output_filename):
    t_span = read_data_file(input_filename)
    python_output = read_data_file(python_output_filename)
    java_output = read_data_file(java_output_filename)

    # Plotting
    plt.figure()
    plt.plot(t_span, python_output, label='Python RK4')
    plt.plot(t_span, java_output, label='Java RK4', linestyle='--')
    plt.legend()
    plt.title(f"RK4 Solver Comparison for {input_filename}")
    plt.savefig(f"python/figs/{input_filename.split('/')[-1]}.png")
    plt.close()

if __name__ == '__main__':
    plot_test('datasets/rk4_input.txt', 'datasets/rk4_output.txt', 'datasets/rk4_output_java.txt')
