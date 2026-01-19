import os
import numpy as np

def analytical_solution(t):
    return np.exp(-t**2)

def main():
    t_span = np.linspace(0, 2, 11)
    y = analytical_solution(t_span)
    
    # Create output directory
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../datasets/rk4')
    os.makedirs(output_dir, exist_ok=True)

    with open(os.path.join(output_dir, 'rk4_input.txt'), 'w') as f:
        for t_val in t_span:
            f.write(f"{t_val}\n")

    with open(os.path.join(output_dir, 'rk4_output.txt'), 'w') as f:
        for y_val in y:
            f.write(f"{y_val}\n")

if __name__ == '__main__':
    main()
