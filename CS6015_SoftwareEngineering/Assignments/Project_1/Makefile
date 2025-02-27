# Compiler and compiler flags
CXX = g++                      # Use g++ as the C++ compiler
CXXFLAGS = -Wall -Wextra -std=c++17  # Compiler flags:
                               # -Wall: Enable all warnings
                               # -Wextra: Enable extra warnings
                               # -std=c++17: Use the C++17 standard

# Target executables
TARGET = msdscript             # Name of the main executable
TEST_TARGET = test_msdscript   # Name of the test executable

# Source and object files for the main program
SRCS = main.cpp expr.cpp cmdline.cpp tests.cpp parse.cpp  # List of source files
OBJS = $(SRCS:.cpp=.o)         # Generate object file names by replacing .cpp with .o

# Source and object files for the test program
TEST_SRCS = test_msdscript.cpp exec.cpp  # List of source files for the test program
TEST_OBJS = $(TEST_SRCS:.cpp=.o)  # Generate object file names by replacing .cpp with .o

# Default target: build the main executable
all: $(TARGET)

# Rule to build the main executable
$(TARGET): $(OBJS)             # The target depends on the object files
	$(CXX) $(CXXFLAGS) -o $@ $^  # Link the object files into the executable
                               # $@: The target (msdscript)
                               # $^: All dependencies (object files)

# Rule to build the test executable
$(TEST_TARGET): $(TEST_OBJS)   # The target depends on the test object files
	$(CXX) $(CXXFLAGS) -o $@ $^  # Link the test object files into the test executable
                               # $@: The target (test_msdscript)
                               # $^: All dependencies (test object files)

# Clean up build artifacts
clean:
	rm -f $(OBJS) $(TARGET)    # Remove object files and the main executable
                               # -f: Force removal (ignore errors if files don't exist)

# Phony targets (targets that are not actual files)
.PHONY: all clean test

# Target to run tests
test: $(TARGET)                # The test target depends on the main executable
	./$(TARGET) --test          # Run the main executable with the --test flag

# Target to generate documentation
doc: msdscript                 # The doc target depends on the main executable
	cd documentation && doxygen  # Change to the documentation directory and run Doxygen