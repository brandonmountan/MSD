//
//  main.cpp
//  VectorPractice
//
//  Created by Brandon Mountan on 8/26/24.
//

#include <iostream>
#include <vector>
#include <typeinfo>

int sum(std::vector<int> v){
    int total = 0;
    for (int i = 0; i < v.size(); i++){
        total = total + v[i];
    }
    return total;
}

std::vector<char> stringToVec(std::string s){
    std::vector<char> result = {};
    for (int i = 0; i < s.size(); i++){
        char c;
        c = s[i];
        result.push_back(c);
        std::cout << c << std::endl;
    }
    return result;
}

std::vector<int> reverse(std::vector<int> nums) {
    std::vector<int> results = {};
    for (auto results = nums.rbegin(); results != nums.rend(); ++results) {
        std::cout << *results << " ";
    }
    std::cout << std::endl;
    return results;
}


int main(int argc, const char * argv[]) {
    
    std::vector<int> nums { 2, 4, 6, 8, 10 };
    
    std::cout << sum(nums) << std::endl;
    
    std::string test = "hello";
    
    std::vector<char> newVec = stringToVec(test);
    
    // iterating through the new vector to visualize its contents and test its functionality
    for (int i = 0; i < newVec.size(); i++){
        std::cout << newVec[i] << std::endl;
    }
    
    std::vector<int> reverseContainer = reverse(nums);
    
    // iterating throught new vector of integers in reverse order to see its contents and test functionality
    for (int i = 0; i < reverseContainer.size(); i++){
        std::cout << reverseContainer[i] << std::endl;
    }
        
    return 0;
}
