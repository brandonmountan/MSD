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
#include <cmath>
#include <sstream>

double stringToInt(std::string number, double base){
    double result = 0;
    for (double i = number.size() - 1; i >= 0; i--){
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
    
    int numberRemainder;
    
    std::string numStr = "";
    
    std::string outputString = "";
    
    std::string reverseString = "";
    
    while (number > 0){
        numberRemainder = number % base;
        numStr = numberRemainder + '0';
        outputString += numStr;
        number /= base;
    }
    for (int i = 0; i < outputString.size()+1; i++){
        reverseString += outputString[outputString.size()-i];
    }
    return reverseString;
}

std::string intToBinary(int number){
        
    int base = 2;
    
    int numberRemainder;
    
    std::string numStr = "";
    
    std::string outputString = "";
    
    std::string reverseString = "";
    
    while (number > 0){
        numberRemainder = number % base;
        numStr = numberRemainder + '0';
        outputString += numStr;
        number /= base;
    }
    for (int i = 0; i < outputString.size()+1; i++){
        reverseString += outputString[outputString.size()-i];
    }
    return reverseString;
}

std::string intToHexidecimalString(int number){
    
    int base = 16;
    
    int numberRemainder;
    
    std::string numStr = "";
    
    std::string outputString = "";
    
    std::string reverseString = "";
    
    while (number > 0){
        numberRemainder = number % base;
        if (numberRemainder > 9){
            
        }
        if (numberRemainder >= 0 || numberRemainder <= 9){
            
        }
        numStr = numberRemainder + '0';
        outputString += numStr;
        number /= base;
        std::cout << numberRemainder << std::endl;
    }
    
    for (int i = 0; i < outputString.size()+1; i++){
        reverseString += outputString[outputString.size()-i];
    }

    return reverseString;
    
}
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
    
    std::cout << intToDecimalString(1234) << std::endl;
    std::cout << intToBinary(1234) << std::endl;
    std::cout << intToHexidecimalString(1234) << std::endl;

    return 0;
}
