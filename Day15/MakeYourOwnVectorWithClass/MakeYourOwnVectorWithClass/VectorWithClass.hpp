//
//  VectorWithClass.hpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#ifndef VectorWithClass_hpp
#define VectorWithClass_hpp

#include <stdio.h>

#endif /* VectorWithClass_hpp */

class myVector {
public:
    //constructor
    myVector(int initialCapacity);
    //copyconstructor
//    myVector(const myVector& rhs);
    myVector& operator=(myVector rhs);
    void freeVector();
    void popBack();
    int get(int index);
    void set(int index, int newValue);
    void grow();
    void pushBack(int pushedInt);
    //destructor
    ~myVector();
private:
    int capacity;
    int size;
    int* arrayPtr;
};

