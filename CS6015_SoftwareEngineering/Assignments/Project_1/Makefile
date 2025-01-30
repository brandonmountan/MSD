CXX = g++
CXXFLAGS = -Wall -Wextra -std=c++17 -g  # Added -g for debugging

TARGET = msdscript
SRCS = main.cpp expr.cpp tests.cpp
OBJS = $(SRCS:.cpp=.o)

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean test

test: $(TARGET)
	@echo "Running tests..."
	@ls -la $(TARGET)  # Debugging: Ensure the binary exists
	@./msdscript --test
