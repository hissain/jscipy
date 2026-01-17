import os
import numpy as np
import matplotlib.pyplot as plt
import style_utils

style_utils.apply_style()

def read_data_file(filename):
    if not os.path.exists(filename):
         # Try prepending datasets/ if just filename given
         filename = os.path.join("datasets", filename)
    
    with open(filename, 'r') as f:
        data = [float(line.strip()) for line in f if line.strip()]
    return data

def plot_test(test_id):
    try:
        signal_in = read_data_file(f'datasets/resample_{test_id}_input.txt')
        resampled_python = read_data_file(f'datasets/resample_{test_id}_output.txt')
        resampled_java = read_data_file(f'datasets/resample_{test_id}_output_java.txt')
    except FileNotFoundError as e:
        print(f"Skipping test {test_id}: {e}")
        return

    # Time axes for plotting
    t_in = np.linspace(0, 1, len(signal_in))
    t_out = np.linspace(0, 1, len(resampled_python))

    # Plotting
    fig, ax = plt.subplots(figsize=style_utils.FIG_SIZE_WIDE)
    ax.plot(t_in, signal_in, 'o-', label='Original Signal', markersize=4, color='gray', alpha=0.5)
    ax.plot(t_out, resampled_python, 's-', label='Python SciPy', markersize=4, linewidth=1.5, alpha=0.8)
    ax.plot(t_out, resampled_java, 'x--', label='Java jSciPy', markersize=6, linewidth=1.5)
    
    ax.set_title(f"Resampling Comparison (Test {test_id})")
    ax.set_xlabel("Time")
    ax.set_ylabel("Amplitude")
    ax.legend()

    plt.tight_layout()
    style_utils.save_plot(fig, f"resample_comparison_{test_id}.png")
    plt.close(fig)

if __name__ == '__main__':
    plot_test(1)
    plot_test(2)
