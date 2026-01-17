import matplotlib.pyplot as plt
import numpy as np
import os

def load_data(path):
    return np.loadtxt(path)

def plot_comparison():
    # Load data
    try:
        java_out = load_data('datasets/bessel_output_java.txt')
        scipy_out = load_data('datasets/bessel_output_scipy.txt')
        signal_in = load_data('datasets/bessel_input.txt')
    except Exception as e:
        print(f"Error loading data: {e}")
        return

    t = np.linspace(0, 1.0, len(signal_in))

    plt.figure(figsize=(10, 6))
    
    # Plot Input
    plt.plot(t, signal_in, 'k--', alpha=0.3, label='Input Signal')
    
    # Plot SciPy (Thick line)
    plt.plot(t, scipy_out, 'b-', linewidth=4, alpha=0.5, label='SciPy Reference')
    
    # Plot Java (Thin line on top)
    plt.plot(t, java_out, 'r-', linewidth=1.5, label='Java Implementation')

    plt.title('Bessel Filter Comparison (Java vs SciPy)')
    plt.xlabel('Time (s)')
    plt.ylabel('Amplitude')
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.tight_layout()

    os.makedirs('python/figs', exist_ok=True)
    output_path = 'python/figs/bessel_comparison.png'
    plt.savefig(output_path, dpi=150)
    print(f"Saved plot to {output_path}")

if __name__ == "__main__":
    plot_comparison()
