//
//  main.cpp
//  Day9InClassPractice
//
//  Created by Brandon Mountan on 8/29/24.
//

#include <iostream> // required for standard input/output stream objects
#include <fstream> // required for file stream objects
#include <vector>

int main(int argc, const char * argv[]) {

    std::ofstream out("file.text");
//    out.open("file.text");
    
    if(!out.is_open()){
        
        // if NOT open, ahmed would like app to crash
        
        std::cerr << "could not open file \n";
        
        return 1;
        
    }
    
    out << "hello world \n" << std::endl; // tells cpu to complete this line before moving on;
    
    out << 34;
    
    out << 23;
    
//    out.flush()
    
    out.close(); // need to always close file if you're reading or writing from it.
    
    std::ifstream in("file.text");
    
    if(!in.is_open()){
        
        std::cerr << "could not open file \n";

    }
    
    std::string str;
    
    std::string str2;
    
    std::string str3;
    
//    in >> str >> str2;
//    
//    in >> str3;
    
    std::getline(in, str);
        
    std::cout << str << std::endl;
    
    while (in >> str){
        std::cout << str << std::endl;
    }
    
    int x = 3;
    
    int y = 2;
    
//    int sum = x + y;
    
    return 0;
}
