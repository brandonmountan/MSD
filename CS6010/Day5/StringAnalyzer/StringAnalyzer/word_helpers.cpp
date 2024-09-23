//
//  word_helpers.cpp
//  StringAnalyzer
//
//  Created by Brandon Mountan on 8/26/24.
//
#include "letter_helpers.hpp"
#include "word_helpers.hpp"
#include <string>

bool IsTerminator(char c){
    return (c == '.' || c == '?' || c == '!');
};

int NumWords(std::string s){
    char space = ' ';
    int count = 0;
    for (int i = 0; i < s.length(); i++){
        if (s[i] == space){
            count += 1;
        }
    }
    return count + 1;
};

int NumSentences(std::string s){
    int count = 0;
    for (int i = 0; i <= s.length(); i++){
        if (IsTerminator(s[i])){
            count += 1;
        }
    }
    return count;
};

double AverageWordLength(std::string s){
    int vowels = NumVowels(s);
    int consonants = NumConsonants(s);
    double words = double(NumWords(s));
    double answer = (vowels + consonants) / words;
    return answer;
};

double averageVowelsPerWord(std::string s){
    int vowels = NumVowels(s);
    double words = double(NumWords(s));
    double answer = vowels / words;
    return answer;
};
