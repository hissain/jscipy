import json
import matplotlib.pyplot as plt
import pandas as pd
import os
import numpy as np

# Configure matplotlib
plt.rcParams['font.family'] = 'sans-serif'
plt.rcParams['font.sans-serif'] = ['Segoe UI', 'Arial', 'DejaVu Sans']

METRICS_FILE = 'datasets/test_metrics.json'
OUTPUT_FILE_TEMPLATE = 'python/figs/accuracy_summary_{}.png'

def format_rmse(val):
    if val == 0:
        return "< 1e-18"
    
    threshold = 1.0
    exponent = int(np.floor(np.log10(val)))
    threshold = 10**exponent
    
    # Rounding logic for cleaner inequality
    if val / threshold > 5:
            threshold *= 10
    elif val / threshold > 1:
            threshold *= 5
    else:
            threshold *= 1
            
    return f"< {threshold:.0e}".replace("e-0", "e-").replace("e+0", "e+")


def get_status(series):
    max_val = series.max()
    if max_val < 1e-9:
        return "Excellent"
    elif max_val < 1e-5:
        return "Good"
    else:
        return "Review"

def extract_subfeature(row):
    # Heuristics to group tests into sub-features
    module = row['module']
    test = row['test']
    
    if module == "Filters":
        if "Butterworth" in test: return "Butterworth"
        if "Chebyshev I " in test: return "Chebyshev I"
        if "Chebyshev II " in test: return "Chebyshev II"
        if "Elliptic" in test: return "Elliptic"
        if "Bessel" in test: return "Bessel"
    
    if module == "FFT":
        if "RFFT" in test or "IRFFT" in test: return "RFFT / IRFFT"
        if "STFT" in test or "ISTFT" in test: return "STFT / ISTFT"
        if "FFT" in test: return "FFT / IFFT"
        
    if module == "2D Ops":
        if "FFT2" in test: return "FFT2 / IFFT2"
        if "Convolve2d" in test: return "Convolve2d"
        
    return test.split()[0] # Fallback to first word

def generate_table(theme_name):
    # Theme Colors
    if theme_name == "dark":
        bg_color, text_color = "#0d1117", "#c9d1d9"
        header_bg, row_bg = "#161b22", "#0d1117"
        grid_color = "#30363d"
        status_colors = {"Excellent": "#238636", "Good": "#d29922", "Review": "#f85149"}
    else:
        bg_color, text_color = "#ffffff", "#24292f"
        header_bg, row_bg = "#f6f8fa", "#ffffff"
        grid_color = "#d0d7de"
        status_colors = {"Excellent": "#1a7f37", "Good": "#9a6700", "Review": "#cf222e"}

    # Load Data
    data = []
    if os.path.exists(METRICS_FILE):
        with open(METRICS_FILE, 'r') as f:
            for line in f:
                if line.strip():
                    try: data.append(json.loads(line))
                    except: continue
    
    if not data:
        print("No metrics found.")
        return

    df = pd.DataFrame(data)
    
    # Create Sub-Feature Column
    df['Sub-Feature'] = df.apply(extract_subfeature, axis=1)
    
    # Aggregate by Module + Sub-Feature
    # We want max RMSE for the group
    summary = df.groupby(['module', 'Sub-Feature'])['rmse'].max().reset_index()
    summary['Status'] = summary['module'].apply(lambda x: "") # Placeholder
    
    # Calculate status per group
    summary['Status_Val'] = summary.apply(lambda row: get_status(pd.Series([row['rmse']])), axis=1)
    summary['RMSE (Approx)'] = summary['rmse'].apply(format_rmse)
    
    # Sort
    module_order = ["Filters", "FFT", "Spectral", "SOS Filt", "2D Ops", "Math", "DCT", "ODE", "Poly", "Signal"]
    summary["module"] = pd.Categorical(summary["module"], categories=module_order, ordered=True)
    summary = summary.sort_values(["module", "Sub-Feature"]).dropna()

    # Prepare Table Data
    display_data = []
    colors = []
    
    headers = ["Module", "Feature", "RMSE (Approx)", "Status"]
    
    current_module = None
    
    for i, row in summary.iterrows():
        module_label = row['module'] if row['module'] != current_module else ""
        current_module = row['module']
        
        status_txt = row['Status_Val']
        display_data.append([module_label, row['Sub-Feature'], row['RMSE (Approx)'], status_txt])
        
        # Row Color
        c_status = status_colors.get(status_txt, text_color)
        colors.append([
            (text_color, row_bg), # Module
            (text_color, row_bg), # Feature
            (text_color, row_bg), # RMSE
            (c_status if theme_name == "dark" else "white", status_colors.get(status_txt)) # Status
        ])

    # Render
    fig, ax = plt.subplots(figsize=(10, len(display_data) * 0.5 + 1.2))
    ax.set_axis_off()
    fig.patch.set_facecolor(bg_color)

    table = ax.table(
        cellText=display_data,
        colLabels=headers,
        loc='center',
        cellLoc='left',
        bbox=[0, 0, 1, 1]
    )
    
    table.auto_set_font_size(False)
    table.set_fontsize(11)
    
    for (r, c), cell in table.get_celld().items():
        cell.set_linewidth(0)
        
        if r == 0: # Header
            cell.set_facecolor(header_bg)
            cell.set_text_props(weight='bold', color=text_color)
            cell.set_height(0.12)
            cell.set_text_props(ha='center')
        else:
            cell.set_height(0.08)
            cell_metrics = colors[r-1][c]
            
            if c == 3: # Status
                cell.set_text_props(color="white", weight='bold', ha='center')
                cell.set_facecolor(status_colors.get(display_data[r-1][3]))
                cell.set_edgecolor(bg_color)
                cell.set_linewidth(2)
            else:
                cell.set_text_props(color=text_color)
                cell.set_facecolor(row_bg)
                
            # Add dividers between modules
            if c == 0 and display_data[r-1][0] != "":
                cell.set_text_props(weight='bold')
            
            # Bottom border
            cell.set_edgecolor(grid_color)
            cell.set_linewidth(0.5)
            cell.set_linestyle('-')

    plt.title(f"jSciPy Validation Details", color=text_color, pad=10, weight='bold')
    
    output_path = OUTPUT_FILE_TEMPLATE.format(theme_name)
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    plt.savefig(output_path, dpi=300, bbox_inches='tight', facecolor=bg_color)
    print(f"Generated {theme_name} at {output_path}")
    plt.close()

def main():
    generate_table("light")
    generate_table("dark")

if __name__ == "__main__":
    main()
