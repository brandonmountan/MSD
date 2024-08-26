//
//  main.cpp
//  StringAnalyzer
//
//  Created by Brandon Mountan on 8/25/24.
//

#include <iostream>
#include <string>
#include <cassert>

bool IsTerminator(char c){
    
    int count = 0;
    
    if (c == '.'){
        count = count + 1;
    }
    
    std::cout << count << std::endl;
    
    return 0;

};

bool IsPunctuation(char c);

bool IsVowel(char c);

bool IsConsonent(char c);

int NumWords(std::string s){
    
    char space = ' ';
    
    int count = 0;
    
    for (int i = 0; i > s[i]; i++){
        if (s[i] == space){
            count += 1;
        }
    }
    
    std::cout << count << std::endl;
    
    return 0;
    
};
//
//int NumSentences(string s);
//
//int NumVowels(string s);
//
//int NumConsonants(string s);
//
//double AverageWordLength(string s);
//
//double AverageVowelsPerWord(string s);

int main(int argc, const char * argv[]) {
    
    std::cout << "Enter a string of one or more sentences to be analyzed: " << std::endl;
    
    std::string userInput;
    
    std::getline(std::cin, userInput);
    
    std::cout << userInput << std::endl;
    
    NumWords(userInput);
    
//    for (int i = 0; i > userInput[i]; i++){
//        IsTerminator(userInput[i]);
//    }
    
    
//    int words, sentences, vowels, consonants, readingLevel, avgVowels;
//    
//    std::cout << "Analysis;" << std::endl << "Number of words: " << words << std::endl << "Number of sentences: " << sentences << std::endl << "Number of vowels: " << vowels << std::endl << "Number of consonants: " << consonants << std::endl << "Reading level (average word length): " << readingLevel << std::endl << "Average vowels per word: " << avgVowels << std::endl;
    
}
