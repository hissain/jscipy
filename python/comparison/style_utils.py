import matplotlib.pyplot as plt
import seaborn as sns
import os
import numpy as np

# --- Configuration ---
FIG_SIZE_WIDE = (10, 6)
FIG_SIZE_SQUARE = (8, 8)
FIG_SIZE_2D = (16, 5) # Standardized for 3-panel 2D comparisons
DPI = 300
FONT_SCALE = 1.1
# --- Configuration ---
FIG_SIZE_WIDE = (10, 6)
FIG_SIZE_SQUARE = (8, 8)
FIG_SIZE_2D = (16, 5) # Standardized for 3-panel 2D comparisons
DPI = 300
FONT_SCALE = 1.1

def get_current_theme():
    return os.environ.get("JSCIPY_PLOT_THEME", "light").lower()

def apply_style():
    """Applies the standard project plotting style based on JSCIPY_PLOT_THEME."""
    theme = get_current_theme()
    
    if theme == "dark":
        plt.style.use('dark_background')
        # Custom dark adjustments if needed, though dark_background is usually good
        # Ensure text is white/light
        plt.rcParams['text.color'] = '0.9'
        plt.rcParams['axes.labelcolor'] = '0.9'
        plt.rcParams['xtick.color'] = '0.9'
        plt.rcParams['ytick.color'] = '0.9'
        plt.rcParams['grid.color'] = '0.3'
        sns.set_palette("bright") # Brighter colors pop better on dark
    else:
        # Default light theme
        sns.set_theme(style="darkgrid", palette="muted", font_scale=FONT_SCALE)

    plt.rcParams['font.family'] = 'sans-serif'
    plt.rcParams['axes.titlesize'] = 14
    plt.rcParams['axes.labelsize'] = 12
    plt.rcParams['xtick.labelsize'] = 10
    plt.rcParams['ytick.labelsize'] = 10
    plt.rcParams['legend.fontsize'] = 10
    plt.rcParams['figure.titlesize'] = 16
    plt.rcParams['image.cmap'] = 'viridis' # Consistent colormap for images

def save_plot(fig, filename):
    """Saves the plot to the standardized figures directory with theme suffix."""
    # Ensure directory exists
    figs_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../figs")
    os.makedirs(figs_dir, exist_ok=True)
    
    # Inject theme suffix
    theme = get_current_theme()
    name, ext = os.path.splitext(filename)
    
    # If the filename already has a theme suffix (e.g. manually added), avoid double suffixing?
    # But for now, we assume scripts pass base filenames like "fft_comparison_1.png"
    # We want "fft_comparison_1_light.png" or "fft_comparison_1_dark.png"
    
    output_filename = f"{name}_{theme}{ext}"
    output_path = os.path.join(figs_dir, output_filename)
    
    # Create subdirectory if filename includes a path (e.g., "dct/dct_comparison.png")
    output_dir = os.path.dirname(output_path)
    if output_dir != figs_dir:
        os.makedirs(output_dir, exist_ok=True)
    
    # Transparent background for dark mode can be useful, but 'dark_background' style sets a black facecolor.
    # We'll save with the facecolor defined by the style.
    fig.savefig(output_path, dpi=DPI, bbox_inches='tight')
    print(f"Saved plot to {output_path}")

def get_colors():
    """Returns the current seaborn color palette."""
    return sns.color_palette(PALETTE)

def finalize_diff_plot(ax, signal_data, is_2d=False):
    """
    Sets the Y-axis (or color scale) of the difference plot to match the signal's magnitude.
    This prevents tiny 1e-15 errors from looking like huge fluctuations due to autoscaling.
    """
    # Calculate signal amplitude (max absolute value)
    ampl = np.max(np.abs(signal_data)) if len(signal_data) > 0 else 1.0
    if ampl == 0: ampl = 1.0 # Safety
    
    # Margin factor to ensure peaks don't touch the edge
    margin = 1.1
    limit = ampl * margin
    
    if is_2d:
        # For 2D heatmaps (imshow), we return vmin/vmax to be used by caller
        return (-limit, limit)
    else:
        # For 1D plots, set ylim centered at 0
        ax.set_ylim(-limit, limit)
        # Optional: Add grid to emphasize the 'flatness'
        ax.grid(True, which='both', linestyle='--', linewidth=0.5, alpha=0.5)
