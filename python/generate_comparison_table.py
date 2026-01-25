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
    # Using multiline headers to save width
    headers = ["Feature", "jSciPy", "Commons\nMath", "JDSP", "Tarsos\nDSP", "IIRJ"]
    
    data = [
        ["Primary Focus", "SciPy-style\nSignal", "General\nMath/Stats", "Java DSP\nToolbox", "Audio\nProcessing", "IIR Filter\nOnly"],
        ["Zero-Phase Filt\n(filtfilt)", "Yes", "No", "No", "No", "No"],
        ["2D Signal Ops\n(conv2, fft2)", "Yes", "No", "No", "No", "No"],
        ["SciPy-like API", "High", "Low", "Partial", "No", "No"],
        ["Filtering Caps", "Excellent\n(IIR+FIR)", "Basic", "Good\n(IIR/FIR)", "Audio\nOnly", "IIR\nOnly"],
        ["Transforms\n(FFT/DCT)", "FFT, DCT,\nHilbert", "Limited", "FFT +\nHilbert", "FFT\n(Audio)", "No"],
        ["Interpolation", "Yes", "Yes", "Yes", "No", "No"],
        ["ODE Solvers (RK4)", "Yes", "Yes", "No", "No", "No"],
        ["Signal Analysis\n(Peaks)", "Yes", "No", "Partial", "Partial", "No"],
        ["Welch PSD", "Yes", "No", "No", "No", "No"],
        ["Spectrogram", "Yes", "No", "Yes", "Yes", "No"],
        ["Window Functions", "Yes", "No", "Yes", "Yes", "No"],
        ["Savitzky-Golay", "Yes", "No", "Yes", "No", "No"],
        ["Median Filter", "Yes", "No", "Yes", "No", "No"],
        ["Detrending", "Yes", "No", "Yes", "No", "No"],
        ["Real-Opt FFT\n(rfft)", "Yes", "No", "Yes", "No", "No"],
        ["STFT / ISTFT", "Yes", "No", "Yes", "Partial", "No"],
        ["1D Conv (Modes)", "Yes", "No", "Partial", "Partial", "No"],
        ["Resampling", "Yes", "No", "Yes", "Yes", "No"],
        ["Cross-Correlation", "Yes", "No", "No", "No", "No"],
        ["Polynomials", "Yes", "Yes", "Yes", "No", "No"]
    ]

    # Render
    # Make it narrower (relative to font) so text appears larger when scaled to width
    fig_width = 12 
    # Increased row height for multilines
    fig_height = len(data) * 0.55 + 2.0
    
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
    table.set_fontsize(12) # Increased from 10
    
    # Styling
    for (r, c), cell in table.get_celld().items():
        cell.set_linewidth(0)
        
        # Header
        if r == 0:
            cell.set_facecolor(header_bg)
            cell.set_text_props(weight='bold', color=text_color, ha='center', va='center')
            cell.set_height(0.12) # Taller header
            # Highlight jSciPy header
            if c == 1:
                 cell.set_text_props(weight='bold', color=highlight_color)
        else:
            cell.set_height(0.065) # Taller rows for potential multiline
            cell.set_text_props(color=text_color, va='center') # Vertically center
            
            # Highlight jSciPy column
            if c == 1: 
                # Gentle highlight
                if theme_name == "light":
                    cell.set_facecolor(highlight_color) 
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
