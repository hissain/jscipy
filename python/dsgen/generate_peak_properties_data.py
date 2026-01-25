import numpy as np
from scipy import signal
import os

def save_data(test_id, x, peaks, prominences, left_bases, right_bases, widths=None, width_heights=None, left_ips=None, right_ips=None, rel_height=0.5):
    output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../datasets/peak_properties")
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    prefix = f"{test_id}_"
    
    # Save input signal and peaks
    if x is not None:
        np.savetxt(os.path.join(output_dir, f"{prefix}input.txt"), x, fmt='%.18e')
    if peaks is not None:
        np.savetxt(os.path.join(output_dir, f"{prefix}peaks.txt"), peaks, fmt='%d')
    
    # Save prominence data
    if prominences is not None:
        np.savetxt(os.path.join(output_dir, f"{prefix}prominences.txt"), prominences, fmt='%.18e')
        np.savetxt(os.path.join(output_dir, f"{prefix}left_bases.txt"), left_bases, fmt='%d')
        np.savetxt(os.path.join(output_dir, f"{prefix}right_bases.txt"), right_bases, fmt='%d')
    
    # Save width data
    if widths is not None:
        # Include rel_height in filename if different from default
        width_prefix = prefix
        if rel_height != 0.5:
            width_prefix = f"{prefix}rh{int(rel_height*100)}_"
            
        np.savetxt(os.path.join(output_dir, f"{width_prefix}widths.txt"), widths, fmt='%.18e')
        np.savetxt(os.path.join(output_dir, f"{width_prefix}width_heights.txt"), width_heights, fmt='%.18e')
        np.savetxt(os.path.join(output_dir, f"{width_prefix}left_ips.txt"), left_ips, fmt='%.18e')
        np.savetxt(os.path.join(output_dir, f"{width_prefix}right_ips.txt"), right_ips, fmt='%.18e')

def generate_multi_peak_signal():
    """Generates a signal with multiple peaks of varying width and prominence."""
    x = np.linspace(0, 100, 1000)
    # Background
    sig = 0.1 * np.sin(x) 
    # Add peaks
    sig += 5 * np.exp(-(x - 20)**2 / 10)  # Wide peak
    sig += 5 * np.exp(-(x - 40)**2 / 2)   # Narrow peak
    sig += 3 * np.exp(-(x - 70)**2 / 20)  # Another wide peak
    # Add a small peak on the shoulder of a larger one
    sig += 2 * np.exp(-(x - 25)**2 / 2)
    return sig

def run_tests():
    print("Generating peak properties test data...")
    
    # --- Test Case 1: Simple Multi-Peak Signal ---
    sig1 = generate_multi_peak_signal()
    peaks1, _ = signal.find_peaks(sig1)
    
    # 1. Prominences
    prominences, left_bases, right_bases = signal.peak_prominences(sig1, peaks1)
    
    # 2. Widths (default rel_height=0.5)
    widths, width_heights, left_ips, right_ips = signal.peak_widths(sig1, peaks1, rel_height=0.5, prominence_data=(prominences, left_bases, right_bases))
    
    save_data("test1", sig1, peaks1, prominences, left_bases, right_bases, widths, width_heights, left_ips, right_ips)
    
    # 3. Widths (rel_height=1.0 - full width at base)
    widths_full, width_heights_full, left_ips_full, right_ips_full = signal.peak_widths(sig1, peaks1, rel_height=1.0, prominence_data=(prominences, left_bases, right_bases))
    save_data("test1", None, None, None, None, None, widths_full, width_heights_full, left_ips_full, right_ips_full, rel_height=1.0)

    # --- Test Case 2: Chirp Signal (Increasing Frequency) ---
    t = np.linspace(0, 10, 500)
    sig2 = signal.chirp(t, f0=1, t1=10, f1=5, method='linear', phi=0) * np.exp(-0.1*t) # Decaying amplitude
    peaks2, _ = signal.find_peaks(sig2)
    
    prominences2, left_bases2, right_bases2 = signal.peak_prominences(sig2, peaks2)
    widths2, width_heights2, left_ips2, right_ips2 = signal.peak_widths(sig2, peaks2, rel_height=0.5)
    
    save_data("test2_chirp", sig2, peaks2, prominences2, left_bases2, right_bases2, widths2, width_heights2, left_ips2, right_ips2)

    # --- Test Case 3: Edge Cases (Flat peaks, Plateaus - simplified for now) ---
    # Java FindPeaks basics finds local maxima. peak_prominences handles plateaus if peak index is given.
    # Let's stick to standard peaks first. 
    
    print("Done generating data.")

if __name__ == "__main__":
    run_tests()
