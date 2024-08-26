//
//  main.cpp
//  StringAnalyzer
//
//  Created by Brandon Mountan on 8/25/24.
//

#include <iostream>
#include <string>
#include "letter_helpers.hpp"
#include "word_helpers.hpp"

int main(int argc, const char * argv[]) {
    
    std::cout << "Enter a string of one or more sentences to be analyzed: " << std::endl;
    
    std::string userInput;
    
    std::getline(std::cin, userInput);
    
    std::cout << "Analysis:" << std::endl << "Number of words: " << NumWords(userInput) << std::endl << "Number of sentences: " << NumSentences(userInput) << std::endl << "Number of vowels: " << NumVowels(userInput) << std::endl << "Number of consonants: " << NumConsonants(userInput) << std::endl << "Reading level (average word length): " << AverageWordLength(userInput) << std::endl << "Average vowels per word: " << averageVowelsPerWord(userInput) << std::endl;
    
    while (userInput != "done"){
        std::cout << "Enter another string of one or more sentences to be analyzed: " << std::endl;
        std::getline(std::cin, userInput);
        std::cout << "Analysis:" << std::endl << "Number of words: " << NumWords(userInput) << std::endl << "Number of sentences: " << NumSentences(userInput) << std::endl << "Number of vowels: " << NumVowels(userInput) << std::endl << "Number of consonants: " << NumConsonants(userInput) << std::endl << "Reading level (average word length): " << AverageWordLength(userInput) << std::endl << "Average vowels per word: " << averageVowelsPerWord(userInput) << std::endl;
    }
    
    return 0;
}
