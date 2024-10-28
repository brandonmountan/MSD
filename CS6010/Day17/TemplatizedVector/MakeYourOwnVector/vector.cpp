//
//  vector.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//


#include "vector.hpp"

// Constructor
template <typename T>
MyVector<T>::MyVector(size_t initialCapacity) : capacity(initialCapacity), size(0) {
    data = new T[capacity]; // Allocate memory
}

// Copy Constructor
template <typename T>
MyVector<T>::MyVector(const MyVector& other) : capacity(other.capacity), size(other.size) {
    data = new T[capacity]; // Allocate memory
    for (size_t i = 0; i < size; ++i) {
        data[i] = other.data[i]; // Copy elements
    }
}

// Copy Assignment Operator
template <typename T>
MyVector<T>& MyVector<T>::operator=(const MyVector& other) {
    if (this != &other) {
        delete[] data; // Clean up existing resources
        capacity = other.capacity;
        size = other.size;
        data = new T[capacity]; // Allocate new memory
        for (size_t i = 0; i < size; ++i) {
            data[i] = other.data[i]; // Copy elements
        }
    }
    return *this;
}

// Destructor
template <typename T>
MyVector<T>::~MyVector() {
    delete[] data; // Free memory
}

// Grow the vector by doubling its capacity
template <typename T>
void MyVector<T>::growVector() {
    size_t newCapacity = capacity * 2;
    T* newData = new T[newCapacity];

    // Copy old data to new array
    for (size_t i = 0; i < size; ++i) {
        newData[i] = data[i];
    }

    delete[] data; // Free old array
    data = newData; // Update to new array
    capacity = newCapacity; // Update capacity
}

// Push back a new element
template <typename T>
void MyVector<T>::pushBack(T value) {
    if (size == capacity) {
        growVector();
    }
    data[size++] = value; // Add the new value and increment size
}

// Pop back an element
template <typename T>
T MyVector<T>::popBack() {
    if (size == 0) {
        throw std::out_of_range("Vector is empty.");
    }
    return data[--size]; // Decrement size and return the last element
}

// Get an element at the specified index
template <typename T>
T MyVector<T>::get(size_t index) const {
    if (index >= size) {
        throw std::out_of_range("Index out of range.");
    }
    return data[index];
}

// Set an element at the specified index
template <typename T>
void MyVector<T>::set(size_t index, T newValue) {
    if (index >= size) {
        throw std::out_of_range("Index out of range.");
    }
    data[index] = newValue;
}

// Operator overloads for array-like access
template <typename T>
T& MyVector<T>::operator[](size_t index) {
    return data[index]; // Provide non-const access
}

template <typename T>
const T& MyVector<T>::operator[](size_t index) const {
    return data[index]; // Provide const access
}

// Comparison operators
template <typename T>
bool MyVector<T>::operator==(const MyVector& other) const {
    if (size != other.size) return false;
    for (size_t i = 0; i < size; ++i) {
        if (data[i] != other.data[i]) return false;
    }
    return true;
}

template <typename T>
bool MyVector<T>::operator!=(const MyVector& other) const {
    return !(*this == other);
}

template <typename T>
bool MyVector<T>::operator<(const MyVector& other) const {
    for (size_t i = 0; i < std::min(size, other.size); ++i) {
        if (data[i] < other.data[i]) return true;
        if (data[i] > other.data[i]) return false;
    }
    return size < other.size; // If all previous elements are equal, check size
}

template <typename T>
bool MyVector<T>::operator<=(const MyVector& other) const {
    return *this < other || *this == other;
}

template <typename T>
bool MyVector<T>::operator>(const MyVector& other) const {
    return !(*this <= other);
}

template <typename T>
bool MyVector<T>::operator>=(const MyVector& other) const {
    return !(*this < other);
}
