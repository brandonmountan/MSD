//
//  main.cpp
//  Day6InClassPractice
//
//  Created by Brandon Mountan on 8/26/24.
//

#include <iostream>
#include "math_operations.hpp"
#include <cassert> // package that helps debug our program
#include <vector>

int main(int argc, const char * argv[]) {
    
/*    assert("condition");*/ // if condition is true program will run, if false program will 'crash'
    
//    assert(5<4);
    
//    std::cout << add(2, 3) << std::endl;
    
    std::vector<int> numbers;
    
    numbers.push_back(1);
    numbers.push_back(2);
    
    for(int i = 0; i < numbers.size(); i++){
        std::cout << numbers[i] << std::endl;
    }
    
    

    return 0;
}
