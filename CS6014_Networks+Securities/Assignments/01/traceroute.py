import matplotlib.pyplot as plt
import re

def parse_traceroute(filename):
    """
    Parses a traceroute output file to extract IP addresses and calculate average delays.

    Args:
        filename (str): Path to the traceroute output file.

    Returns:
        list of tuple: A list of tuples where each tuple contains:
                       - str: IP address (e.g., "192.168.1.1").
                       - float: Average delay in milliseconds.
                       The order of hops is preserved.
    """

    # initialize empty list to store parsed traceroute data
    data = []
    # reg ex patterns
    ip_pattern = r"\((\d{1,3}(?:\.\d{1,3}){3})\)"
    delay_pattern = r"(\d+\.\d+) ms"

    # open traceroute file for reading
    with open(filename, 'r') as file:
        # read file line by line
        for line in file:
            # search line for all occurrences of the IP and delay patterns
            ip_matches = re.findall(ip_pattern, line) # list of matched IP addresses
            delay_matches = re.findall(delay_pattern, line) # list of matched delay values (as strings)

            # Skip lines with no valid data (e.g., "* * *")
            if not ip_matches or not delay_matches: # no IPs or delays
                continue

            # Convert delay matches to floats from strings
            delays = list(map(float, delay_matches))

            # Calculate average delay for the line
            avg_delay = sum(delays) / len(delays) # sum delays and divide by count

            # Use the first IP address as the key
            if ip_matches:
                data.append((ip_matches[0], avg_delay))
    # return parsed traceroute data as a list of (IP, average_delay) tuples
    # order is preserved, cannot change add or remove element once tuple is created
    # can store elements of different data types, can access elements using indices
    return data

# parameters
def write_output(hop_data, output_path):
    """
    Writes the processed traceroute data to a CSV file.

    Args:
        hop_data (list of tuple): List of tuples containing:
                                  - str: IP address.
                                  - float: Average delay in milliseconds.
        output_path (str): Path to the output CSV file.

    Returns:
        None
    """
    with open(output_path, 'w') as file:
        for ip, delay in hop_data:
            file.write(f"{ip}, {delay:.2f}\n")


def plot_graph(data1, data2, label1, label2):
    """
    Plots a comparison graph of traceroute delays for two datasets.

    Args:
        data1 (list of tuple): First dataset, a list of tuples containing:
                               - str: IP address.
                               - float: Average delay in milliseconds.
        data2 (list of tuple): Second dataset, similar structure to data1.
        label1 (str): Label for the first dataset in the plot.
        label2 (str): Label for the second dataset in the plot.

    Returns:
        None
    """
    if not data1 and not data2:
        print("Error: Both datasets are empty. No data to plot.")
        return
    elif not data1:
        print("Warning: First dataset is empty.")
    elif not data2:
        print("Warning: Second dataset is empty.")

    # Extract IP addresses and delays
    x1, y1 = zip(*data1) if data1 else ([], [])
    x2, y2 = zip(*data2) if data2 else ([], [])

    plt.figure(figsize=(12, 8))

    # Plot the datasets
    if data1:
        hops1 = range(1, len(x1) + 1)
        plt.plot(hops1, y1, marker='o', label=label1, color='blue', linewidth=1.5)
    if data2:
        hops2 = range(1, len(x2) + 1)
        plt.plot(hops2, y2, marker='o', label=label2, color='orange', linestyle='--', linewidth=1.5)

    # Configure axes, labels, and title
    plt.xlabel('Hop Number', fontsize=12)
    plt.ylabel('Average Delay (ms)', fontsize=12)
    plt.title('Traceroute Average Delay Per Hop', fontsize=14)
    plt.legend(fontsize=12)
    plt.grid(alpha=0.5)
    plt.tight_layout()

    # Save and show the graph
    plt.savefig("traceroute_graph.pdf")
    plt.show()

# First run
data1 = parse_traceroute("traceroute_output_1.txt")
write_output(data1, "processed_traceroute_1.csv")

# Second run
data2 = parse_traceroute("./traceroute_output_2.txt")
write_output(data2, "processed_traceroute_2.csv")

# Plot the results
plot_graph(data1, data2, "First Run", "Second Run")
