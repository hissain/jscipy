# Contributing to jSciPy

First off, thank you for considering contributing to jSciPy! It's people like you that make jSciPy such a great tool for the Java community.

## 🤝 How Can I Contribute?

### Reporting Bugs
This section guides you through submitting a bug report for jSciPy. Following these guidelines helps maintainers and the community understand your report, reproduce the behavior, and find related reports.

*   **Check existing issues** to see if the bug has already been reported.
*   **Use the Bug Report template** to ensure all necessary details (version, environment, reproduction steps) are included.

### Suggesting Enhancements
This section guides you through submitting an enhancement suggestion for jSciPy, including completely new features and minor improvements to existing functionality.

*   **Use the Feature Request template**.
*   **Explain why** this validation would be useful to most jSciPy users and maybe even provide a code example.

### Pull Requests
The process is straightforward:

1.  **Fork** the repo on GitHub.
2.  **Clone** the project to your own machine.
3.  **Create a branch** for your work.
4.  **Commit** changes to your own branch.
5.  **Push** your work back up to your fork.
6.  Submit a **Pull Request** so that we can review your changes.

NOTE: Be sure to merge the latest from "upstream" before making a pull request!

## 🧪 Development & Testing

jSciPy uses **Gradle** for building and testing.

### Build the Project
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Check Checkstyle
```bash
./gradlew check
```

## 🚀 Adding a New Feature (Workflow)

jSciPy follows a strict **"Golden Master"** testing approach where Java implementations are verified against Python's SciPy. When adding a new feature (e.g., a new filter or transform), please follow this workflow:

### 1. Implement in Java
Add your implementation in `src/main/java/com/hissain/jscipy/...`. Ensure it mirrors the SciPy API as closely as possible.

### 2. Generate Ground Truth Data (Python)
We use Python scripts to generate "correct" input/output data using SciPy.
*   **Create a script**: `python/dsgen/generate_<feature>_test_data.py`
*   **Output location**: Save all data to `datasets/` (use relative paths, e.g., `../../datasets`).
*   **Content**: Generate random or representative inputs, run the SciPy function, and save inputs and expected outputs to `.txt` files.

```python
# Example: python/dsgen/generate_myfeature.py
import numpy as np
import os
def generate():
    # ... generate data ...
    script_dir = os.path.dirname(os.path.abspath(__file__))
    dataset_dir = os.path.join(script_dir, "../../datasets")
    # ... save to dataset_dir ...
```

### 3. Verify with Java Tests
Create a JUnit test that asserts your Java implementation matches the SciPy data.
*   **Create a test**: `src/test/java/.../<Feature>Test.java`
*   **Load Data**: Read the input and expected output from `datasets/`.
*   **Run**: Execute your Java method.
*   **Assert**: Compare Java output vs SciPy output (typically RMSE < 1e-15).
*   **Save Output**: Save the Java execution result to `datasets/` (e.g., `myfeature_output_java.txt`) for plotting.

### 4. Visualize Comparison
Create a Python script to verify and visualize the accuracy.
*   **Create a script**: `python/comparison/plot_<feature>.py`
*   **Load Data**: Load SciPy output and Java output from `datasets/`.
*   **Plot**: Create a visual comparison (e.g., overlaid signals or difference plot).
*   **Save Plot**: Save the figure to `python/figs/`.

### 5. Finalize
*   Run the orchestrators to ensure everything works:
    ```bash
    python3 python/generate_all_data.py
    python3 python/generate_all_plots.py
    ```
*   Add the generated plot to `README.md`.

## 📝 Coding Style
*   We follow standard Java naming conventions.
*   Please include Javadoc for public methods and classes.
*   Write unit tests for any new functionality.

Thank you for your help!
