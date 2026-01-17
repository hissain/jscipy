import numpy as np
import matplotlib.pyplot as plt
import os

ARTIFACT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../figs")

def load_matrix(filename):
    with open(filename, 'r') as f:
        # Skip header line (rows cols)
        lines = f.readlines()[1:]
        data = []
        for line in lines:
            data.append([float(x) for x in line.split()])
    return np.array(data)

def main():
    print("Loading data...")
    try:
        script_dir = os.path.dirname(os.path.abspath(__file__))
        datasets_dir = os.path.join(script_dir, '../../datasets')
        
        java_res = load_matrix(os.path.join(datasets_dir, "java_conv2d_result.txt"))
        scipy_res = load_matrix(os.path.join(datasets_dir, "conv2d_out_full_2.txt"))
        
        diff = np.abs(java_res - scipy_res)
        rmse = np.sqrt(np.mean(diff**2))
        max_diff = np.max(diff)

        print(f"RMSE: {rmse}")
        print(f"Max Diff: {max_diff}")

        fig, axes = plt.subplots(1, 2, figsize=(12, 5))
        
        im0 = axes[0].imshow(java_res, cmap='viridis')
        axes[0].set_title("Java convolve2d")
        plt.colorbar(im0, ax=axes[0])
        
        im1 = axes[1].imshow(scipy_res, cmap='viridis')
        axes[1].set_title("SciPy convolve2d")
        plt.colorbar(im1, ax=axes[1])
        
        plt.tight_layout()
        
        output_path = os.path.join(ARTIFACT_DIR, "convolve2d_comparison.png")
        plt.savefig(output_path)
        print(f"Comparison saved to {output_path}")

    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
