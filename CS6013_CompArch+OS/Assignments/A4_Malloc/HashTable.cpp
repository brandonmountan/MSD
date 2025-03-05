#include "HashTable.h"
#include <sys/mman.h>  // For mmap() and munmap()
#include <cstdint>     // For uintptr_t

/**
 * @brief Computes the hash index for a given pointer.
 * @param ptr The pointer to hash.
 * @return The computed hash index.
 */
size_t HashTable::hash(void* ptr) {
    return (reinterpret_cast<uintptr_t>(ptr) >> 12) % capacity;
}

/**
 * @brief Grows the hash table when it becomes too full.
 *
 * This function doubles the capacity of the hash table and rehashes all valid entries
 * into the new table. It uses mmap() to allocate memory for the new table and munmap()
 * to deallocate the old table.
 */
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

/**
 * @brief Constructs a new HashTable object.
 * @param initial_capacity The initial capacity of the hash table (default is 16).
 */
HashTable::HashTable(size_t initial_capacity) : capacity(initial_capacity), size(0) {
    table = static_cast<Entry*>(
        mmap(nullptr, capacity * sizeof(Entry), PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0)
    );
}

/**
 * @brief Destroys the HashTable object.
 *
 * This function deallocates the memory used by the hash table using munmap().
 */
HashTable::~HashTable() {
    munmap(table, capacity * sizeof(Entry));
}

/**
 * @brief Inserts a new entry into the hash table.
 * @param ptr The pointer to insert.
 * @param size The size of the allocated memory.
 */
void HashTable::insert(void* ptr, size_t size) {
    if (size >= capacity / 2) grow();  // Grow the table if it's half full
    size_t index = hash(ptr);
    while (table[index].occupied && !table[index].deleted) {
        index = (index + 1) % capacity;  // Linear probing
    }
    table[index] = {ptr, size, true, false};
    ++size;  // Increment the number of occupied slots
}

/**
 * @brief Removes an entry from the hash table using lazy deletion.
 * @param ptr The pointer to remove.
 */
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

/**
 * @brief Finds the size of an allocation given its pointer.
 * @param ptr The pointer to search for.
 * @return The size of the allocation, or 0 if the pointer is not found.
 */
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