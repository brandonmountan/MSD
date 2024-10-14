//
//  VectorWithClass.hpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#ifndef VectorWithClass_hpp
#define VectorWithClass_hpp

#include <stdio.h>
#include <iostream>
#include <cstdlib>

#endif /* VectorWithClass_hpp */

template<typename T>
class myVector {
public:
    //constructors
    myVector(T initialCapacity);
    
    //rule of 3
    myVector(const myVector& rhs);
    myVector& operator=(myVector rhs);
    ~myVector();
    
    void freeVector();
    void popBack();
    int get(T index);
    void set(T index, T newValue);
    void grow();
    void pushBack(T pushedInt);
    
    void operator<(myVector rhs);
    
    T* begin();
    T* end();
        
private:
    T capacity;
    T size;
    T* arrayPtr;
};

template<typename T>
myVector<T>::myVector(T initialCapacity){
   T* createdPtr = new T[initialCapacity];
   capacity = initialCapacity;
   size = 0;
   arrayPtr = createdPtr;
}

//rule of 3
//1. copy constructor
template<typename T>
myVector<T>::myVector(const myVector& rhs){
    //fill in 'this' with a copy of rhs
    T rhsCapacity = rhs.capacity;
    T* newArrayPtr = new T[rhsCapacity];
    arrayPtr = newArrayPtr;
    size = rhs.size;
    capacity = rhs.capacity;
    for (int i = 0; i < rhsCapacity; i++){
        arrayPtr[i] = rhs.arrayPtr[i];
    }
};

//2. operator=
template<typename T>
myVector<T>& myVector<T>::operator=(myVector rhs){
    //rhs is it's own copy because it's passed by value
    T tmpSize = size;
    size = rhs.size;
    rhs.size = tmpSize;
    
    T tmpCapacity = size;
    size = rhs.size;
    rhs.size = tmpCapacity;
    
    return *this;
    //the destructor will delete[] my old data
}

//3. destructor
template<typename T>
myVector<T>::~myVector(){
    delete [] arrayPtr;
}

template<typename T>
void myVector<T>::freeVector(){
   delete[] arrayPtr;
}

template<typename T>
void myVector<T>::popBack(){
   size--;
};

template<typename T>
int myVector<T>::get(T index){
   return arrayPtr[index];
};

template<typename T>
void myVector<T>::set(T index, T newValue){
   arrayPtr[index] = newValue;
};

template<typename T>
void myVector<T>::grow(){
   int* createdPtrX2 = new int[capacity * 2];
   for (int i = 0; i < size; i++){
       createdPtrX2[i] = arrayPtr[i];
   }
   delete [] arrayPtr;
   arrayPtr = createdPtrX2;
   capacity = capacity * 2;
};

template<typename T>
void myVector<T>::pushBack(T pushedInt){
   if (size == capacity){
       grow();
   }
   arrayPtr[size++] = pushedInt;
};

//template<typename T>
//bool myVector<T>::operator<(myVector rhs){
//    
//};


template<typename T>
T* myVector<T>::begin(){
    return arrayPtr;
};

template<typename T>
T* myVector<T>::end(){
    return begin() + size();
};

