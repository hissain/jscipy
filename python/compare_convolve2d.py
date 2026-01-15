import numpy as np
import matplotlib.pyplot as plt
import os

ARTIFACT_DIR = "/Users/hissain/.gemini/antigravity/brain/1b86e521-ac93-4e92-bfd1-e25d910f4a60"

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
        java_res = load_matrix("test_data/java_conv2d_result.txt")
        scipy_res = load_matrix("test_data/conv2d_out_full_2.txt")
        
        diff = np.abs(java_res - scipy_res)
        rmse = np.sqrt(np.mean(diff**2))
        max_diff = np.max(diff)

        print(f"RMSE: {rmse}")
        print(f"Max Diff: {max_diff}")

        fig, axes = plt.subplots(1, 3, figsize=(15, 5))
        
        im0 = axes[0].imshow(java_res, cmap='viridis')
        axes[0].set_title("Java convolve2d")
        plt.colorbar(im0, ax=axes[0])
        
        im1 = axes[1].imshow(scipy_res, cmap='viridis')
        axes[1].set_title("SciPy convolve2d")
        plt.colorbar(im1, ax=axes[1])
        
        im2 = axes[2].imshow(diff, cmap='inferno')
        axes[2].set_title(f"Absolute Difference\n(Max: {max_diff:.2e})")
        plt.colorbar(im2, ax=axes[2])

        plt.tight_layout()
        
        output_path = os.path.join(ARTIFACT_DIR, "convolve2d_comparison.png")
        plt.savefig(output_path)
        print(f"Comparison saved to {output_path}")

    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
