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

struct Words {
    double length;
    std::string wordString;
};

//std::vector<Words> wordAnalyzer(std::vector<std::string> words){
//    std::vector<Words> container = {};
//    for (int i = 0; i < words.size(); i ++){
//        Words word;
//        word.wordString = words[i];
//        word.length = words[i].length();
//        container.push_back(word);
//    }
//    return container;
//}

void findMin(std::vector<Words> containerOfWords){
    for (int i = 0; i < containerOfWords.size(); i++){
        int minWordLength = containerOfWords[0].length;
        if (containerOfWords[i+1].length < containerOfWords[i].length){
            minWordLength = containerOfWords[i+1].length;
            std::string shortestWord = containerOfWords[i+1].wordString;
        }
    }
}


//void findMax(std::string str){
//    for (int i = 0; ; i++){
//        
//    }
//}
//
//void findTitle(std::string str) {
//    
//    //        std::ifstream inf{ argv[1] };
//          'Title:' is the key word
//}         for(int i = 0; i < array.size(); i++)
//              if (word[i].wordString = "Title:")
//              std::cout << word[i+1].wordString
//
//
//void findAuthor(std::string str) {
//    
//    //        std::ifstream inf{ argv[1] };
//          'Author:' is the key word
//
//}
//
//void findKeyWord (int argc, const char * argv[]) {
//    
//        std::ifstream inf{ argv[1] };
//          argv[2] is the key word
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
        
    std::vector<Words> container = {};
    
    int wordCount = 0, characterCount = 0;
    
    while (inf >> strInput){
        wordCount++;
        Words word;
        word.length = strInput.length();
        word.wordString = strInput;
        container.push_back(word);
        for (int i = 0; i < strInput.size(); i++){
            characterCount++;
        }
    }
        
    std::string shortestWord;
    
    std::string longestWord;
    
    for (int i = 0; i < container.size(); i++){
        int minWordLength = container[0].length;
        if (container[i+1].length < container[i].length){
            minWordLength = container[i+1].length;
            shortestWord = container[i+1].wordString;
        }
    }
    
    for (int i = 0; i < container.size(); i++){
        int maxWordLength = container[0].length;
        if (container[i+1].length > container[i].length)
            maxWordLength = container[i+1].length;
//            shortestWord = container[i+1].wordString;
    }
    

    
    std::cout << container[1].wordString << std::endl;
    
    std::cout << "Total number of words: " << wordCount << std::endl;
    std::cout << "Total number of characters: " << characterCount << std::endl;
    
//    The title of the book (see below)
//    The author of the book (see below)
//    The total number of words in the file
//    The total number of characters in the file (excluding whitespace).
//    The shortest word in the book
//    The longest word in the book
//    The number of appearances, and locations of, the users key word (see below)
        
    return 0;
}
