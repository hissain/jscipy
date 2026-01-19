import numpy as np
import scipy.signal as signal
import os

# Ensure the datasets directory exists
output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/detrend")
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

# Dataset 1: Linear Trend + Noise
np.random.seed(42)
t = np.linspace(0, 5, 100)
linear_trend = 2 * t + 1
noise = np.random.normal(0, 0.5, 100)
input_1 = linear_trend + noise

# Dataset 2: Constant Offset + Random Walk
input_2 = np.cumsum(np.random.normal(0, 0.5, 100)) + 10

# Save inputs
np.savetxt(os.path.join(output_dir, "detrend_input_1.txt"), input_1)
np.savetxt(os.path.join(output_dir, "detrend_input_2.txt"), input_2)

# Apply detrend (linear)
output_linear_1 = signal.detrend(input_1, type='linear')
output_linear_2 = signal.detrend(input_2, type='linear')

# Apply detrend (constant)
output_constant_1 = signal.detrend(input_1, type='constant')
output_constant_2 = signal.detrend(input_2, type='constant')

# Save outputs
np.savetxt(os.path.join(output_dir, "detrend_output_linear_1.txt"), output_linear_1)
np.savetxt(os.path.join(output_dir, "detrend_output_linear_2.txt"), output_linear_2)
np.savetxt(os.path.join(output_dir, "detrend_output_constant_1.txt"), output_constant_1)
np.savetxt(os.path.join(output_dir, "detrend_output_constant_2.txt"), output_constant_2)

print("Detrend test data generated.")
