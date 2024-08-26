//
//  main.cpp
//  StringAnalyzer
//
//  Created by Brandon Mountan on 8/25/24.
//

#include <iostream>
#include <string>

bool IsTerminator(char c){
    return (c == '.' || c == '?' || c == '!');
};

bool IsPunctuation(char c){
    return (c == '.' || c == '?' || c == '!' || c == ',');
};

bool IsVowel(char c) {
    return (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y');
};

bool IsConsonent(char c){
    return (c == 'b' || c == 'c' || c == 'd' || c == 'f' || c == 'g' || c == 'h' || c == 'j' || c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'p' || c == 'q' || c == 'r' || c == 's' || c == 't' || c == 'v' || c == 'w' || c == 'x' || c == 'z');
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
