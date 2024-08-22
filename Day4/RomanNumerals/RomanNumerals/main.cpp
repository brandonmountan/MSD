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
        std::cout << romanNum + "M";
        num = num - 1000;
    }
    while (num >= 900) {
        std::cout << romanNum + "CM";
        num = num - 900;
    }
    while (num >= 500) {
        std::cout << romanNum + "D";
        num = num - 500;
    }
    while (num >= 400) {
        std::cout << romanNum + "CD";
        num = num - 400;
    }
    while (num >= 100) {
        std::cout << romanNum + "C";
        num = num - 100;
    }
    while (num >= 90) {
        std::cout << romanNum + "XC";
        num = num - 90;
    }
    while (num >= 50) {
        std::cout << romanNum + "L";
        num = num - 50;
    }
    while (num >= 40) {
        std::cout << romanNum + "XL";
        num = num - 40;
    }
    while (num >= 10) {
        std::cout << romanNum + "X";
        num = num - 10;
    }
    while (num >= 9) {
        std::cout << romanNum + "IX";
        num = num - 9;
    }
    while (num >= 5) {
        std::cout << romanNum + "V";
        num = num - 5;
    }
    while (num >= 4) {
        std::cout << romanNum + "IV";
        num = num -4;
    }
    while (num >= 1) {
        std::cout << romanNum + "I";
        num = num - 1;
    }
    std::cout << "\n";
    return 0;
}
