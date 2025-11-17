import numpy as np
from scipy.signal import savgol_filter

def savitzky_golay_smoothing(data, window_length, polyorder, deriv=0, delta=1.0):
    """
    Applies a Savitzky-Golay filter for smoothing.

    Parameters
    ----------
    data : array_like
        The data to be filtered.
    window_length : int
        The length of the filter window (i.e., the number of coefficients).
        `window_length` must be an odd positive integer.
    polyorder : int
        The order of the polynomial used to fit the samples.
        `polyorder` must be less than `window_length`.
    deriv : int, optional
        The order of the derivative to compute. Default is 0 (smoothing).
    delta : float, optional
        The spacing of the samples to which the filter will be applied.
        This is only used if `deriv > 0`. Default is 1.0.

    Returns
    -------
    ndarray
        The filtered data.
    """
    return savgol_filter(data, window_length, polyorder, deriv=deriv, delta=delta)

def savitzky_golay_differentiation(data, window_length, polyorder, deriv=1, delta=1.0):
    """
    Applies a Savitzky-Golay filter for differentiation.

    Parameters
    ----------
    data : array_like
        The data to be filtered.
    window_length : int
        The length of the filter window (i.e., the number of coefficients).
        `window_length` must be an odd positive integer.
    polyorder : int
        The order of the polynomial used to fit the samples.
        `polyorder` must be less than `window_length`.
    deriv : int, optional
        The order of the derivative to compute. Default is 1 (first derivative).
    delta : float, optional
        The spacing of the samples to which the filter will be applied.
        This is only used if `deriv > 0`. Default is 1.0.

    Returns
    -------
    ndarray
        The filtered data (derivative).
    """
    return savgol_filter(data, window_length, polyorder, deriv=deriv, delta=delta)

if __name__ == '__main__':
    # Example Usage
    t = np.linspace(-4 * np.pi, 4 * np.pi, 500)
    data = np.sin(t) + np.random.randn(len(t)) * 0.1

    # Smoothing
    smoothed_data = savitzky_golay_smoothing(data, window_length=51, polyorder=3)
    print("Smoothed data (first 10 points):", smoothed_data[:10])

    # Differentiation (first derivative)
    differentiated_data = savitzky_golay_differentiation(data, window_length=51, polyorder=3, deriv=1, delta=t[1]-t[0])
    print("Differentiated data (first 10 points):", differentiated_data[:10])
