# Compiler and flags
CXX = c++
CXXFLAGS = -std=c++11 -Wall -Wextra -g

# Target executable
TARGET = main

# Source files
SRCS = main.cpp shelpers.cpp

# Object files (generated from source files)
OBJS = $(SRCS:.cpp=.o)

# Default target
all: $(TARGET)

# Rule to build the target executable
$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS)

# Rule to compile .cpp files into .o files
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Clean up generated files
clean:
	rm -f $(OBJS) $(TARGET)

# Phony targets (not actual files)
.PHONY: all clean