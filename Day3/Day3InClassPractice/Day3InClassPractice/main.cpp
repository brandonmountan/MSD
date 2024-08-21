//
//  main.cpp
//  Day3InClassPractice
//
//  Created by Brandon Mountan on 8/21/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // insert code here...
    
    int age;
    
    std::cout << "please enter your age: \n";
    std::cin >> age;
    
    if (age > 18 && age < 80){
        std::cout << "hooray, you can vote. \n";
    }
    
    return 0;
}
