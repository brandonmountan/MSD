//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/03/2025
//
// Class: CS 6013 - Systems I
//
//////////////////////////////////////////////////////////////////////////////////

#ifndef MYMALLOC_H
#define MYMALLOC_H

#include "HashTable.h"  // Include the HashTable class
#include <unistd.h>     // For sysconf()

class MyMalloc {
private:
    HashTable allocations;  // Hash table to track allocated memory

    // Function to round up the requested size to the nearest multiple of the page size
    size_t round_up(size_t size);

public:
    // Allocate memory using mmap()
    void* allocate(size_t size);

    // Deallocate memory using munmap()
    void deallocate(void* ptr);
};

#endif // MYMALLOC_H