//
//  VectorWithClass.cpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#include "VectorWithClass.hpp"
#include <cassert>
#include <iostream>

//constructors
myVector::myVector(int initialCapacity){
   int* createdPtr = new int[initialCapacity];
   capacity = initialCapacity;
   size = 0;
   arrayPtr = createdPtr;
}

//rule of 3
//1. copy constructor
myVector::myVector(const myVector& rhs){
    //fill in 'this' with a copy of rhs
    int rhsCapacity = rhs.capacity;
    int* newArrayPtr = new int[rhsCapacity];
    arrayPtr = newArrayPtr;
    size = rhs.size;
    capacity = rhs.capacity;
    for (int i = 0; i < rhsCapacity; i++){
        arrayPtr[i] = rhs.arrayPtr[i];
    }
};
//2. operator=
myVector& myVector::operator=(myVector rhs){
    //rhs is it's own copy because it's passed by value
    int tmpSize = size;
    size = rhs.size;
    rhs.size = tmpSize;
    
    int tmpCapacity = size;
    size = rhs.size;
    rhs.size = tmpCapacity;
    
    return *this;
    //the destructor will delete[] my old data
}
//3. destructor
myVector::~myVector(){
    delete [] arrayPtr;
}

void myVector::freeVector(){
   delete[] arrayPtr;
}

void myVector::popBack(){
   size--;
};

int myVector::get(int index){
   return arrayPtr[index];
};

void myVector::set(int index, int newValue){
   arrayPtr[index] = newValue;
};

void myVector::grow(){
   int* createdPtrX2 = new int[capacity * 2];
   for (int i = 0; i < size; i++){
       createdPtrX2[i] = arrayPtr[i];
   }
   delete [] arrayPtr;
   arrayPtr = createdPtrX2;
   capacity = capacity * 2;
};

void myVector::pushBack(int pushedInt){
   if (size == capacity){
       grow();
   }
   arrayPtr[size++] = pushedInt;
};



int main(){
    
    myVector v1 = myVector(16);

    for (int i = 0; i < 16; i++){
        v1.pushBack(i);
        std::cout << v1.get(i);
    }

    std::cout << "\n";
    
    myVector v2 = v1;
    
    for (int i = 0; i < 16; i++){
        std::cout << v2.get(i);
    }
    
    std::cout << "\n";

    
}
