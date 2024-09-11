//
//  main.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 9/10/24.
//

#include <iostream>
#include <vector>

struct myVector {
    int capacity; // how big is the array
    int size; // current number of ints in array
    int* arrayPtr; // where does it start
    int makeVector(int initialCapacity);
    int freeVector(myVector);
    void pushBack(myVector, int someInt);
    void popBack(myVector, int someInt);
    
};

int myVector::makeVector(int initialCapacity){
    myVector theVector;
    theVector.size = 0;
    theVector.capacity = initialCapacity;
//    theVector.arrayPtr = ?
//    return theVector;
    //return a vector with the given capacity and a size of 0
    return 0;
}

int myVector::freeVector(myVector){
    
    delete myVector[];
    // should deallocate any heap memory used by the myvector object.
    return 0;
}

void myVector::pushBack(myVector, int pushedInt){
    
    
    
};

void myVector::popBack(myVector, int poppedInt){
    
    
    
};

myVector::get(myVec, index){
    
    
};

myVector::set(myVec, index, newValue){
    
    
};

myVector::grow(myVec){
    
    
};



int main(int argc, const char * argv[]) {

    
    
    
    
    
    
    
    
    
    
    return 0;
}
