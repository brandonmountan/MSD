//
// Created by Brandon Mountan on 3/3/25.
//

#include "HashTable.h"
#include <sys/mman.h>  // For mmap() and munmap()
#include <cstdint>     // For uintptr_t

// Hash function to compute the index for a given pointer
size_t HashTable::hash(void* ptr) {
    return (reinterpret_cast<uintptr_t>(ptr) >> 12) % capacity;
}

// Function to grow the hash table when it becomes too full
void HashTable::grow() {
    size_t new_capacity = capacity * 2;  // Double the capacity
    Entry* new_table = static_cast<Entry*>(
        mmap(nullptr, new_capacity * sizeof(Entry), PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0)
    );

    // Rehash all valid entries from the old table into the new table
    for (size_t i = 0; i < capacity; ++i) {
        if (table[i].occupied && !table[i].deleted) {
            size_t index = hash(table[i].ptr);  // Compute the new index
            while (new_table[index].occupied) {
                index = (index + 1) % new_capacity;  // Linear probing for collisions
            }
            new_table[index] = table[i];  // Copy the entry to the new table
        }
    }

    // Free the old table using munmap()
    munmap(table, capacity * sizeof(Entry));

    // Update the table pointer and capacity
    table = new_table;
    capacity = new_capacity;
}

// Constructor to initialize the hash table
HashTable::HashTable(size_t initial_capacity) : capacity(initial_capacity), size(0) {
    table = static_cast<Entry*>(
        mmap(nullptr, capacity * sizeof(Entry), PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0)
    );
}

// Destructor to clean up the hash table
HashTable::~HashTable() {
    munmap(table, capacity * sizeof(Entry));
}

// Insert a new entry into the hash table
void HashTable::insert(void* ptr, size_t size) {
    if (size >= capacity / 2) grow();  // Grow the table if it's half full
    size_t index = hash(ptr);
    while (table[index].occupied && !table[index].deleted) {
        index = (index + 1) % capacity;  // Linear probing
    }
    table[index] = {ptr, size, true, false};
    ++size;  // Increment the number of occupied slots
}

// Remove an entry from the hash table using lazy deletion
void HashTable::remove(void* ptr) {
    size_t index = hash(ptr);
    while (table[index].occupied) {
        if (table[index].ptr == ptr) {
            table[index].deleted = true;  // Lazy deletion
            --size;  // Decrement the number of occupied slots
            return;
        }
        index = (index + 1) % capacity;  // Linear probing
    }
}

// Find the size of an allocation given its pointer
size_t HashTable::find(void* ptr) {
    size_t index = hash(ptr);
    while (table[index].occupied) {
        if (table[index].ptr == ptr && !table[index].deleted) {
            return table[index].size;  // Return the size of the allocation
        }
        index = (index + 1) % capacity;  // Linear probing
    }
    return 0;  // Return 0 if the pointer is not found
}