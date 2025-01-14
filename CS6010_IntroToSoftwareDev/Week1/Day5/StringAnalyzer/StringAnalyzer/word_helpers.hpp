//
//  word_helpers.hpp
//  StringAnalyzer
//
//  Created by Brandon Mountan on 8/26/24.
//

#ifndef word_helpers_hpp
#define word_helpers_hpp

#include <stdio.h>
#include <string>

bool IsTerminator(char c);
int NumWords(std::string s);
int NumSentences(std::string s);
double AverageWordLength(std::string s);
double averageVowelsPerWord(std::string s);

#endif /* word_helpers_hpp */
