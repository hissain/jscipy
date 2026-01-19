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
        "windows_bartlett_M51_sym_java.txt",
        "windows_flattop_M51_sym_java.txt",
        "windows_bohman_M51_sym_java.txt"
    ]
    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    dataset_dir = os.path.join(script_dir, "../../datasets")
    
    for filename in targets:
        java_path = os.path.join(dataset_dir, filename)
        if not os.path.exists(java_path):
            print(f"Skipping {filename}, not found.")
            continue

        source_filename = filename.replace("_java.txt", ".txt")
        
        # Parse info
        parts = filename.split('_')
        name = parts[1]
        length = parts[2]
        mode = parts[3]
        
        title = f"Window Comparison: {name} {length} {mode}"
        
        java_data = np.loadtxt(java_path)
        source_data = load_data(source_filename)
        
        if source_data is None:
            continue
            
        fig, ax = plt.subplots(figsize=style_utils.FIG_SIZE_WIDE)
        ax.plot(source_data, label='SciPy (Golden Master)', linewidth=2, alpha=0.8)
        ax.plot(java_data, label='jSciPy (Java)', linestyle='--', linewidth=2)
        ax.set_title(title)
        ax.set_xlabel('Sample')
        ax.set_ylabel('Amplitude')
        ax.legend()
        ax.grid(True)
        
        # style_utils.save_plot handles directory and theme suffix
        # We provide the base name *without* suffix
        out_name = f"plot_window_{name}.png"
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
