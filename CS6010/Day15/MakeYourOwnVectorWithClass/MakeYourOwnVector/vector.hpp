//
//  vector.hpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//

#ifndef VECTOR_H
#define VECTOR_H

#include <iostream>
#include <stdexcept>

class MyVector {
private:
    int* data;       // Pointer to the beginning of the array
    size_t capacity; // Total capacity of the vector
    size_t size;     // Current number of elements in the vector

    void growVector(); // Private method to grow the vector

public:
    MyVector(size_t initialCapacity);
    ~MyVector(); // Destructor to free memory

    void pushBack(int value);
    int popBack();
    int get(size_t index) const;
    void set(size_t index, int newValue);
    size_t getSize() const { return size; }
    size_t getCapacity() const { return capacity; }
};

#endif // VECTOR_H
