//
//  vector.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//


#include "vector.hpp"

// Create a vector with given initial capacity
MyVector makeVector(size_t initialCapacity) {
    MyVector vec;
    vec.data = new int[initialCapacity]; // Allocate memory
    vec.capacity = initialCapacity;
    vec.size = 0;
    return vec;
}

// Free memory allocated for the vector
void freeVector(MyVector& myVec) {
    delete[] myVec.data;
    myVec.data = nullptr;
    myVec.size = 0;
    myVec.capacity = 0;
}

// Push back a new element
void pushBack(MyVector& myVec, int value) {
    if (myVec.size == myVec.capacity) {
        growVector(myVec);
    }
    myVec.data[myVec.size++] = value; // Add the new value and increment size
}

// Pop back an element
int popBack(MyVector& myVec) {
    if (myVec.size == 0) {
        throw std::out_of_range("Vector is empty.");
    }
    return myVec.data[--myVec.size]; // Decrement size and return the last element
}

// Get an element at the specified index
int get(const MyVector& myVec, size_t index) {
    if (index >= myVec.size) {
        throw std::out_of_range("Index out of range.");
    }
    return myVec.data[index];
}

// Set an element at the specified index
void set(MyVector& myVec, size_t index, int newValue) {
    if (index >= myVec.size) {
        throw std::out_of_range("Index out of range.");
    }
    myVec.data[index] = newValue;
}

// Grow the vector by doubling its capacity
void growVector(MyVector& myVec) {
    size_t newCapacity = myVec.capacity * 2;
    int* newData = new int[newCapacity];
    
    // Copy old data to new array
    for (size_t i = 0; i < myVec.size; ++i) {
        newData[i] = myVec.data[i];
    }
    
    delete[] myVec.data; // Free old array
    myVec.data = newData; // Update to new array
    myVec.capacity = newCapacity; // Update capacity
}
