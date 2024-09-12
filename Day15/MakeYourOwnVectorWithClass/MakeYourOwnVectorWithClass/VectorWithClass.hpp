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
private:
    int capacity;
    int size;
    int* arrayPtr;
public:
    myVector(int initialCapacity);
    void freeVector();
    void popBack();
    int get(int index);
    void set(int index, int newValue);
    void grow();
    void pushBack(int pushedInt);
};
