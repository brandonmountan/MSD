#Macros
# gnu c++ compiler
CXX = g++
# C++ version, include warnings, run in debug
CXXFLAGS = -Wall -std=c++20 -g
# output executable file name
TARGET = msdscript
# source files
CXXSRC = main.cpp cmdline.cpp expr.cpp
# ALL object files
CXXOBJS = $(CXXSRC:.cpp=.o)
#Rule to create executable program
build:$(TARGET)
$(TARGET): $(CXXOBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(CXXOBJS)
# Rule to make all .cpp into .o files
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@
	
# Rule to clean .o files and executable
clean:
	rm -f $(CXXOBJS) $(TARGET)
	
# Rule to run tests
test: $(TARGET)
	./$(TARGET) --test
.PHONY: clean build test











Message Jason Ablott









