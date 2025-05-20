CXX = c++
CXXFLAGS = -std=c++11 -Wall -Werror

main: main.o shelpers.o
	$(CXX) $(CXXFLAGS) main.o shelpers.o -o main

main.o: main.cpp shelpers.hpp
	$(CXX) $(CXXFLAGS) -c main.cpp

shelpers.o: shelpers.cpp shelpers.hpp
	$(CXX) $(CXXFLAGS) -c shelpers.cpp

clean:
	rm -f *.o main