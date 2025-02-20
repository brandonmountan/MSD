CXX = g++
CXXFLAGS = -Wall -Wextra -std=c++17

TARGET = msdscript
LIBRARY_DIR = ../Library_MSDScript
LIBRARY = $(LIBRARY_DIR)/libmsdscript.a

# Sources for the library (core functionality)
LIB_SRCS = expr.cpp
LIB_OBJS = $(LIB_SRCS:.cpp=.o)

# Sources for the main executable (entry point)
MAIN_SRCS = main.cpp
MAIN_OBJS = $(MAIN_SRCS:.cpp=.o)

all: $(TARGET)

# Build the static library
$(LIBRARY): $(LIB_OBJS)
	mkdir -p $(LIBRARY_DIR)
	ar rcs $@ $^

# Build the main executable
$(TARGET): $(MAIN_OBJS) $(LIBRARY)
	$(CXX) $(CXXFLAGS) -o $@ $(MAIN_OBJS) -L$(LIBRARY_DIR) -lmsdscript

# Compile .cpp files to .o
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(LIB_OBJS) $(MAIN_OBJS) $(TARGET)

.PHONY: all clean test

# Run tests (--test flag)
test: $(TARGET)
	./$(TARGET) --test

# Generate documentation (optional)
doc:
	cd documentation && doxygen