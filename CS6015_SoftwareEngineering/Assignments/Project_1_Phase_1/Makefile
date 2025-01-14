CXX = g++
CXXFLAGS = -Wall -g
TARGET = msdscript
OBJS = main.o cmdline.o

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS)

main.o: main.cpp cmdline.h
	$(CXX) $(CXXFLAGS) -c main.cpp

cmdline.o: cmdline.cpp cmdline.h
	$(CXX) $(CXXFLAGS) -c cmdline.cpp

clean:
	rm -f $(TARGET) $(OBJS)