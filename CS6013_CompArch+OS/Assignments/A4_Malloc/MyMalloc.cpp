//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/03/2025
//
// Class: CS 6013 - Systems I
//
//////////////////////////////////////////////////////////////////////////////////

#include "MyMalloc.h"
#include <unistd.h>  // For sysconf()

/**
 * @brief Rounds up the requested size to the nearest multiple of the page size.
 * @param size The requested size in bytes.
 * @return The rounded-up size.
 */
size_t MyMalloc::round_up(size_t size) {
    const size_t page_size = sysconf(_SC_PAGESIZE);  // Get the system page size
    return (size + page_size - 1) & ~(page_size - 1);  // Round up to the nearest page size
}

/**
 * @brief Allocates memory using mmap().
 * @param size The size of the memory to allocate in bytes.
 * @return A pointer to the allocated memory, or nullptr if allocation fails.
 */
void* MyMalloc::allocate(size_t size) {
    size_t rounded_size = round_up(size);  // Round up the size
    void* ptr = mmap(nullptr, rounded_size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (ptr == MAP_FAILED) return nullptr;  // Check for allocation failure
    allocations.insert(ptr, rounded_size);  // Track the allocation in the hash table
    return ptr;  // Return the pointer to the allocated memory
}

/**
 * @brief Deallocates memory using munmap().
 * @param ptr A pointer to the memory to deallocate.
 */
void MyMalloc::deallocate(void* ptr) {
    size_t size = allocations.find(ptr);  // Find the size of the allocation
    if (size > 0) {
        munmap(ptr, size);  // Free the memory
        allocations.remove(ptr);  // Remove the allocation from the hash table
    }
}