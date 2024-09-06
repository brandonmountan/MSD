//
//  analyzer.cpp
//  BookAnalyzer
//
//  Created by Brandon Mountan on 9/4/24.
//

#include "analyzer.hpp"
#include <iostream>
#include <fstream>
#include <vector>
#include <array>
#include <string>
#include <string.h>
#include <stdlib.h>
#include <algorithm>

int analyzer(int argc, const char * argv[]) {
    
    std::ifstream myFile{ argv[1] };
    std::string keyword = argv[2];
//    std::ifstream myFile{ "winniethepooh.txt" };
    
    if (!myFile) {
        std::cerr << "Book not found! Please try another input. \n";
        return 1;
    }
    
    std::string strInput{};

    std::vector<std::string> container = {};
    
    while (myFile >> strInput){
        container.push_back(strInput);
    }
    
    int keywordCount = 0;
    
    int x = 0;
        
    for (int i = 0; i < container.size(); i++){
        if (container[i] == keyword){
            x = i;
            keywordCount++;
        }
    }
    
    std::cout << keywordCount << std::endl;
    
    
    return 0;
}
