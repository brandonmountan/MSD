//
//  main.cpp
//  Day4InClassPractice
//
//  Created by Brandon Mountan on 8/22/24.
//

#include <iostream>
#include <cassert>


// Day 5, functions instead of if/else statements

//bool isPasswordLengthValid(){
//    
//}
//
//bool containsSpecialCharacter(){
//    
//}
//
//bool isFirstLetterCapital(){
//    
//}
//
//bool isValidPassword(std::string password){
//    if(isPasswordLengthValid()){
//        
//    } else if (containsSpecialCharacter()){
//        
//    } else if (isFirstLetterCapital()){
//        
//    } else {
//        std::cout << "password is strong" << std::endl;
//    }
//}
//

int main(int argc, const char * argv[]) {
    std::string password;
    bool isValid=false;
    
    while(!isValid){
        std::cout<<"please enter password \n";
        std::cin>>password;
        
        if(password.length()<8){
            std::cout<<"enter at least 8 characters \n";
        } else if (password.find("$") == std::string::npos){
            std::cout<<"please include '$' in password \n";
        } else if (!(password[0]>='A' && password[0]<='Z')){
            std::cout<<"please make first letter of password capitalized \n";
        } else {
            isValid=true;
            std::cout<<"strong password \n";
        }
    }
    
    return 0;
}
