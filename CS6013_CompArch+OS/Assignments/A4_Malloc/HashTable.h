//
// Created by Brandon Mountan on 3/3/25.
//

#ifndef HASHTABLE_H
#define HASHTABLE_H

#include <sys/mman.h>  // For mmap() and munmap()
#include <cstddef>     // For size_t
#include <cstdint>     // For uintptr_t

class HashTable {
private:
    // Entry struct to store pointer and size information
    struct Entry {
        void* ptr;      // Pointer to the allocated memory
        size_t size;    // Size of the allocated memory
        bool occupied;  // Whether this entry is currently in use
        bool deleted;   // Whether this entry has been lazily deleted
    };

    Entry* table;       // Pointer to the hash table array
    size_t capacity;    // Total number of slots in the hash table
    size_t size;        // Number of occupied slots in the hash table

    // Hash function to compute the index for a given pointer
    size_t hash(void* ptr);

    // Function to grow the hash table when it becomes too full
    void grow();

public:
    // Constructor and Destructor
    HashTable(size_t initial_capacity = 16);
    ~HashTable();

    // Insert a new entry into the hash table
    void insert(void* ptr, size_t size);

    // Remove an entry from the hash table using lazy deletion
    void remove(void* ptr);

    // Find the size of an allocation given its pointer
    size_t find(void* ptr);
};

#endif // HASHTABLE_H