//
//  main.cpp
//  FunctionPractice
//
//  Created by Brandon Mountan on 8/23/24.
//

#include <iostream>
#include <cmath>
#include <math.h>
#include <ctime>

// part 1 a) function

//int pythagoreanTheorem(int sideOne, int sideTwo){
//    
//    float result = (sideOne*sideOne) + (sideTwo*sideTwo);
//    
//    result = std::sqrt(result);
//    
//    return result;
//    
//}

// part 1 b) function

//double xVelocity(double speed, double angle){
//        
//    double resultX = speed*cos(angle*(M_PI/180));
//    
//    return resultX;
//}
//
//double yVelocity(double speed, double angle){
//    
//    double resultY = speed*sin(angle*(M_PI/180));
//    
//    return resultY;
//}

// Part 2

// Write a function that performs the hypotenuse task described above, but does not read from std::cin. What parameters should it take, and what will it return? Why wouldn't you want to get the input from std::cin inside your function?

// pythagoreanTheoremTwo(someOtherParameter, someOtherParameterTwo){
//
//          float result = ?
//
// }

// Why would it be difficult to turn the speed/velocity task above into a function? What imperfect solutions can you come up with that wrap that code into one (or more) functions?

// Write a function isCapitalized that takes in a string parameter and returns whether or not the string starts with a capital letter.


//void isCapitalized(){
//    std::cout << "enter a word" << std::endl;
//    std::string userInput;
//    std::cin >> userInput;
//    if(userInput[0]>='A' && userInput[0]<='Z'){
//        std::cout << "the first letter of your word is capitalized" << std::endl;
//    } else {
//        std::cout << "the first letter of your word is not capitalized" << std::endl;
//    }
//}

//  Write a function boolToString that takes in a Boolean parameter and returns the string "true" or "false" depending on its value. Use this function to display the results of testing the isCapitalized function.

bool boolToString(){
    
    bool t;
    
    std::cout << "enter a value " << std::endl;
    
    std::cin >> t;
        
    return t;
}

    

int main(int argc, const char * argv[]) {

// Part 1 a)
    
//    int sideOne, sideTwo;
//        
//    std::cout << "please enter first side length" << std::endl;
//    
//    std::cin >> sideOne;
//    
//    std::cout << "please enter second side length" << std::endl;
//    
//    std::cin >> sideTwo;
//    
//    int result = pythagoreanTheorem(sideOne, sideTwo);
//    
//    std::cout << "the length of the third side is: " << result << std::endl;
//    

    
// Part 1 b)
//    double speed, angle;
//    
//    std::cout << "please enter speed " << std::endl;
//    
//    std::cin >> speed;
//    
//    std::cout << "please enter angle " << std::endl;
//    
//    std::cin >> angle;
//    
//    double resultX = xVelocity(speed, angle);
//    
//    double resultY = yVelocity(speed, angle);
//    
//    std::cout << "your x velocity is: " << resultX << std::endl;
//    
//    std::cout << "your y velocity is: " << resultY << std::endl;
//    
//    return 0;
//    
    
// Part 1 c)
// Which functions are being called in the Example code?
//  time(): Get the current calendar time as a value of type time_t.
//  asctime(): Interprets the contents of the tm structure pointed by timeptr as a calendar time and converts it to a C-string containing a human-readable version of the corresponding date and time.
//  localtime(): converts time since epoch to calendar time expressed as local time

//    std::time_t result = std::time(nullptr);
//    std::cout << std::asctime(std::localtime(&result))
//              << result << " seconds since the Epoch\n";
//    
    
//  Part 2 isCapitalized()
    
//    isCapitalized();
    
// Part 2 boolToString
    
    boolToString();
        
}
