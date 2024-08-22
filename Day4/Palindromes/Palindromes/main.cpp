//
//  main.cpp
//  Palindromes
//
//  Created by Brandon Mountan on 8/22/24.
//

#include <iostream>
#include <string>

int main(int argc, const char * argv[]) {
    
    std::string userInput;
    
    std::cout<<"please enter a palindrome using all lowercase letters \n";
    
    std::cin>>userInput;
    
    std::string reversed;
    
    for (int i=1; i<userInput.size()+1; i++){
        reversed = reversed + userInput[userInput.length()-i];
    }
    
    std::cout<<reversed<<std::endl;
    
    if (userInput==reversed){
        std::cout<<"nice palindrome :)";
    } else {
        std::cout<<"not a palindrome :(";
    }
    
    return 0;
}

