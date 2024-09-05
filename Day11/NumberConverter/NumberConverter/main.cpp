//
//  main.cpp
//  NumberConverter
//
//  Created by Brandon Mountan on 9/3/24.
//

#include <iostream>
#include <math.h>
#include <string>
#include <ctype.h>
#include <assert.h>

double stringToInt(std::string number, double base){
    double result = 0;
    for (int i = (number.size()-1); i >= 0; i--){
        if (base == 16){
            char charInt = tolower(number[i]);
            int numericValue = charInt - 'a' + 10;
            result = result + numericValue*(pow(base, (number.size() - 1 - i)));
        } else {
            int numericValue = number[i] - '0';
            result = result + numericValue*(pow(base, (number.size() - 1 - i)));
        }
    }
    return result;
}

std::string intToDecimalString(int number){
    
    int base = 10;
    
    int quotient;
    
    int remainder;

    while(number > 0){
        
        quotient = number / pow(base, numberlength())
        
    }
    

//}

//std::string intToBinary(int number){
//    
//    int base = 2;
//    
//    
//    
//}
//
//std::string intToHexidecimalString(int number){
//    
//    int base = 16;
//    
//    
//    
//}
//
//void testStringToInt(){
//    assert(stringToInt("99", 10) == 99);
//    assert(stringToInt("10", 2) == 2);
//    //etc, more tests here
//}

int main(int argc, const char * argv[]) {

    
    std::string testStr = "FF";
    double testExp = 16;
    
    std::cout << stringToInt(testStr, testExp) << std::endl;
    
//    testStringToInt();
    
//    std::cout << intToDecimalString(10) << std::endl;
    
    return 0;
}
