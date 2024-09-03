//
//  main.cpp
//  BookAnalyzer
//
//  Created by Brandon Mountan on 8/31/24.
//

#include <iostream>
#include <fstream>
#include <vector>
#include <array>
#include <string>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

//void findMin(std::vector<std::string> vectorOfWords){
//    std::string strInput{};
//    vectorOfWords.push_back(strInput);
//    int minWordLength = std::stoi(vectorOfWords[0].length());
//    for (int j = 1; j < vectorOfWords.size(); j++){
//        if (vectorOfWords[j].size() < minWordLength) {
//            int newMinWordLength = std::stoi(vectorOfWords[j]);
//            minWordLength = newMinWordLength;
//        }
//        std::cout << minWordLength << std::endl;
//    }
//}


//void findMax(std::string str){
//    for (int i = 0; ; i++){
//        
//    }
//}
//
//void findTitle(std::string str) {
//    
//    
//    
//}
//
//void findAuthor(std::string str) {
//    
//    
//    
//}
//
//void findKeyWord (int argc, const char * argv[]) {
//    
//        std::ifstream inf{ argv[1] };
//
//    
//}

int main(int argc, const char * argv[]) {
    
    // ifstream is used for reading files
//    std::ifstream inf{ argv[1] };
    std::ifstream inf{ "Sample.txt" };


    // If we couldn't open the output file stream for reading
    if (!inf)
    {
        // Print an error and exit
        std::cerr << "Book not found! Please try another input. \n";
        return 1;
    }

    // While there's still stuff left to read
    std::string strInput{};
    
    std::vector<std::string> words;
    
    int wordCount = 0, characterCount = 0;
    
    long minWordLength;
    
    while (inf >> strInput){
        wordCount++;
        words.push_back(strInput);
        long minWordLength = words[0].length();
        for (int j = 0; j < words[j].length(); j++){
            if (words[j+1].length() < words[j].length())
                minWordLength = words[j+1].length();
        }
        for (int i = 0; i < strInput.size(); i++){
            characterCount++;
        }
    }
    
    
    std::cout << "Total number of words: " << wordCount << std::endl;
    std::cout << "Total number of characters: " << characterCount << std::endl;
    
    return 0;
    
    
    
//    The title of the book (see below)
//    The author of the book (see below)
//    The total number of words in the file
//    The total number of characters in the file (excluding whitespace).
//    The shortest word in the book
//    The longest word in the book
//    The number of appearances, and locations of, the users key word (see below)

    // When inf goes out of scope, the ifstream
    // destructor will close the file
        
    return 0;
}
