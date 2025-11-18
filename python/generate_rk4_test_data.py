import numpy as np

def analytical_solution(t):
    return np.exp(-t**2)

def main():
    t_span = np.linspace(0, 2, 11)
    y = analytical_solution(t_span)

    with open('datasets/rk4_input.txt', 'w') as f:
        for t_val in t_span:
            f.write(f"{t_val}\n")

    with open('datasets/rk4_output.txt', 'w') as f:
        for y_val in y:
            f.write(f"{y_val}\n")

if __name__ == '__main__':
    main()
