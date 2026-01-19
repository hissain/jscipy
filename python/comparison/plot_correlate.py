import matplotlib.pyplot as plt
import numpy as np
import os
import glob

# Set style
plt.style.use('dark_background')
plt.rcParams['figure.facecolor'] = '#1e1e1e'
plt.rcParams['axes.facecolor'] = '#1e1e1e'
plt.rcParams['text.color'] = '#e0e0e0'
plt.rcParams['axes.labelcolor'] = '#e0e0e0'
plt.rcParams['xtick.color'] = '#e0e0e0'
plt.rcParams['ytick.color'] = '#e0e0e0'
plt.rcParams['grid.color'] = '#444444'

def load_data(filename):
    return np.loadtxt(filename)

def plot_comparison(test_id):
    datasets_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets")
    figs_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../figs")
    if not os.path.exists(figs_dir):
        os.makedirs(figs_dir)

    expected_file = os.path.join(datasets_dir, f"{test_id}_output.txt")
    actual_file = os.path.join(datasets_dir, f"{test_id}_output_java.txt")
    
    if not os.path.exists(expected_file) or not os.path.exists(actual_file):
        print(f"Skipping {test_id}: Missing data files.")
        return

    expected = load_data(expected_file)
    actual = load_data(actual_file)
    
    plt.figure(figsize=(10, 6))
    plt.plot(expected, 'g-', linewidth=2, label='SciPy (Expected)', alpha=0.7)
    # Plot actual with a slight offset or different style to see overlap
    plt.plot(actual, 'r--', linewidth=2, label='Java (Actual)')
    
    plt.title(f'Cross-Correlation: {test_id}')
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    output_path = os.path.join(figs_dir, f"correlate_comparison_{test_id}.png")
    plt.savefig(output_path)
    plt.close()
    print(f"Generated plot: {output_path}")

if __name__ == "__main__":
    test_ids = [
        "correlate_basic_full", "correlate_basic_same", "correlate_basic_valid",
        "correlate_random_full", "correlate_random_same", "correlate_random_valid"
    ]
    
    for tid in test_ids:
        plot_comparison(tid)
