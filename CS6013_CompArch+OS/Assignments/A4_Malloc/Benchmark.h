//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/03/2025
//
// Class: CS 6013 - Systems I
//
//////////////////////////////////////////////////////////////////////////////////

#ifndef BENCHMARK_H
#define BENCHMARK_H

#include "MyMalloc.h"  // Include the MyMalloc class
#include <chrono>      // For high_resolution_clock
#include <iostream>    // For std::cout
#include <cstdlib>     // For malloc() and free()

class Benchmark {
public:
    // Function to compare the performance of MyMalloc and system malloc
    static void run();
};

#endif // BENCHMARK_H