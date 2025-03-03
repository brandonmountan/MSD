#include "MyMalloc.h"
#include "Benchmark.h"

int main() {
    std::cout << "Testing Custom Allocator...\n";
    MyMalloc my_malloc;

    // Test 1: Allocate and deallocate a small block of memory
    void* ptr1 = my_malloc.allocate(1024);  // Allocate 1 KB
    if (ptr1) {
        std::cout << "Allocated 1 KB at address: " << ptr1 << "\n";
        my_malloc.deallocate(ptr1);  // Deallocate the memory
        std::cout << "Deallocated 1 KB at address: " << ptr1 << "\n";
    } else {
        std::cerr << "Failed to allocate memory!\n";
    }

    // Test 2: Allocate and deallocate a large block of memory
    void* ptr2 = my_malloc.allocate(1024 * 1024);  // Allocate 1 MB
    if (ptr2) {
        std::cout << "Allocated 1 MB at address: " << ptr2 << "\n";
        my_malloc.deallocate(ptr2);  // Deallocate the memory
        std::cout << "Deallocated 1 MB at address: " << ptr2 << "\n";
    } else {
        std::cerr << "Failed to allocate memory!\n";
    }

    std::cout << "\nRunning Benchmark...\n";
    Benchmark::run();  // Run the benchmark

    return 0;
}