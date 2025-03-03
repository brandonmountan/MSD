//
// Created by Brandon Mountan on 3/3/25.
//

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