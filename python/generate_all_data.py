import os
import subprocess
import sys

def main():
    # Define the directory containing the data generation scripts
    dsgen_dir = os.path.join(os.path.dirname(__file__), 'dsgen')
    
    # Check if the directory exists
    if not os.path.exists(dsgen_dir):
        print(f"Error: Directory '{dsgen_dir}' not found.")
        sys.exit(1)

    # List all python files in the directory starting with 'generate_' and ending with '.py'
    scripts = [
        f for f in os.listdir(dsgen_dir) 
        if f.startswith('generate_') and f.endswith('.py')
    ]
    
    scripts.sort()
    
    print(f"Found {len(scripts)} data generation scripts in {dsgen_dir}")
    print("-" * 50)

    success_count = 0
    failure_count = 0

    for script in scripts:
        script_path = os.path.join(dsgen_dir, script)
        print(f"Running {script}...")
        
        try:
            # Run the script using the current python executable
            result = subprocess.run(
                [sys.executable, script_path], 
                check=True, 
                capture_output=True, 
                text=True
            )
            print(f"✅ {script} completed successfully.")
            success_count += 1
            
        except subprocess.CalledProcessError as e:
            print(f"❌ {script} failed with return code {e.returncode}.")
            print("Stderr:")
            print(e.stderr)
            failure_count += 1
        except Exception as e:
            print(f"❌ {script} failed with exception: {e}")
            failure_count += 1
            
    print("-" * 50)
    print(f"Summary: {success_count} succeeded, {failure_count} failed.")

    if failure_count > 0:
        sys.exit(1)

if __name__ == "__main__":
    main()
