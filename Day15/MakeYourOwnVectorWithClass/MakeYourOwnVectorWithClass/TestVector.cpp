//
//  TestVector.cpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/12/24.
//

#include <stdio.h>
#include <iostream>
#include <cassert>
template<typename T>
class myVector {
private:
    T capacity;
    T size;
    T* arrayPtr;
public:
    //constructors
    myVector<T>(const T initialCapacity){
        T* createdPtr = new T[initialCapacity];
        capacity = initialCapacity;
        size = 0;
        arrayPtr = createdPtr;
     };
    //rule of 3
    //1. copyconstructor
    myVector<T>(const myVector& rhs){
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
    myVector& operator=(myVector rhs){
        //rhs is it's own copy because it's passed by value
        int tmpSize = size;
        size = rhs.size;
        rhs.size = tmpSize;
        
        int tmpCapacity = size;
        size = rhs.size;
        rhs.size = tmpCapacity;
        
        return *this;
        //the destructor will delete[] my old data
    };
    //3. destructor
    ~myVector(){
        delete[] arrayPtr;
     };
    void freeVector(){
        delete[] arrayPtr;
     };
    void popBack(){
        size--;
     };
    int get(T index){
        return arrayPtr[index];
     };
    void set(T index, T newValue){
        arrayPtr[index] = newValue;
     };
    void grow(){
        T* createdPtrX2 = new T[capacity * 2];
        for (int i = 0; i < size; i++){
            createdPtrX2[i] = arrayPtr[i];
        }
        delete [] arrayPtr;
        arrayPtr = createdPtrX2;
        capacity = capacity * 2;
     };
    void pushBack(T pushedInt){
        if (size == capacity){
            grow();
        }
        arrayPtr[size++] = pushedInt;
     };

};

int main(){
    
    myVector v1 = myVector(16);

    for (int i = 0; i < 16; i++){
        v1.pushBack(i);
        std::cout << v1.get(i);
    }

    std::cout << "\n";
    
    myVector v2 = v1;


//    v1.popBack();
//
//    for (int i = 0; i < v1.size; i++){
//        std::cout << v1.get(i) << std::endl;
//    }
//
//    v1.set(5, 27);
//
//    for (int i = 0; i < v1.size; i++){
//        std::cout << v1.get(i) << std::endl;
//    }
    
}
