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
#include <stdlib.h>
#include <algorithm>

struct Author {
    int authorStart;
    int authorEnd;
    std::string authorStr;
};

struct KeyWord {
    int keywordStart;
    int keywordEnd;
    std::string keywordStr;
    
};

std::string findMin(std::vector<std::string> containerOfWords){
    std::string shortestWord = containerOfWords[0];
    for (int i = 0; i < containerOfWords.size(); i++){
        if (shortestWord.size() > containerOfWords[i].size()){
            shortestWord = containerOfWords[i];
        }
    }
    return shortestWord;
}

// try selection sort

std::string findMax(std::vector<std::string> containerOfWords){
    std::string longestWord = containerOfWords[0];
    for (int i = 0; i < containerOfWords.size(); i++){
        if (longestWord.size() < containerOfWords[i].size()){
            longestWord = containerOfWords[i];
        }
    }
    return longestWord;
}


std::string findTitle(std::vector<std::string> v){
    
    std::string title;
        
    int x = 0, y = 0;
    
    for (int i = 0; i < v.size(); i++){
        if (v[i] == "Title:"){
            x = i;
        }
        if (v[i] == "Author:"){
            y = i;
        }
    }
    for (int j = x + 1; j < y; j++) {
        title += v[j] + "\n";
    }
    return title;
}

std::string findAuthor(std::vector<std::string> v) {
    
    std::string author;
        
    int x = 0, y = 0;
    
    for (int i = 0; i < v.size(); i++){
        if (v[i] == "Author:"){
            x = i;
        }
        if (v[i] == "Release"){
            y = i;
        }
    }
    for (int j = x + 1; j < y; j++) {
        author += v[j] + " ";
    }
    return author;
}

//int analyzer(int argc, const char * argv[]) {
//    
//    std::ifstream myFile{ argv[1] };
//    
//    std::string keyword = argv[2];
//        
//    if (!myFile) {
//        std::cerr << "Book not found! Please try another input. \n";
////        return 1;
//    }
//    
//    std::string strInput{};
//
//    std::vector<std::string> container = {};
//    
//    int keywordCount = 0;
//        
//    while (myFile >> strInput){
//        container.push_back(strInput);
//    }
//        
//    int x = 0;
//        
//    for (int i = 0; i < container.size(); i++){
//        if (container[i] == keyword){
//            x = i;
//            keywordCount++;
//            std::string wordBefore = container[i-1];
//            std::string wordAfter = container[i+1];
//            int spotInBook = x / container.size();
//            std::cout << "Spot"
//        }
//    }
//    
//    return keywordCount;
////    number of times;
////    string will be keyword - 1, keyword, keyword + 1
////    % will be index at keyword divided by total index
////    for (int j = x + 1; j < y; j++) {
////        author += v[j] + "\n";
////    }
////    return author;
//
//}

int main(int argc, const char * argv[]) {
    
    // ifstream is used for reading files
    std::ifstream myFile{ argv[1] };
    std::string keyword = argv[2];
//    std::ifstream myFile{ "winniethepooh.txt" };
    
    if (!myFile) {
        std::cerr << "Book not found! Please try another input. \n";
        return 1;
    }
    
    std::string strInput{};

    std::vector<std::string> container = {};
    
    int wordCount = 0, characterCount = 0;
    
    while (myFile >> strInput){
        wordCount++;
        characterCount += strInput.size();
        container.push_back(strInput);
    }
    
    int keywordCount = 0;
    
    int x = 0;
        
    for (int i = 0; i < container.size(); i++){
        if (container[i] == keyword){
            x = i;
            keywordCount++;
            std::string wordBefore = container[i-1];
            std::string wordAfter = container[i+1];
            double spotInBook = i / container.size() * 100;
            std::cout << wordBefore << " " << keyword << " " << wordAfter << "\n";
            std::cout << "Spot in the book as a percent: " << spotInBook << "\n";
         }
    }
    
    std::cout << "Keyword count: " << keywordCount << std::endl;
        
    std::string shortestWord = findMin(container);
    
    std::string longestWord = findMax(container);

    std::cout << "The shortest word in the book is: " << shortestWord << std::endl;
    std::cout << "The longest word in the book is: " << longestWord << std::endl;
    std::cout << "Total number of words: " << wordCount << std::endl;
    std::cout << "Total number of characters: " << characterCount << std::endl;
    std::cout << "The title of the book is: " << findTitle(container) << std::endl;
    std::cout << "The author of the book is: " << findAuthor(container) << std::endl;
        
    return 0;
}
