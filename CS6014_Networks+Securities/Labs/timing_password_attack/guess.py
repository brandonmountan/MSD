import subprocess
import re
import random

def run_check_and_parse(guess):
    try:
        # Run the "check" command and capture the output
        result = subprocess.run(["./check", guess], capture_output=True, text=True, check=True)
        output = result.stdout

        # Search for the pattern and extract the number
        matches = re.findall(r"failed in virtual (\d+) ticks", output)
        
        if matches:
            # Convert matches to integers
            return int(matches[0])
        else:
            print(f"Succeeded with {guess}")
            exit(0)

    except subprocess.CalledProcessError as e:
        print(f"Command failed with error:\n{e.stderr}")
        exit(1)

def set_string_char(str, i, char_val):
    return str[:i] + chr(char_val + 97) + str[i + 1:]

# Find the right number of characters
print("Determining password length...")
best_time = run_check_and_parse("")
password = "_"

while True:
    new_time = run_check_and_parse(password)
    if new_time > best_time:
        best_time = new_time
        break
    password += "_"

length = len(password)
print(f"Password length is {length}")

# Now determine each character systematically
print("Determining password characters...")
for i in range(length):
    max_ticks = 0
    best_char = 'a'
    
    # Try each possible character for this position
    for c in range(26):
        test_password = set_string_char(password, i, c)
        ticks = run_check_and_parse(test_password)
        
        # The correct character should give us the highest tick count
        if ticks > max_ticks:
            max_ticks = ticks
            best_char = chr(c + 97)
    
    # Update the password with the best character found
    password = set_string_char(password, i, ord(best_char) - 97)
    print(f"Progress: {password} (position {i+1}/{length} found: '{best_char}')")

# Final check (should succeed)
run_check_and_parse(password)
