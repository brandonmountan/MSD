//
//  main.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 9/10/24.
//

#include <iostream>
#include "vector.hpp"

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
