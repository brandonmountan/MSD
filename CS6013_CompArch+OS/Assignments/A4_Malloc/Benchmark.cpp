//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/03/2025
//
// Class: CS 6013 - Systems I
//
//////////////////////////////////////////////////////////////////////////////////

#include "Benchmark.h"
#include <chrono>  // For high_resolution_clock
#include <iostream>  // For std::cout
#include <cstdlib>  // For malloc() and free()

// Function to compare the performance of MyMalloc and system malloc
void Benchmark::run() {
    MyMalloc my_malloc;  // Create an instance of the custom allocator
    const int num_allocations = 100000;  // Number of allocations to test

    // Benchmark the custom allocator
    auto start = std::chrono::high_resolution_clock::now();
    for (int i = 0; i < num_allocations; ++i) {
        void* ptr = my_malloc.allocate(1024);  // Allocate 1 KB of memory
        my_malloc.deallocate(ptr);  // Deallocate the memory
    }
    auto end = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> elapsed = end - start;
    std::cout << "MyMalloc: " << elapsed.count() << " seconds\n";

    // Benchmark the system allocator
    start = std::chrono::high_resolution_clock::now();
    for (int i = 0; i < num_allocations; ++i) {
        void* ptr = malloc(1024);  // Allocate 1 KB of memory
        free(ptr);  // Deallocate the memory
    }
    end = std::chrono::high_resolution_clock::now();
    elapsed = end - start;
    std::cout << "System malloc: " << elapsed.count() << " seconds\n";
}