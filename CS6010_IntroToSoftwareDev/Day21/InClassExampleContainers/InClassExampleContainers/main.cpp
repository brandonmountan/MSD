//
//  main.cpp
//  InClassExampleContainers
//
//  Created by Brandon Mountan on 9/17/24.
//

#include <iostream>
#include <vector>

int main(int argc, const char * argv[]) {
    // insert code here...
    std::vector<int> numbers {2, 3, 0, 1, 3, 5};
    
    std::sort(numbers.begin(), numbers.end());
    
    for (int num: numbers)
        std::cout << num << std::endl;
    
    std::cout << "Hello, World!\n";
    return 0;
}
