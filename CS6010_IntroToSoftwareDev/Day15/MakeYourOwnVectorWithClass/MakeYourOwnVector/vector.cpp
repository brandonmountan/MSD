//
//  vector.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//


#include "vector.hpp"

// Constructor
MyVector::MyVector(size_t initialCapacity) : capacity(initialCapacity), size(0) {
    data = new int[capacity]; // Allocate memory
}

// Destructor
MyVector::~MyVector() {
    delete[] data; // Free memory
}

// Grow the vector by doubling its capacity
void MyVector::growVector() {
    size_t newCapacity = capacity * 2;
    int* newData = new int[newCapacity];

    // Copy old data to new array
    for (size_t i = 0; i < size; ++i) {
        newData[i] = data[i];
    }

    delete[] data; // Free old array
    data = newData; // Update to new array
    capacity = newCapacity; // Update capacity
}

// Push back a new element
void MyVector::pushBack(int value) {
    if (size == capacity) {
        growVector();
    }
    data[size++] = value; // Add the new value and increment size
}

// Pop back an element
int MyVector::popBack() {
    if (size == 0) {
        throw std::out_of_range("Vector is empty.");
    }
    return data[--size]; // Decrement size and return the last element
}

// Get an element at the specified index
int MyVector::get(size_t index) const {
    if (index >= size) {
        throw std::out_of_range("Index out of range.");
    }
    return data[index];
}

// Set an element at the specified index
void MyVector::set(size_t index, int newValue) {
    if (index >= size) {
        throw std::out_of_range("Index out of range.");
    }
    data[index] = newValue;
}
