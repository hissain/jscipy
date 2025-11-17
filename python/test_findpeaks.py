#!/usr/bin/env python3
"""
Test script for scipy.signal.find_peaks with default parameters
"""

import numpy as np
from scipy.signal import find_peaks


def read_data_file(filename):
    """Read data from a file, one value per line"""
    with open(filename, 'r') as f:
        data = [float(line.strip()) for line in f if line.strip()]
    return np.array(data)


def test_findpeaks(input_file, expected_output_file, test_name):
    """Test find_peaks with given input and expected output"""
    print(f"\n{test_name}")
    print("=" * 60)
    
    # Read input signal
    signal = read_data_file(input_file)
    print(f"Signal length: {len(signal)}")
    
    # Apply find_peaks with default parameters
    peaks, _ = find_peaks(signal)
    
    print(f"Found {len(peaks)} peaks")
    print(f"Peak indices: {peaks}")
    
    # Read expected output
    expected_peaks = read_data_file(expected_output_file).astype(int)
    print(f"\nExpected {len(expected_peaks)} peaks")
    print(f"Expected indices: {expected_peaks}")
    
    # Compare results
    if np.array_equal(peaks, expected_peaks):
        print("\n✓ PASS: Output matches expected results!")
        return True
    else:
        print("\n✗ FAIL: Output does not match expected results")
        print(f"Difference: {set(peaks) - set(expected_peaks)} extra, {set(expected_peaks) - set(peaks)} missing")
        return False


def main():
    """Main test function"""
    print("Testing scipy.signal.find_peaks with default parameters")
    print("=" * 60)
    
    # Test with dataset 1
    test1_pass = test_findpeaks(
        'datasets/findpeaks_input1.txt',   # Signal data (input)
        'datasets/findpeaks_output1.txt',  # Peak indices (output)
        'Test 1: Dataset 1'
    )
    
    # Test with dataset 2
    test2_pass = test_findpeaks(
        'datasets/findpeaks_input2.txt',   # Signal data (input)
        'datasets/findpeaks_output2.txt',  # Peak indices (output)
        'Test 2: Dataset 2'
    )
    

    # Summary
    print("\n" + "=" * 60)
    print("SUMMARY")
    print("=" * 60)
    print(f"Test 1: {'PASS' if test1_pass else 'FAIL'}")
    print(f"Test 2: {'PASS' if test2_pass else 'FAIL'}")
    

    # Test with dataset 3
    test3_pass = test_findpeaks(
        'datasets/findpeaks_input3.txt',   # Signal data (input)
        'datasets/findpeaks_output3.txt',  # Peak indices (output)
        'Test 3: Dataset 3'
    )

    # Test with dataset 4
    test4_pass = test_findpeaks(
        'datasets/findpeaks_input4.txt',   # Signal data (input)
        'datasets/findpeaks_output4.txt',  # Peak indices (output)
        'Test 4: Dataset 4'
    )

    # Test with dataset 5
    test5_pass = test_findpeaks(
        'datasets/findpeaks_input5.txt',   # Signal data (input)
        'datasets/findpeaks_output5.txt',  # Peak indices (output)
        'Test 5: Dataset 5'
    )
    
    # Summary
    print("\n" + "=" * 60)
    print("SUMMARY")
    print("=" * 60)
    print(f"Test 1: {'PASS' if test1_pass else 'FAIL'}")
    print(f"Test 2: {'PASS' if test2_pass else 'FAIL'}")
    print(f"Test 3: {'PASS' if test3_pass else 'FAIL'}")
    print(f"Test 4: {'PASS' if test4_pass else 'FAIL'}")
    print(f"Test 5: {'PASS' if test5_pass else 'FAIL'}")
    
    if test1_pass and test2_pass and test3_pass and test4_pass and test5_pass:
        print("\n✓ All tests passed!")
        return 0
    else:
        print("\n✗ Some tests failed")
        return 1


if __name__ == '__main__':
    exit(main())
