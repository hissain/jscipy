import numpy as np
import matplotlib.pyplot as plt
import os
import glob

def load_data(filename):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    dataset_dir = os.path.join(script_dir, "../../datasets")
    filepath = os.path.join(dataset_dir, filename)
    if not os.path.exists(filepath):
        print(f"File not found: {filepath}")
        return None
    return np.loadtxt(filepath)

def plot_comparisons():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    figs_dir = os.path.join(script_dir, "../figs")
    if not os.path.exists(figs_dir):
        os.makedirs(figs_dir)

    # Find all java output files to verify against their sources
    # Pattern: windows_<name>_M<length>_<type>_java.txt
    # Source: windows_<name>_M<length>_<type>.txt
    
    # plot specific representative windows
    targets = [
        "windows_bartlett_M51_sym_java.txt",
        "windows_flattop_M51_sym_java.txt",
        "windows_bohman_M51_sym_java.txt"
    ]
    
    dataset_dir = os.path.join(script_dir, "../../datasets")
    
    for filename in targets:
        java_path = os.path.join(dataset_dir, filename)
        if not os.path.exists(java_path):
            print(f"Skipping {filename}, not found.")
            continue

        source_filename = filename.replace("_java.txt", ".txt")
        
        # Parse info for title: windows_bartlett_M51_sym_java.txt
        parts = filename.split('_')
        name = parts[1]
        length = parts[2]
        mode = parts[3]
        
        title = f"Window Comparison: {name} {length} {mode}"
        
        java_data = np.loadtxt(java_path)
        source_data = load_data(source_filename)
        
        if source_data is None:
            continue
            
        plt.figure(figsize=(10, 6))
        plt.plot(source_data, 'b-', linewidth=2, label='SciPy (Golden Master)')
        plt.plot(java_data, 'r--', linewidth=2, label='jSciPy (Java)')
        plt.title(title)
        plt.xlabel('Sample')
        plt.ylabel('Amplitude')
        plt.legend()
        plt.grid(True)
        
        out_name = f"plot_window_{name}.png"
        save_path = os.path.join(figs_dir, out_name)
        plt.savefig(save_path)
        print(f"Saved plot to {save_path}")
        plt.close()

if __name__ == "__main__":
    plot_comparisons()
