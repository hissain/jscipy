import numpy as np
import scipy.signal.windows as win
import os

def save_data(filename, data):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    # datasets is at ../../datasets/windows relative to python/dsgen/
    dataset_dir = os.path.join(script_dir, "../../datasets/windows")
    if not os.path.exists(dataset_dir):
        os.makedirs(dataset_dir)
        
    filepath = os.path.join(dataset_dir, filename)
    np.savetxt(filepath, data, fmt='%.18e')
    print(f"Saved {filepath}")

def generate():
    # Test lengths
    lengths = [10, 51]
    
    # Windows to test
    window_funcs = {
        'bartlett': win.bartlett,
        'triang': win.triang,
        'flattop': win.flattop,
        'parzen': win.parzen,
        'bohman': win.bohman
    }
    
    for name, func in window_funcs.items():
        for M in lengths:
            # Symmetric
            try:
                w_sym = func(M, sym=True)
            except TypeError:
                 # Fallback if specific function doesn't support sym (shouldn't happen for these)
                w_sym = func(M)
                
            filename_sym = f"windows_{name}_M{M}_sym.txt"
            save_data(filename_sym, w_sym)
            
            # Periodic
            try:
                w_periodic = func(M, sym=False)
            except TypeError:
                w_periodic = func(M) # Default/Available
            
            filename_periodic = f"windows_{name}_M{M}_periodic.txt"
            save_data(filename_periodic, w_periodic)

if __name__ == "__main__":
    generate()
