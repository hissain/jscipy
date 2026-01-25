import matplotlib.pyplot as plt
import pandas as pd
import os

# Configure matplotlib
plt.rcParams['font.family'] = 'sans-serif'
plt.rcParams['font.sans-serif'] = ['Segoe UI', 'Arial', 'DejaVu Sans']

OUTPUT_FILE_TEMPLATE = 'python/figs/comparison_summary_{}.png'

def generate_table(theme_name):
    # Theme Colors
    if theme_name == "dark":
        bg_color, text_color = "#0d1117", "#c9d1d9"
        header_bg, row_bg = "#161b22", "#0d1117"
        grid_color = "#30363d"
        highlight_color = "#238636" # Green for jSciPy column
    else:
        bg_color, text_color = "#ffffff", "#24292f"
        header_bg, row_bg = "#f6f8fa", "#ffffff"
        grid_color = "#d0d7de"
        highlight_color = "#e6ffed" # Light green background for jSciPy

    # Data from README.md
    headers = ["Feature", "jSciPy", "Commons Math", "JDSP", "TarsosDSP", "IIRJ", "EJML"]
    
    data = [
        ["Primary Focus", "SciPy-style Signal", "General Math/Stats", "Java DSP Toolbox", "Audio Processing", "IIR Filter Only", "Linear Algebra"],
        ["Zero-Phase Filt (filtfilt)", "Yes (SciPy-compat)", "No", "No", "No", "No", "No"],
        ["2D Signal Ops (conv2, fft2)", "Yes", "No", "No", "No", "No", "No"],
        ["SciPy-like API", "High", "Low", "Partial", "No", "No", "No"],
        ["Filtering Caps", "Excellent (IIR+FIR)", "Basic", "Good (IIR/FIR)", "Audio Only", "IIR Only", "No"],
        ["Transforms (FFT/DCT)", "FFT, DCT, Hilbert", "Limited", "FFT + Hilbert", "FFT (Audio)", "No", "No"],
        ["Interpolation", "Yes", "Yes", "Yes", "No", "No", "No"],
        ["ODE Solvers (RK4)", "Yes", "Yes", "No", "No", "No", "No"],
        ["Signal Analysis (Peaks)", "Yes", "No", "Partial", "Partial", "No", "No"],
        ["Welch PSD", "Yes", "No", "No", "No", "No", "No"],
        ["Spectrogram", "Yes", "No", "Yes", "Yes", "No", "No"],
        ["Window Functions", "Yes", "No", "Yes", "Yes", "No", "No"],
        ["Savitzky-Golay", "Yes", "No", "Yes", "No", "No", "No"],
        ["Median Filter", "Yes", "No", "Yes", "No", "No", "No"],
        ["Detrending", "Yes", "No", "Yes", "No", "No", "No"],
        ["Real-Opt FFT (rfft)", "Yes", "No", "Yes", "No", "No", "No"],
        ["STFT / ISTFT", "Yes", "No", "Yes", "Partial", "No", "No"],
        ["1D Conv (Modes)", "Yes", "No", "Partial", "Partial", "No", "No"],
        ["Resampling", "Yes", "No", "Yes", "Yes", "No", "No"],
        ["Cross-Correlation", "Yes", "No", "No", "No", "No", "No"],
        ["Polynomials", "Yes", "Yes", "Yes", "No", "No", "No"]
    ]

    # Render
    # Adjust width based on columns
    fig_width = 14
    fig_height = len(data) * 0.4 + 1.5
    
    fig, ax = plt.subplots(figsize=(fig_width, fig_height))
    ax.set_axis_off()
    fig.patch.set_facecolor(bg_color)

    table = ax.table(
        cellText=data,
        colLabels=headers,
        loc='center',
        cellLoc='left',
        bbox=[0, 0, 1, 1]
    )
    
    table.auto_set_font_size(False)
    table.set_fontsize(10)
    
    # Styling
    for (r, c), cell in table.get_celld().items():
        cell.set_linewidth(0)
        
        # Header
        if r == 0:
            cell.set_facecolor(header_bg)
            cell.set_text_props(weight='bold', color=text_color, ha='center')
            cell.set_height(0.08)
            # Highlight jSciPy header
            if c == 1:
                 cell.set_text_props(weight='bold', color="#238636" if theme_name=="dark" else "#1a7f37")
        else:
            cell.set_height(0.045)
            cell.set_text_props(color=text_color)
            
            # Highlight jSciPy column
            if c == 1: 
                # Gentle highlight
                if theme_name == "light":
                    cell.set_facecolor("#f6fff8") 
                else:
                     cell.set_facecolor("#0f1612") # Very subtle Green tint for Dark
                cell.set_text_props(weight='bold', color=text_color)
            else:
                cell.set_facecolor(row_bg)
                
            # Bottom border
            cell.set_edgecolor(grid_color)
            cell.set_linewidth(0.5)
            cell.set_linestyle('-')

    plt.title("jSciPy vs. Other Java Libraries", color=text_color, pad=10, weight='bold', size=14)
    
    output_path = OUTPUT_FILE_TEMPLATE.format(theme_name)
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    plt.savefig(output_path, dpi=300, bbox_inches='tight', facecolor=bg_color)
    print(f"Generated {theme_name} comparison table at {output_path}")
    plt.close()

def main():
    generate_table("light")
    generate_table("dark")

if __name__ == "__main__":
    main()
