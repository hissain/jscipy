import numpy as np
import subprocess
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme()

def read_data_file(filename):
    with open(filename, 'r') as f:
        return np.array([float(line.strip()) for line in f])

def run_java_rk4_solver(input_filename):
    classpath = "java/build/libs/jscipy-1.0.0.jar:java/build/classes/java/test" # Main JAR and test classes
    process = subprocess.Popen(['java', '-cp', classpath, 'com.hissain.jscipy.signal.RK4SolverTest', input_filename], stdout=subprocess.PIPE)
    output, _ = process.communicate()
    if not output:
        return np.array([])
    return np.array([float(line) for line in output.decode().strip().splitlines()])

def rk4_solve(f, y0, t_span):
    y = np.zeros(len(t_span))
    y[0] = y0
    for i in range(len(t_span) - 1):
        h = t_span[i+1] - t_span[i]
        t = t_span[i]
        k1 = h * f(t, y[i])
        k2 = h * f(t + h / 2.0, y[i] + k1 / 2.0)
        k3 = h * f(t + h / 2.0, y[i] + k2 / 2.0)
        k4 = h * f(t + h, y[i] + k3)
        y[i+1] = y[i] + (k1 + 2*k2 + 2*k3 + k4) / 6.0
    return y

def differential_equation(t, y):
    return -2 * t * y

def run_test(input_filename):
    t_span = read_data_file(input_filename)

    # Python implementation
    python_output = rk4_solve(differential_equation, 1.0, t_span)

    # Java implementation
    java_output = run_java_rk4_solver(input_filename)

    # Compare outputs
    rmse = np.sqrt(np.mean((python_output - java_output)**2))
    print(f"RMSE for {input_filename}: {rmse}")

    # Plotting
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True)
    ax1.plot(t_span, python_output, label='Python RK4', linewidth=0.5)
    ax1.legend()
    ax1.set_title(f"Python RK4 Solution for {input_filename}")
    ax2.plot(t_span, java_output, label='Java RK4', linewidth=0.5)
    ax2.legend()
    ax2.set_title(f"Java RK4 Solution for {input_filename}")
    plt.savefig(f"python/figs/{input_filename.split('/')[-1]}.png")
    plt.close(fig)

if __name__ == '__main__':
    run_test('datasets/rk4_input.txt')
