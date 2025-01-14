//
//  main.cpp
//  StringDemo
//
//  Created by Brandon Mountan on 9/11/24.
//

#include <iostream>
#include "MyString.hpp"
#include <cassert>

int main(int argc, const char * argv[]) {
    
    MyString ms1;
    assert(ms1.size() == 0);
    
    MyString ms2 = "hello"; //actually calls my char* constructor
    int size = ms2.size();
    for(int i = 0; i < size; i++){
        std::cout << ms2[i];
    }
    std::cout << std::endl;
    
    
    
    {
//        MyString ms3 = ms2;
        MyString ms3; //default constructor
        ms3 = ms2; //operator=
        ms3[0] = 'j';
        
        int size = ms3.size();
        for(int i = 0; i < size; i++){
            std::cout << ms3[i];
        }
        std::cout << std::endl;
        
        MyString ms4 = ms2 + ms3;
        int ms4Size = ms4.size();
        for(int i = 0; i < ms4Size; i++){
            std::cout << ms4[i];
        }
        std::cout << std::endl;
        
    }
    
    size = ms2.size();
    for(int i = 0; i < size; i++){
        std::cout << ms2[i];
    }
    std::cout << std::endl;
    
    for(char c : ms2){
        std::cout << c << std::endl;
    }
    
    return 0;
}
