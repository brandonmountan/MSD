//
//  main.cpp
//  MakeYourOwnVector
//
//  Created by Brandon Mountan on 09/10/24.
//

#include <iostream>
#include "vector.hpp"
#include "vector.cpp"

void testVector() {
    MyVector<int> vec(2); // Initial capacity of 2

    // Test pushBack
    vec.pushBack(10);
    vec.pushBack(20);
    vec.pushBack(30); // Should grow

    std::cout << "Vector contents after pushes: ";
    for (size_t i = 0; i < vec.getSize(); ++i) {
        std::cout << vec[i] << " ";
    }
    std::cout << std::endl;

    // Test copy constructor
    MyVector<int> vecCopy = vec;

    std::cout << "Copied Vector: ";
    for (size_t i = 0; i < vecCopy.getSize(); ++i) {
        std::cout << vecCopy.get(i) << " ";
    }
    std::cout << std::endl;

    // Test assignment operator
    MyVector<int> vecAssigned(1);
    vecAssigned = vec;

    std::cout << "Assigned Vector: ";
    for (size_t i = 0; i < vecAssigned.getSize(); ++i) {
        std::cout << vecAssigned.get(i) << " ";
    }
    std::cout << std::endl;

    // Test operator[]
    std::cout << "Accessing using operator[]: " << vec[0] << ", " << vec[1] << ", " << vec[2] << std::endl;

    // Pop back and test
    std::cout << "Popped value: " << vec.popBack() << std::endl;

    std::cout << "Vector contents after pop: ";
    for (size_t i = 0; i < vec.getSize(); ++i) {
        std::cout << vec[i] << " ";
    }
    std::cout << std::endl;
}

void testDifferentTypes() {
    // Test with double
    MyVector<double> doubleVec(2);
    doubleVec.pushBack(1.1);
    doubleVec.pushBack(2.2);
    doubleVec.pushBack(3.3); // Should grow

    std::cout << "Double Vector: ";
    for (size_t i = 0; i < doubleVec.getSize(); ++i) {
        std::cout << doubleVec[i] << " ";
    }
    std::cout << std::endl;
}

void testComparisonOperators() {
    MyVector<int> vec1(3);
    vec1.pushBack(1);
    vec1.pushBack(2);
    vec1.pushBack(3);

    MyVector<int> vec2(3);
    vec2.pushBack(1);
    vec2.pushBack(2);
    vec2.pushBack(4);

    std::cout << "vec1 == vec2: " << (vec1 == vec2) << std::endl;
    std::cout << "vec1 != vec2: " << (vec1 != vec2) << std::endl;
    std::cout << "vec1 < vec2: " << (vec1 < vec2) << std::endl;
    std::cout << "vec1 <= vec2: " << (vec1 <= vec2) << std::endl;
    std::cout << "vec1 > vec2: " << (vec1 > vec2) << std::endl;
    std::cout << "vec1 >= vec2: " << (vec1 >= vec2) << std::endl;
}

int main() {
    testVector();
    testDifferentTypes();
    testComparisonOperators();
    return 0;
}
