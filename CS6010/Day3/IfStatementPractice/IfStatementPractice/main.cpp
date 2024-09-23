//
//  main.cpp
//  IfStatementPractice
//
//  Created by Brandon Mountan on 8/21/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // Part 1
    // declaring age a variable and taking
    int age;
    std::cout << "Please enter your age: \n";
    std::cin >> age;
    
    // determining if the user is old enough to vote
    if (age >= 18){
        std::cout << "Welcome to democracy, you are old enough to vote. \n";
    } else {
        std::cout << "You are not old enough to vote, sorry. \n";
    }
    
    if (age >= 30){
        std::cout << "Congratulations, you are old enough to run for senate. \n";
    } else {
        std::cout << "You are not old enough to run for senate, sorry. \n";
    }
    
    // outputting which generation the user is apart of
    if (age >= 80){
        std::cout << "Wow, you are part of the Greatest Generation. \n";
    } else if (age >= 60 && age <= 80){
        std::cout << "You are part of the Baby Boomer generation. \n";
    } else if (age >= 40 && age <= 60){
        std::cout << "You are part of Generation X. \n";
    } else if (age >= 20 && age <= 40){
        std::cout << "You are a silly Millenial. \n";
    } else {
        std::cout << "You are an iKid. \n";
    };
    
    // Part 2
    bool isWeekday;
    char weekdayResponse;
    std::cout << "Please tell me, is it a weekday today? (y/n) \n";
    std::cin >> weekdayResponse;
    
    if (weekdayResponse == 'y'){
        isWeekday = true;
    } else if (weekdayResponse == 'n'){
        isWeekday = false;
    } else {
        std::cout << "invalid response. \n";
        return 0;
    }
    
    bool isHoliday;
    char holidayResponse;
    std::cout << "Is today a holiday? (y/n) \n";
    std::cin >> holidayResponse;
    
    if (holidayResponse == 'y') {
        isHoliday = true;
    } else if (holidayResponse == 'n') {
        isHoliday = false;
    } else {
        std::cout << "invalid response. \n";
        return 0;
    }
    
    bool youngChildren;
    char childrenResponse;
    std::cout << "Do you have young children? (y/n) \n";
    std::cin >> childrenResponse;
    
    if (childrenResponse == 'y') {
        youngChildren = true;
    } else if (childrenResponse == 'n') {
        youngChildren = false;
    } else {
        std::cout << "invalid response. \n";
        return 0;
    }
    
    if (isWeekday == true || isHoliday == true || youngChildren == true){
        std::cout << "Early bird gets the worm \n";
    } else {
        std::cout << "sleep in! \n";
    }

    return 0;
}
