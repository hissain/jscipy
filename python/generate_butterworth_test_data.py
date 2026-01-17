import numpy as np
from scipy import signal
import os

def save_data(test_id, input_signal, filtered_signal):
    output_dir = "datasets"
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    np.savetxt(os.path.join(output_dir, f"butterworth_{test_id}_input.txt"), input_signal, fmt='%.18e')
    np.savetxt(os.path.join(output_dir, f"butterworth_{test_id}_output.txt"), filtered_signal, fmt='%.18e')

def generate_simple_tone(test_id, order, cutoff, sample_rate, num_samples):
    print(f"Generating simple tone for test {test_id} (Order: {order}, Cutoff: {cutoff})")
    t = np.linspace(0, 1, num_samples, False)
    # Simple sine wave
    input_signal = np.sin(2 * np.pi * 10 * t) + 0.5 * np.random.randn(num_samples)
    
    b, a = signal.butter(order, cutoff, fs=sample_rate, btype='low', analog=False)
    filtered_signal = signal.filtfilt(b, a, input_signal)
    
    save_data(test_id, input_signal, filtered_signal)

def generate_multi_tone(test_id, order, cutoff, sample_rate, num_samples):
    print(f"Generating multi-tone for test {test_id} (Order: {order}, Cutoff: {cutoff})")
    t = np.linspace(0, 1, num_samples, False)
    # Mix of 5Hz, 15Hz, 30Hz. Cutoff is usually around 10-20Hz, so 30Hz should be attenuated.
    input_signal = (np.sin(2 * np.pi * 5 * t) + 
                    0.5 * np.sin(2 * np.pi * 15 * t) + 
                    0.2 * np.sin(2 * np.pi * 30 * t))
    
    b, a = signal.butter(order, cutoff, fs=sample_rate, btype='low', analog=False)
    filtered_signal = signal.filtfilt(b, a, input_signal)
    
    save_data(test_id, input_signal, filtered_signal)

def generate_chirp(test_id, order, cutoff, sample_rate, num_samples):
    print(f"Generating chirp for test {test_id} (Order: {order}, Cutoff: {cutoff})")
    t = np.linspace(0, 1, num_samples, False)
    # Chirp from 1Hz to 50Hz
    input_signal = signal.chirp(t, f0=1, t1=1, f1=50, method='linear')
    
    b, a = signal.butter(order, cutoff, fs=sample_rate, btype='low', analog=False)
    filtered_signal = signal.filtfilt(b, a, input_signal)
    
    save_data(test_id, input_signal, filtered_signal)

def generate_impulse(test_id, order, cutoff, sample_rate, num_samples):
    print(f"Generating impulse for test {test_id} (Order: {order}, Cutoff: {cutoff})")
    input_signal = np.zeros(num_samples)
    input_signal[num_samples // 2] = 1.0 # Unit impulse in the middle
    
    b, a = signal.butter(order, cutoff, fs=sample_rate, btype='low', analog=False)
    filtered_signal = signal.filtfilt(b, a, input_signal)
    
    save_data(test_id, input_signal, filtered_signal)

if __name__ == "__main__":
    sample_rate = 250
    num_samples = 250
    cutoff = 20 # Hz

    # original simple tests, keeping IDs for compatibility or just reusing
    generate_simple_tone("simple_o2", 2, cutoff, sample_rate, num_samples)
    generate_simple_tone("simple_o4", 4, cutoff, sample_rate, num_samples)

    # New complex tests
    generate_multi_tone("multitone_o4", 4, cutoff, sample_rate, num_samples)
    generate_chirp("chirp_o4", 4, cutoff, sample_rate, num_samples)
    generate_impulse("impulse_o4", 4, cutoff, sample_rate, num_samples)
