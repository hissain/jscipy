import numpy as np
import matplotlib.pyplot as plt
import os
import style_utils

style_utils.apply_style()

def load_data(test_id, prefix=""):
    data_dir = "../../datasets/peak_properties/"
    path = os.path.dirname(os.path.abspath(__file__)) + "/" + data_dir
    
    try:
        x = np.loadtxt(path + f"{test_id}_input.txt")
        peaks = np.loadtxt(path + f"{test_id}_peaks.txt", dtype=int)
        
        prominences = np.loadtxt(path + f"{test_id}_prominences.txt")
        left_bases = np.loadtxt(path + f"{test_id}_left_bases.txt", dtype=int)
        right_bases = np.loadtxt(path + f"{test_id}_right_bases.txt", dtype=int)
        
        # Widths (default rel_height 0.5)
        width_prefix = prefix if prefix else f"{test_id}_"
        widths = np.loadtxt(path + f"{width_prefix}widths.txt")
        width_heights = np.loadtxt(path + f"{width_prefix}width_heights.txt")
        left_ips = np.loadtxt(path + f"{width_prefix}left_ips.txt")
        right_ips = np.loadtxt(path + f"{width_prefix}right_ips.txt")
        
        return x, peaks, prominences, left_bases, right_bases, widths, width_heights, left_ips, right_ips
    except Exception as e:
        print(f"Error loading data for {test_id}: {e}")
        return None

def plot_peak_properties(test_id, title_suffix=""):
    data = load_data(test_id)
    if data is None:
        return

    x, peaks, prominences, left_bases, right_bases, widths, width_heights, left_ips, right_ips = data
    
    fig, ax = plt.subplots(figsize=(10, 6))
    
    # Plot signal
    ax.plot(x, label='Signal')
    
    # Plot Peaks
    ax.plot(peaks, x[peaks], "x", label='Peaks')
    
    # Plot Prominences
    for i in range(len(peaks)):
        # Vertical line for prominence
        ax.vlines(x=peaks[i], ymin=x[peaks][i] - prominences[i], ymax=x[peaks][i], color='C2', alpha=0.5)
        # Horizontal line for bases (contour)
        contour_height = x[peaks][i] - prominences[i]
        ax.hlines(y=contour_height, xmin=left_bases[i], xmax=right_bases[i], color='C2', linestyle='--', alpha=0.5)
        
    # Plot Widths
    for i in range(len(peaks)):
        ax.hlines(y=width_heights[i], xmin=left_ips[i], xmax=right_ips[i], color='C3', linewidth=2)

    ax.set_title(f'Peak Properties Analysis {title_suffix}')
    ax.set_xlabel('Sample Index')
    ax.set_ylabel('Amplitude')
    ax.legend()
    
    output_name = f"peak_properties/{test_id}_analysis.png"
    style_utils.save_plot(fig, output_name)
    plt.close(fig)

if __name__ == "__main__":
    plot_peak_properties("test1", "(Multi-Peak)")
    plot_peak_properties("test2_chirp", "(Chirp)")
    print("Peak properties plots generated.")
