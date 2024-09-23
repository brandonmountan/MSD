//
//  vector.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 9/10/24.
//

#include "vector.hpp"

 myVector myVector::makeVector(int initialCapacity){
    int* createdPtr = new int[initialCapacity];
    myVector createdVector;
    createdVector.capacity = initialCapacity;
    createdVector.size = 0;
    createdVector.arrayPtr = createdPtr;
    return createdVector;
}

void myVector::freeVector(myVector &createdVector){
    delete[] createdVector.arrayPtr;
}

void myVector::popBack(myVector &createdVector){
    createdVector.size = createdVector.size - 1;
};

int myVector::get(myVector &createdVector, int index){
    return createdVector.arrayPtr[index];
};

void myVector::set(myVector &createdVector, int index, int newValue){
    createdVector.arrayPtr[index] = newValue;
};

void myVector::grow(myVector &createdVector){
    int* createdPtrX2 = new int[createdVector.capacity * 2];
    for (int i = 0; i < createdVector.size; i++){
        createdPtrX2[i] = createdVector.arrayPtr[i];
    }
    delete [] createdVector.arrayPtr;
    createdVector.arrayPtr = createdPtrX2;
    createdVector.capacity = createdVector.capacity * 2;
};

void myVector::pushBack(myVector &createdVector, int pushedInt){
    if (createdVector.size == createdVector.capacity){
        grow(createdVector);
    }
    createdVector.arrayPtr[createdVector.size++] = pushedInt;
};

