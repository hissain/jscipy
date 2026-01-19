import numpy as np
import matplotlib.pyplot as plt
import os
import glob
import style_utils

def load_data(filename):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    dataset_dir = os.path.join(script_dir, "../../datasets")
    filepath = os.path.join(dataset_dir, filename)
    if not os.path.exists(filepath):
        print(f"File not found: {filepath}")
        return None
    return np.loadtxt(filepath)

def plot_comparisons():
    # plot specific representative windows
    targets = [
        ("windows_bartlett_M51_sym_java.txt", "Bartlett Window (M=51)"),
        ("windows_flattop_M51_sym_java.txt", "Flat Top Window (M=51)"),
        ("windows_bohman_M51_sym_java.txt", "Bohman Window (M=51)")
    ]
    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    dataset_dir = os.path.join(script_dir, "../../datasets")
    
    # Create a 3-row, 1-column subplot
    fig, axes = plt.subplots(3, 1, figsize=(10, 12), sharex=True)
    
    if len(targets) != 3:
        print("Error: Expected exactly 3 targets for this layout")
        return

    for i, (filename, title) in enumerate(targets):
        ax = axes[i]
        java_path = os.path.join(dataset_dir, filename)
        
        if not os.path.exists(java_path):
            print(f"Skipping {filename}, not found.")
            continue

        source_filename = filename.replace("_java.txt", ".txt")
        java_data = np.loadtxt(java_path)
        source_data = load_data(source_filename)
        
        if source_data is None:
            continue
            
        ax.plot(source_data, label='SciPy (Golden Master)', linewidth=2.5, alpha=0.7)
        ax.plot(java_data, label='jSciPy (Java)', linestyle='--', linewidth=2.0)
        ax.set_title(title)
        ax.set_ylabel('Amplitude')
        ax.legend(loc='upper right')
        ax.grid(True)
        
        # Only set xlabel on bottom plot
        if i == 2:
            ax.set_xlabel('Sample')

    plt.tight_layout()
    out_name = "windows_comparison.png"
    style_utils.save_plot(fig, out_name)
    plt.close(fig)

if __name__ == "__main__":
    # Ensure figs dir exists
    script_dir = os.path.dirname(os.path.abspath(__file__))
    figs_dir = os.path.join(script_dir, "../figs")
    if not os.path.exists(figs_dir):
        os.makedirs(figs_dir)
        
    style_utils.apply_style()
    plot_comparisons()
