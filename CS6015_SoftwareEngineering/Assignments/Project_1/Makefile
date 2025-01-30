CXX = g++
CXXFLAGS = -Wall -Wextra -std=c++17

TARGET = msdscript
SRCS = main.cpp expr.cpp tests.cpp
OBJS = $(SRCS:.cpp=.o)

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean test

test: $(TARGET)  # Ensure msdscript is built before running tests
	./msdscript --test
