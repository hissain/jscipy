import numpy as np
import os

# Set seed for reproducibility
np.random.seed(42)

DATA_DIR = "datasets/poly"
os.makedirs(DATA_DIR, exist_ok=True)

def save_data(filename, data):
    filepath = os.path.join(DATA_DIR, filename)
    with open(filepath, 'w', newline='\n') as f:
        np.savetxt(f, data, fmt='%.16e')
    print(f"Saved {filepath}")

def generate_poly_test_data():
    # Case 1: Exact Fit (3 points, degree 2)
    # y = 2x^2 - 3x + 1
    # x = [0, 1, 2] -> y = [1, 0, 3]
    x1 = np.array([0.0, 1.0, 2.0])
    coeffs_true = np.array([2.0, -3.0, 1.0]) # Highest degree first for np.polyfit output
    y1 = np.polyval(coeffs_true, x1) # 2x^2 - 3x + 1
    
    # Fit
    coeffs1 = np.polyfit(x1, y1, 2)
    
    save_data("polyfit_exact_x.txt", x1)
    save_data("polyfit_exact_y.txt", y1)
    save_data("polyfit_exact_coeffs.txt", coeffs1)

    # Evaluate on new points
    x1_val = np.array([-1.0, 0.5, 3.0])
    y1_val = np.polyval(coeffs1, x1_val)
    save_data("polyval_exact_x.txt", x1_val)
    save_data("polyval_exact_y.txt", y1_val)


    # Case 2: Least Squares (10 points, degree 2)
    # y = x^2 + noise
    x2 = np.linspace(-5, 5, 10)
    y2 = x2**2 + np.random.normal(0, 0.5, size=len(x2))
    
    coeffs2 = np.polyfit(x2, y2, 2)
    
    save_data("polyfit_lstsq_x.txt", x2)
    save_data("polyfit_lstsq_y.txt", y2)
    save_data("polyfit_lstsq_coeffs.txt", coeffs2)
    
    # Evaluate
    x2_val = np.linspace(-6, 6, 20)
    y2_val = np.polyval(coeffs2, x2_val)
    save_data("polyval_lstsq_x.txt", x2_val)
    save_data("polyval_lstsq_y.txt", y2_val)
    
    print("Polynomial test data generated.")

if __name__ == "__main__":
    generate_poly_test_data()
