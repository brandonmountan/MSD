//
//  VectorWithClass.cpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#include "VectorWithClass.hpp"

myVector::myVector(int initialCapacity){
   int* createdPtr = new int[initialCapacity];
   capacity = initialCapacity;
   size = 0;
   arrayPtr = createdPtr;
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

