//
//  main.cpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#include <iostream>

class myVector {
private:
    int capacity;
    int size;
    int* arrayPtr;
public:
    myVector(int initialCapacity){
        int* createdPtr = new int[initialCapacity];
        myVector createdVector;
        createdVector.capacity = initialCapacity;
        createdVector.size = 0;
        createdVector.arrayPtr = createdPtr;
        return createdVector;
    }

    static void freeVector(myVector &createdVector){
        delete[] createdVector.arrayPtr;
    }

    static void popBack(myVector &createdVector){
        createdVector.size = createdVector.size - 1;
    };

    static int get(myVector &createdVector, int index){
        return createdVector.arrayPtr[index];
    };

    static void set(myVector &createdVector, int index, int newValue){
        createdVector.arrayPtr[index] = newValue;
    };

    static void grow(myVector &createdVector){
        int* createdPtrX2 = new int[createdVector.capacity * 2];
        for (int i = 0; i < createdVector.size; i++){
            createdPtrX2[i] = createdVector.arrayPtr[i];
        }
        delete [] createdVector.arrayPtr;
        createdVector.arrayPtr = createdPtrX2;
        createdVector.capacity = createdVector.capacity * 2;
    };

    static void pushBack(myVector &createdVector, int pushedInt){
        if (createdVector.size == createdVector.capacity){
            grow(createdVector);
        }
        createdVector.arrayPtr[createdVector.size++] = pushedInt;
    };
};



int main(int argc, const char * argv[]) {
    
    myVector v1 = myVector::makeVector(16);
    
    for (int i = 0; i < v1.capacity; i++){
        myVector::pushBack(v1, i);
        std::cout << myVector::get(v1, i) << std::endl;
    }
    
    myVector::popBack(v1);
    
    for (int i = 0; i < v1.size; i++){
        std::cout << myVector::get(v1, i) << std::endl;
    }
    
    myVector::set(v1, 5, 27);
    
    for (int i = 0; i < v1.size; i++){
        std::cout << myVector::get(v1, i) << std::endl;
    }
    
    return 0;
}

