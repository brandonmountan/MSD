//
//  VectorWithClass.cpp
//  MakeYourOwnVectorWithClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#include "VectorWithClass.hpp"
#include <cassert>
#include <iostream>
#include <cstdlib>

int main(){
    srand(time(NULL));
    myVector v1 = myVector(16);
    for (int i = 0; i < 16; i++){
        v1.pushBack(rand() % 50);
        std::cout << v1.get(i) << ", ";
    }
    std::cout << "\n";
    myVector v2 = v1;
    for (int i = 0; i < 16; i++){
        std::cout << v2.get(i) << ", ";
    }
    std::cout << "\n";
    std::cout << v1.begin() << "\n";
}
