//
//  FractionTest.cpp
//  FractionClass
//
//  Created by Brandon Mountan on 09/11/24.
//

#include <iostream>
#include "MyFractions.hpp"

void testFraction() {
    Fraction f1(2, 4); // Should reduce to 1/2
    Fraction f2(3, 6); // Should reduce to 1/2
    Fraction f3(5, -10); // Should reduce to -1/2

    std::cout << "f1: " << f1.toString() << std::endl; // 1/2
    std::cout << "f2: " << f2.toString() << std::endl; // 1/2
    std::cout << "f3: " << f3.toString() << std::endl; // -1/2

    Fraction sum = f1 + f2;
    std::cout << "Sum: " << sum.toString() << std::endl; // 1/1

    Fraction product = f1 * f3;
    std::cout << "Product: " << product.toString() << std::endl; // -1/4

    Fraction reciprocal = f1.reciprocal();
    std::cout << "Reciprocal of f1: " << reciprocal.toString() << std::endl; // 2/1

    // Additional tests can be added as needed
}

int main() {
    testFraction();
    return 0;
}
