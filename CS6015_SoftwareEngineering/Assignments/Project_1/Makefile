CXX = g++
CXXFLAGS = -Wall -Wextra -std=c++17 -g  # Added -g for debugging

TARGET = msdscript
SRCS = main.cpp expr.cpp tests.cpp
OBJS = $(SRCS:.cpp=.o)

# Default target (runs when you type 'make' in the terminal)
$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: clean test

test: $(TARGET)
	@echo "Running tests..."
	@ls -la $(TARGET)  # Debugging: Ensure the binary exists
	@./msdscript --test
