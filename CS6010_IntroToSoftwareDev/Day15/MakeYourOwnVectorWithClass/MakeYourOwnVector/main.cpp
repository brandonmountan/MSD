//
//  main.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//


#include <iostream>
#include "vector.hpp"

void testVector() {
    MyVector vec(2); // Initial capacity of 2

    // Test pushBack
    vec.pushBack(10);
    vec.pushBack(20);

    std::cout << "Vector after pushes: ";
    for (size_t i = 0; i < vec.getSize(); ++i) {
        std::cout << vec.get(i) << " ";
    }
    std::cout << std::endl;

    vec.pushBack(30); // This should trigger a grow
    std::cout << "Vector after growing: ";
    for (size_t i = 0; i < vec.getSize(); ++i) {
        std::cout << vec.get(i) << " ";
    }
    std::cout << std::endl;

    // Test popBack
    std::cout << "Popped value: " << vec.popBack() << std::endl;
    std::cout << "Vector size after pop: " << vec.getSize() << std::endl;
}

int main() {
    testVector();
    return 0;
}
