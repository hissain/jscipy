import os
import numpy as np
import matplotlib.pyplot as plt
import style_utils

style_utils.apply_style()

# Consistent path handling
def get_path(filename):
    return os.path.join("datasets", filename)

def read_data_file(filename):
    path = get_path(filename)
    with open(path, 'r') as f:
        data = [float(line.strip()) for line in f if line.strip()]
    return np.array(data)

def plot_test(test_name, title):
    try:
        input_file = f"{test_name}_input.txt"
        scipy_file = f"{test_name}_output.txt"
        java_file = f"{test_name}_output_java.txt"

        signal_in = read_data_file(input_file)
        scipy_out = read_data_file(scipy_file)
        java_out = read_data_file(java_file)
    except FileNotFoundError as e:
        print(f"Skipping {test_name}: {e}")
        return

    # Create time axis
    t = np.arange(len(signal_in))

    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=style_utils.FIG_SIZE_WIDE, sharex=True)

    # Plot comparison
    ax1.plot(t, signal_in, 'k.', alpha=0.3, label='Input', markersize=2)
    ax1.plot(t, scipy_out, label='SciPy', linewidth=2.0, alpha=0.7)
    ax1.plot(t, java_out, '--', label='Java', linewidth=1.5)
    ax1.set_title(title)
    ax1.set_ylabel('Amplitude')
    ax1.legend()
    ax1.grid(True)

    # Plot residual
    residual = scipy_out - java_out
    rmse = np.sqrt(np.mean(residual**2))
    
    ax2.plot(t, residual, color='red', label=f'Residual (RMSE={rmse:.2e})')
    ax2.set_title('Difference (SciPy - Java)')
    ax2.set_xlabel('Sample Index')
    ax2.set_ylabel('Difference')
    ax2.legend()
    ax2.grid(True)

    plt.tight_layout()
    style_utils.save_plot(fig, f"{test_name}.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_test('savitzky_golay_smoothing', 'Savitzky-Golay Smoothing Comparison')
    plot_test('savitzky_golay_differentiation', 'Savitzky-Golay Differentiation Comparison')
