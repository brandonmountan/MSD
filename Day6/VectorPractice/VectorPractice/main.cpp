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
    char c;
    for (int i = 0; i < s.size(); i++){
        c = s[i];
        result.push_back(c);
        std::cout << c << std::endl;
    }
    return result;
}

//std::vector<int> reverse(std::vector<int> nums) {
//    std::vector<int> results = {};
//    for (int i = nums.size(); i >= 0; i--){
//        
//    }
//    return results;
//}


int main(int argc, const char * argv[]) {
    
    std::vector<int> nums { 2, 4, 6, 8, 10 };
    
    std::cout << sum(nums) << std::endl;
    
    std::string test = "hello";
    
    stringToVec(test);
    
    
    return 0;
}
