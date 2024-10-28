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
#include <algorithm>

template <typename T>
class MyVector {
private:
    T* data;       // Pointer to the beginning of the array
    size_t capacity; // Total capacity of the vector
    size_t size;     // Current number of elements in the vector

    void growVector(); // Private method to grow the vector

public:
    MyVector(size_t initialCapacity);
    MyVector(const MyVector& other); // Copy constructor
    MyVector& operator=(const MyVector& other); // Copy assignment operator
    ~MyVector(); // Destructor

    void pushBack(T value);
    T popBack();
    T get(size_t index) const;
    void set(size_t index, T newValue);
    size_t getSize() const { return size; }
    size_t getCapacity() const { return capacity; }

    // Operator overloads for array-like access
    T& operator[](size_t index);
    const T& operator[](size_t index) const;

    // Comparison operators
    bool operator==(const MyVector& other) const;
    bool operator!=(const MyVector& other) const;
    bool operator<(const MyVector& other) const;
    bool operator<=(const MyVector& other) const;
    bool operator>(const MyVector& other) const;
    bool operator>=(const MyVector& other) const;
};

#endif // VECTOR_H
