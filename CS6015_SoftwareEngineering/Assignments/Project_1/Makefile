CXX = g++
CXXFLAGS = -Wall -Wextra -std=c++17

TARGET = msdscript
SRCS = main.cpp expr.cpp cmdline.cpp tests.cpp parse.cpp
OBJS = $(SRCS:.cpp=.o)

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean test

test: $(TARGET)
	./$(TARGET) --test

doc: msdscript
	cd documentation && doxygen