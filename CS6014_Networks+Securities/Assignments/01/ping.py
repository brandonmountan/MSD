import re

def parse_ping(file_path):
    delays = []
    with open(file_path, 'r') as file:
        for line in file:
            # Match lines containing "time=" and extract the round-trip time
            match = re.search(r'time=([\d.]+) ms', line)
            if match:
                delay = float(match.group(1))  # Extract the delay in ms
                delays.append(delay)

    if not delays:
        raise ValueError("No valid delay data found in the file.")

    # Assume the minimum delay corresponds to zero queuing
    min_delay = min(delays)
    queuing_delays = [d - min_delay for d in delays]

    # Calculate the average queuing delay
    avg_queuing_delay = sum(queuing_delays) / len(queuing_delays)

    return queuing_delays, avg_queuing_delay


def write_queuing_data(queuing_delays, output_path):
    with open(output_path, 'w') as file:
        file.write("Packet Number,Queuing Delay (ms)\n")
        for i, delay in enumerate(queuing_delays, start=1):
            file.write(f"{i},{delay:.2f}\n")


# Example usage
try:
    queuing_delays, avg_queuing_delay = parse_ping("ping_output.txt")
    write_queuing_data(queuing_delays, "queuing_delays.csv")
    print(f"Average Queuing Delay: {avg_queuing_delay:.2f} ms")
except ValueError as e:
    print(f"Error: {e}")
