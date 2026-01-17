import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def plot_test(input_filename, python_output_filename, java_output_filename, title_suffix=""):
    try:
        if not os.path.exists(java_output_filename):
            print(f"File not found: {java_output_filename}")
            return
        
        signal_in = read_data_file(input_filename)
        python_output = read_data_file(python_output_filename)
        java_output = read_data_file(java_output_filename)

        # Plotting
        fig, (ax1, ax2, ax3) = plt.subplots(3, 1, sharex=True, figsize=(10, 8))
        
        ax1.plot(signal_in, label='Input', linewidth=0.5, color='gray')
        ax1.legend()
        ax1.set_title(f"Input Signal {title_suffix}")
        
        ax2.plot(python_output, label='Python SciPy', linewidth=1.0)
        ax2.plot(java_output, label='Java jSciPy', linestyle=':', linewidth=2.0)
        ax2.legend()
        ax2.set_title("Filtered Output Comparison")

        # Difference
        diff = python_output - java_output
        rmse = np.sqrt(np.mean(diff**2))
        ax3.plot(diff, label=f'Diff (RMSE={rmse:.2e})', color='red', linewidth=0.5)
        ax3.legend()
        ax3.set_title("Difference (Python - Java)")

        plt.tight_layout()
        output_filename = f"python/figs/{input_filename.split('/')[-1]}.png"
        plt.savefig(output_filename)
        print(f"Saved plot to {output_filename}")
        plt.close(fig)
    except Exception as e:
        print(f"Error plotting {input_filename}: {e}")

if __name__ == '__main__':
    datasets = [
        ("simple_o2", "Simple Tone (Ord 2)"),
        ("simple_o4", "Simple Tone (Ord 4)"),
        ("multitone_o4", "Multi-tone (Ord 4)"),
        ("chirp_o4", "Chirp (Ord 4)"),
        ("impulse_o4", "Impulse (Ord 4)")
    ]

    for dataset_id, title in datasets:
        plot_test(
            f'datasets/butterworth_{dataset_id}_input.txt',
            f'datasets/butterworth_{dataset_id}_output.txt',
            f'datasets/butterworth_{dataset_id}_output_java.txt',
            f"({title})"
        )
