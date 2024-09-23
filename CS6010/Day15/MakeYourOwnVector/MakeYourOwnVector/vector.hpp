//
//  vector.hpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 9/10/24.
//

#ifndef vector_hpp
#define vector_hpp

#include <stdio.h>

#endif /* vector_hpp */

struct myVector {
    int capacity;
    int size;
    int* arrayPtr;
    static myVector makeVector(int initialCapacity);
    static void freeVector(myVector &createdVector);
    static void popBack(myVector &createdVector);
    static int get(myVector &createdVector, int index);
    static void set(myVector &createdVector, int index, int newValue);
    static void grow(myVector &createdVector);
    static void pushBack(myVector &createdVector, int pushedInt);
};
