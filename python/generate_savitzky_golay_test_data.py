import numpy as np
from savitzky_golay import savitzky_golay_smoothing, savitzky_golay_differentiation
import os

def generate_savitzky_golay_test_data(test_id, window_length, polyorder, num_samples=500, noise_level=0.1):
    """
    Generates test data for Savitzky-Golay smoothing and differentiation.

    Parameters
    ----------
    test_id : str
        A unique identifier for the test data files.
    window_length : int
        The length of the filter window.
    polyorder : int
        The order of the polynomial used to fit the samples.
    num_samples : int, optional
        The number of data points to generate. Default is 500.
    noise_level : float, optional
        The standard deviation of the Gaussian noise added to the signal. Default is 0.1.
    """
    t = np.linspace(-4 * np.pi, 4 * np.pi, num_samples)
    clean_signal = np.sin(t)
    noisy_signal = clean_signal + np.random.randn(num_samples) * noise_level

    # Smoothing
    smoothed_output = savitzky_golay_smoothing(noisy_signal, window_length, polyorder)

    # Differentiation
    delta = t[1] - t[0]
    differentiated_output = savitzky_golay_differentiation(noisy_signal, window_length, polyorder, deriv=1, delta=delta)

    # Save data
    output_dir = "datasets"
    os.makedirs(output_dir, exist_ok=True)

    input_filename = os.path.join(output_dir, f"savitzky_golay_input{test_id}.txt")
    smoothing_output_filename = os.path.join(output_dir, f"savitzky_golay_smoothing_output{test_id}.txt")
    differentiation_output_filename = os.path.join(output_dir, f"savitzky_golay_differentiation_output{test_id}.txt")

    np.savetxt(input_filename, noisy_signal)
    np.savetxt(smoothing_output_filename, smoothed_output)
    np.savetxt(differentiation_output_filename, differentiated_output)

    print(f"Generated test data for Savitzky-Golay (ID: {test_id})")
    print(f"  Input: {input_filename}")
    print(f"  Smoothing Output: {smoothing_output_filename}")
    print(f"  Differentiation Output: {differentiation_output_filename}")

if __name__ == '__main__':
    # Generate a few test cases
    np.random.seed(42) # for reproducibility

    generate_savitzky_golay_test_data(test_id="1", window_length=51, polyorder=3)
    generate_savitzky_golay_test_data(test_id="2", window_length=31, polyorder=2)
    generate_savitzky_golay_test_data(test_id="3", window_length=71, polyorder=5)
