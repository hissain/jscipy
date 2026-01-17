import matplotlib.pyplot as plt
import seaborn as sns
import os

# --- Configuration ---
FIG_SIZE_WIDE = (10, 6)
FIG_SIZE_SQUARE = (8, 8)
FIG_SIZE_2D = (16, 5) # Standardized for 3-panel 2D comparisons
DPI = 300
FONT_SCALE = 1.1
PALETTE = "muted"
STYLE = "darkgrid" # Default seaborn theme (grey background with white grids)

def apply_style():
    """Applies the standard project plotting style."""
    sns.set_theme(style=STYLE, palette=PALETTE, font_scale=FONT_SCALE)
    plt.rcParams['font.family'] = 'sans-serif'
    plt.rcParams['axes.titlesize'] = 14
    plt.rcParams['axes.labelsize'] = 12
    plt.rcParams['xtick.labelsize'] = 10
    plt.rcParams['ytick.labelsize'] = 10
    plt.rcParams['legend.fontsize'] = 10
    plt.rcParams['figure.titlesize'] = 16
    plt.rcParams['image.cmap'] = 'viridis' # Consistent colormap for images

def save_plot(fig, filename):
    """Saves the plot to the standardized figures directory."""
    # Ensure directory exists
    figs_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../figs")
    os.makedirs(figs_dir, exist_ok=True)
    
    output_path = os.path.join(figs_dir, filename)
    fig.savefig(output_path, dpi=DPI, bbox_inches='tight')
    print(f"Saved plot to {output_path}")

def get_colors():
    """Returns the current seaborn color palette."""
    return sns.color_palette(PALETTE)
