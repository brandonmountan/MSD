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

struct MyVector {
    int* data;      // Pointer to the beginning of the array
    size_t capacity; // Total capacity of the vector
    size_t size;     // Current number of elements in the vector
};

// Function prototypes
MyVector makeVector(size_t initialCapacity);
void freeVector(MyVector& myVec);
void pushBack(MyVector& myVec, int value);
int popBack(MyVector& myVec);
int get(const MyVector& myVec, size_t index);
void set(MyVector& myVec, size_t index, int newValue);
void growVector(MyVector& myVec);

#endif // VECTOR_H
