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
    return 0;
}
