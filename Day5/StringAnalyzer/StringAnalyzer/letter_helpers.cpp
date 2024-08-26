//
//  letter_helpers.cpp
//  StringAnalyzer
//
//  Created by Brandon Mountan on 8/26/24.
//

#include "letter_helpers.hpp"
#include <string>

int NumVowels(std::string s){
    int count = 0;
    for (int i = 0; i < s.length(); i++){
        if (s[i] == 'a' || s[i] == 'A' || s[i] == 'e' || s[i] == 'E'|| s[i] == 'i' || s[i] == 'I' || s[i] == 'o' || s[i] == 'O' || s[i] == 'u' || s[i] == 'U' || s[i] == 'y' || s[i] == 'Y'){
            count += 1;
        }
    }
    return count;
};

int NumConsonants(std::string s){
    int count = 0;
    for (int i = 0; i < s.length(); i++){
        if (s[i] == 'b' || s[i] == 'B' || s[i] == 'c' || s[i] == 'C' || s[i] == 'd' || s[i] == 'D' || s[i] == 'f' || s[i] == 'F' || s[i] == 'g' || s[i] == 'G' || s[i] == 'h' || s[i] == 'H' || s[i] == 'j' || s[i] == 'J' || s[i] == 'k' || s[i] == 'K' || s[i] == 'l' || s[i] == 'L' || s[i] == 'm' || s[i] == 'M' || s[i] == 'n' || s[i] == 'N' || s[i] == 'p' || s[i] == 'P' || s[i] == 'q' || s[i] == 'Q' || s[i] == 'r' || s[i] == 'R' || s[i] == 's' || s[i] == 'S' || s[i] == 't' || s[i] == 'T' || s[i] == 'v' || s[i] == 'V' ||  s[i] == 'w' || s[i] == 'W' || s[i] == 'x' || s[i] == 'X' || s[i] == 'z' || s[i] == 'Z'){
            count += 1;
        }
    }
    return count;
};
