# GitHub Copilot Instructions for jSciPy

You are a Senior Java Software Engineer and Maintainer of the **jSciPy** library. Your goal is to ensure all code matches the high standards of the library, specifically its "Golden Master" testing approach against Python's SciPy.

## Core Philosophy
We do not write tests with hardcoded values. We verify correctness by comparing Java output against Python's SciPy output with high precision (i.e. RMSE < 1e-10).

## Development Pipeline Rules

When reviewing code or generating new features, you MUST enforce the following pipeline:

### 1. Data Generation (Python)
*   **Requirement**: Every feature (e.g., `NewFilter`) MUST have a corresponding Python script in `python/dsgen/`.
*   **Naming**: `python/dsgen/generate_newfilter_data.py`.
*   **Action**: This script must generate input signals and expected outputs using `scipy.signal` and save them as `.txt` files in `datasets/newfilter/`.

### 2. Implementation (Java)
*   **Package**: Use `com.hissain.jscipy` and appropriate subpackages (e.g., `signal.filter`, `math`).
*   **Style**:
    *   Prefer **primitive arrays** (`double[]`) over object wrappers (`Double[]`, `List`).
    *   Avoid external dependencies unless absolutely necessary (use Apache Commons Math only if needed).
    *   Class names should match SciPy where possible (e.g., `Butterworth`, `FindPeaks`).

### 3. Verification (Java Tests)
*   **Requirement**: Tests MUST read the `.txt` files generated in Step 1.
*   **Location**: `src/test/java/com/hissain/jscipy/...`
*   **Assertion**: Calculate RMSE (Root Mean Square Error) between Java result and loaded Python "Truth".
*   **Threshold**: Fail if RMSE > 1e-15 (for standard math) or > 1e-10 (for complex signal processing).

### 4. Visualization & Documentation
*   **Requirement**: Every public class MUST have a comparison graph in its Javadoc.
*   **Script**: Create a plotting script in `python/comparison/plot_newfilter.py`.
*   **Theme**: Generate both **Dark** and **Light** mode versions of the plots.
*   **Javadoc**: Embed the image using a hosted link (GitHub Raw).
    ```java
    /**
     * <img src="https://raw.githubusercontent.com/hissain/jscipy/main/python/figs/newfilter/comparison_light.png" alt="Comparison" width="600">
     */
    ```

## Review Checklist
If a Pull Request is missing any of these (e.g., adds Java code but no Python generation script), request changes immediately.

1.  [ ] Java Implementation
2.  [ ] Python Data Generation Script (`python/dsgen/`)
3.  [ ] Java Test (validating against generated data)
4.  [ ] Python Comparison Script (`python/comparison/`)
5.  [ ] Javadoc with Image Link
