//
//  main.cpp
//  RomanNumerals
//
//  Created by Brandon Mountan on 8/22/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    
    std::cout << "Enter any number" << std::endl;
    
    int num;
    
    std::cin >> num;
    
    std::string romanNum = "";
    
    if (num < 0) {
        std::cout << "number cannot be negative" << std::endl;
    }
    
    while (num >= 1000) {
        romanNum += "M";
        num = num - 1000;
    }
    // 900 can only appear once => if statements => good for programmers
    while (num >= 900) {
        romanNum += "CM";
        num = num - 900;
    }
    while (num >= 500) {
        romanNum += "D";
        num = num - 500;
    }
    while (num >= 400) {
        romanNum += "CD";
        num = num - 400;
    }
    while (num >= 100) {
        romanNum += "C";
        num = num - 100;
    }
    while (num >= 90) {
        romanNum += "XC";
        num = num - 90;
    }
    while (num >= 50) {
        romanNum += "L";
        num = num - 50;
    }
    while (num >= 40) {
        romanNum += "XL";
        num = num - 40;
    }
    while (num >= 10) {
        romanNum += "X";
        num = num - 10;
    }
    while (num >= 9) {
        romanNum += "IX";
        num = num - 9;
    }
    while (num >= 5) {
        romanNum += "V";
        num = num - 5;
    }
    while (num >= 4) {
        romanNum += "IV";
        num = num -4;
    }
    while (num >= 1) {
        romanNum += "I";
        num = num - 1;
    }
    
    std::cout << romanNum << "\n";
    
    return 0;
}
