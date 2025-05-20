# ////////////////////////////////////////////////////////////////////////
# //
# // Author: Brandon Mountan
# // Date: 04/18/2025
# //
# // CS 6013
# //
# //
# ////////////////////////////////////////////////////////////////////////

#!/usr/bin/env python3
import matplotlib.pyplot as plt
import pandas as pd
import glob
import os
import sys
import re

def plot_strong_scaling_by_type(file_pattern, output_dir):
    """Plot strong scaling graphs from CSV files, separated by data type."""
    # Get the directory of the script
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Search in multiple possible locations
    possible_dirs = [
        script_dir,  # Script directory
        os.getcwd(),  # Current working directory
        os.path.join(script_dir, 'cmake-build-debug'),  # CLion's default build directory
        os.path.join(os.path.dirname(script_dir), 'cmake-build-debug')  # CLion's build dir if in subdirectory
    ]

    # Find all matching CSV files in possible directories
    csv_files = []
    for directory in possible_dirs:
        pattern = os.path.join(directory, file_pattern)
        found_files = glob.glob(pattern)
        if found_files:
            print(f"Found CSV files in: {directory}")
            csv_files.extend(found_files)

    if not csv_files:
        print(f"No files found matching pattern: {file_pattern}")
        print(f"Searched in: {possible_dirs}")
        return False

    # Group files by data type (int, float, double)
    data_types = set()
    file_by_type = {}

    for csv_file in csv_files:
        basename = os.path.basename(csv_file)
        # Extract data type from filename
        match = re.search(r'_(int|float|double|long)_', basename)
        if match:
            data_type = match.group(1)
            data_types.add(data_type)
            if data_type not in file_by_type:
                file_by_type[data_type] = []
            file_by_type[data_type].append(csv_file)

    # For each data type, create separate plots for each implementation
    for data_type in data_types:
        # Create separate plots for each implementation
        implementation_types = ['std_threads', 'omp_custom', 'omp_builtin']

        for implementation in implementation_types:
            # Filter files for this implementation and data type
            impl_files = [f for f in file_by_type[data_type] if implementation in f]

            if not impl_files:
                continue

            # Create figure
            plt.figure(figsize=(10, 6))

            for csv_file in impl_files:
                # Read CSV data
                df = pd.read_csv(csv_file)

                # Get unique array sizes
                array_sizes = df['Array_Size'].unique()

                # Plot line for each array size
                for size in array_sizes:
                    subset = df[df['Array_Size'] == size]
                    plt.plot(subset['Threads'], subset['Time_ms'],
                             marker='o', label=f"{size} elements")

                    # Add ideal scaling line
                    if 1 in subset['Threads'].values:
                        base_time = subset[subset['Threads'] == 1]['Time_ms'].values[0]
                        threads = subset['Threads'].values
                        ideal_times = [base_time / t for t in threads]
                        plt.plot(threads, ideal_times, linestyle='--', color='gray', alpha=0.5,
                                 label=f"Ideal scaling for {size}" if size == array_sizes[0] else "")

            # Customize plot
            plt.title(f'Strong Scaling: {implementation}_{data_type}')
            plt.xlabel('Number of Threads')
            plt.ylabel('Execution Time (ms)')
            plt.grid(True, which='both', linestyle='--', alpha=0.7)
            plt.legend()
            plt.tight_layout()

            # Save plot
            output_filename = os.path.join(output_dir, f'strong_scaling_{implementation}_{data_type}.png')
            plt.savefig(output_filename)
            print(f"Strong scaling plot saved as {output_filename}")
            plt.close()

    return True

def plot_weak_scaling_by_type(file_pattern, output_dir):
    """Plot weak scaling graphs from CSV files, separated by data type."""
    # Get the directory of the script
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Search in multiple possible locations
    possible_dirs = [
        script_dir,  # Script directory
        os.getcwd(),  # Current working directory
        os.path.join(script_dir, 'cmake-build-debug'),  # CLion's default build directory
        os.path.join(os.path.dirname(script_dir), 'cmake-build-debug')  # CLion's build dir if in subdirectory
    ]

    # Find all matching CSV files in possible directories
    csv_files = []
    for directory in possible_dirs:
        pattern = os.path.join(directory, file_pattern)
        found_files = glob.glob(pattern)
        if found_files:
            print(f"Found CSV files in: {directory}")
            csv_files.extend(found_files)

    if not csv_files:
        print(f"No files found matching pattern: {file_pattern}")
        print(f"Searched in: {possible_dirs}")
        return False

    # Group files by data type (int, float, double)
    data_types = set()
    file_by_type = {}

    for csv_file in csv_files:
        basename = os.path.basename(csv_file)
        # Extract data type from filename
        match = re.search(r'_(int|float|double|long)_', basename)
        if match:
            data_type = match.group(1)
            data_types.add(data_type)
            if data_type not in file_by_type:
                file_by_type[data_type] = []
            file_by_type[data_type].append(csv_file)

    # For each data type, create a separate plot
    for data_type in data_types:
        # Create figure
        plt.figure(figsize=(10, 6))

        for csv_file in file_by_type[data_type]:
            # Extract method name from filename
            method_name = os.path.basename(csv_file).replace('_weak_scaling.csv', '')

            # Read CSV data
            df = pd.read_csv(csv_file)

            # Plot line
            plt.plot(df['Threads'], df['Time_ms'], marker='o', label=method_name)

        # Add horizontal line for ideal weak scaling
        if file_by_type[data_type]:
            df = pd.read_csv(file_by_type[data_type][0])
            if not df.empty:
                ideal_time = df.iloc[0]['Time_ms']
                plt.axhline(y=ideal_time, color='gray', linestyle='--', label='Ideal weak scaling')

        # Customize plot
        plt.title(f'Weak Scaling: {data_type} type')
        plt.xlabel('Number of Threads')
        plt.ylabel('Execution Time (ms)')
        plt.grid(True, which='both', linestyle='--', alpha=0.7)
        plt.legend()
        plt.tight_layout()

        # Save plot
        output_filename = os.path.join(output_dir, f'weak_scaling_{data_type}.png')
        plt.savefig(output_filename)
        print(f"Weak scaling plot saved as {output_filename}")
        plt.close()

    return True

def create_comparison_by_array_size(strong_pattern, output_dir):
    """Create comparison plots for each array size."""
    # Get the directory of the script
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Search in multiple possible locations
    possible_dirs = [
        script_dir,  # Script directory
        os.getcwd(),  # Current working directory
        os.path.join(script_dir, 'cmake-build-debug'),  # CLion's default build directory
        os.path.join(os.path.dirname(script_dir), 'cmake-build-debug')  # CLion's build dir if in subdirectory
    ]

    # Find all matching CSV files in possible directories
    csv_files = []
    for directory in possible_dirs:
        pattern = os.path.join(directory, strong_pattern)
        found_files = glob.glob(pattern)
        if found_files:
            print(f"Found CSV files in: {directory}")
            csv_files.extend(found_files)

    if not csv_files:
        print(f"No files found matching pattern: {strong_pattern}")
        print(f"Searched in: {possible_dirs}")
        return False

    # Read all data
    all_data = pd.DataFrame()
    for csv_file in csv_files:
        method_name = os.path.basename(csv_file).replace('_strong_scaling.csv', '')
        df = pd.read_csv(csv_file)
        df['Method'] = method_name
        all_data = pd.concat([all_data, df])

    # Get unique array sizes and data types
    array_sizes = all_data['Array_Size'].unique()

    # Group by data type
    all_data['Data_Type'] = all_data['Method'].str.extract(r'_(int|float|double|long)$')

    # Create comparison plots for each array size and data type
    for size in array_sizes:
        data_types = all_data[all_data['Array_Size'] == size]['Data_Type'].unique()

        for data_type in data_types:
            # Create figure
            plt.figure(figsize=(10, 6))

            # Filter data for this size and type
            subset = all_data[(all_data['Array_Size'] == size) & (all_data['Data_Type'] == data_type)]

            # Get unique methods
            methods = subset['Method'].unique()

            # Plot each method
            for method in methods:
                method_data = subset[subset['Method'] == method]
                plt.plot(method_data['Threads'], method_data['Time_ms'], marker='o', label=method)

            # Add ideal scaling
            base_methods = subset[subset['Threads'] == 1]
            if not base_methods.empty:
                # Use average of base times for ideal scaling
                base_time = base_methods['Time_ms'].mean()
                threads = sorted(subset['Threads'].unique())
                ideal_times = [base_time / t for t in threads]
                plt.plot(threads, ideal_times, linestyle='--', color='gray', label="Ideal scaling")

            # Customize plot
            plt.title(f'Performance Comparison: {data_type} type, {size} elements')
            plt.xlabel('Number of Threads')
            plt.ylabel('Execution Time (ms)')
            plt.grid(True, which='both', linestyle='--', alpha=0.7)
            plt.legend()
            plt.tight_layout()

            # Save plot
            output_filename = os.path.join(output_dir, f'comparison_{data_type}_{size}.png')
            plt.savefig(output_filename)
            print(f"Comparison plot saved as {output_filename}")
            plt.close()

    return True

if __name__ == "__main__":
    print("Starting plot generation...")

    # Create plots directory if it doesn't exist
    output_dir = "plots"
    os.makedirs(output_dir, exist_ok=True)

    # Plot strong scaling for each implementation and data type
    plot_strong_scaling_by_type("*_strong_scaling.csv", output_dir)

    # Plot weak scaling for each data type
    plot_weak_scaling_by_type("*_weak_scaling.csv", output_dir)

    # Create comparison plots by array size
    create_comparison_by_array_size("*_strong_scaling.csv", output_dir)

    print("All plots generated successfully!")