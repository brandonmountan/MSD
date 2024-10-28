//
//  main.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//


#include <iostream>
#include "vector.hpp"

void testVector() {
    MyVector vec = makeVector(2); // Initial capacity of 2

    // Test pushBack
    pushBack(vec, 10);
    pushBack(vec, 20);
    
    std::cout << "Vector after pushes: ";
    for (size_t i = 0; i < vec.size; ++i) {
        std::cout << get(vec, i) << " ";
    }
    std::cout << std::endl;

    pushBack(vec, 30); // This should trigger a grow
    std::cout << "Vector after growing: ";
    for (size_t i = 0; i < vec.size; ++i) {
        std::cout << get(vec, i) << " ";
    }
    std::cout << std::endl;

    // Test popBack
    std::cout << "Popped value: " << popBack(vec) << std::endl;
    std::cout << "Vector size after pop: " << vec.size << std::endl;

    // Cleanup
    freeVector(vec);
}

int main() {
    testVector();
    return 0;
}
