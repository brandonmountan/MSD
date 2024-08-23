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

// part a) function

//int pythagoreanTheorem(int sideOne, int sideTwo){
//    
//    float result = (sideOne*sideOne) + (sideTwo*sideTwo);
//    
//    result = std::sqrt(result);
//    
//    return result;
//    
//}

// part b) function

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
    

int main(int argc, const char * argv[]) {

// part a)
    
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

    
// part b)
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
    
// part c)
// Which functions are being called in the Example code?
//  time(): Get the current calendar time as a value of type time_t.
//  asctime(): Interprets the contents of the tm structure pointed by timeptr as a calendar time and converts it to a C-string containing a human-readable version of the corresponding date and time.
//  localtime(): converts time since epoch to calendar time expressed as local time

    std::time_t result = std::time(nullptr);
    std::cout << std::asctime(std::localtime(&result))
              << result << " seconds since the Epoch\n";
    

    
    
}
