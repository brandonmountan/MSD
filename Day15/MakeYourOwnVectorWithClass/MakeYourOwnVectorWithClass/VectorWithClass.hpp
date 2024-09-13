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
    //constructors
    myVector();
    myVector(int initialCapacity);
    
    //rule of 3
    myVector(const myVector& rhs);
    myVector& operator=(myVector rhs);
    ~myVector();
    
    void freeVector();
    void popBack();
    int get(int index);
    void set(int index, int newValue);
    void grow();
    void pushBack(int pushedInt);
    
private:
    int capacity;
    int size;
    int* arrayPtr;
};

