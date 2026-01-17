import os
import subprocess
import sys

def main():
    # Define the directory containing the plot scripts
    comparison_dir = os.path.join(os.path.dirname(__file__), 'comparison')
    
    # Check if the directory exists
    if not os.path.exists(comparison_dir):
        print(f"Error: Directory '{comparison_dir}' not found.")
        sys.exit(1)

    # List all python files in the directory starting with 'plot_'
    scripts = [
        f for f in os.listdir(comparison_dir) 
        if f.startswith('plot_') and f.endswith('.py')
    ]
    
    scripts.sort()
    
    print(f"Found {len(scripts)} plot scripts in {comparison_dir}")
    print("-" * 50)

    success_count = 0
    failure_count = 0

    themes = ["light", "dark"]

    for script in scripts:
        script_path = os.path.join(comparison_dir, script)
        print(f"Processing {script}...")
        
        for theme in themes:
            print(f"  Running {script} in {theme} mode...")
            
            # Prepare environment with theme
            env = os.environ.copy()
            env["JSCIPY_PLOT_THEME"] = theme

            try:
                # Run the script using the current python executable
                result = subprocess.run(
                    [sys.executable, script_path], 
                    check=True, 
                    capture_output=True, 
                    text=True,
                    env=env
                )
                print(f"  ✅ {theme} complete.")
                # Optional: Print stdout if verbose is needed
                # if result.stdout:
                #     print(result.stdout)
                
            except subprocess.CalledProcessError as e:
                print(f"  ❌ {script} ({theme}) failed with return code {e.returncode}.")
                print("  Stderr:")
                print(e.stderr)
                failure_count += 1
            except Exception as e:
                print(f"  ❌ {script} ({theme}) failed with exception: {e}")
                failure_count += 1
        
        success_count += 1 # Counting scripts, not individual runs for summary simplicity
            
    print("-" * 50)
    print(f"Summary: {success_count} succeeded, {failure_count} failed.")

    if failure_count > 0:
        sys.exit(1)

if __name__ == "__main__":
    main()
