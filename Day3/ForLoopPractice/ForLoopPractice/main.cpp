//
//  main.cpp
//  ForLoopPractice
//
//  Created by Brandon Mountan on 8/21/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // for loops, technically less lines of code and easier for me to understand
//    int i;
//    for (i=1; i<=10; i++){
//        std::cout << i <<"\n";
//    }
    
    // while loops,
//    int j=1;
//    while (j<=10){
//        std::cout << j << "\n";
//        j++;
//    }
    
//    int num1, num2;
//    std::cout << "please enter any whole number. \n";
//    std::cin >> num1;
//    std::cout << "please enter another whole number. \n";
//    std::cin >> num2;
//
//    if (num1 < num2){
//        int k=num1;
//        while (k <= num2){
//            std::cout << k << "\n";
//            k++;
//        }
//    } else if (num1 > num2){
//        int k=num1;
//        while (k >= num2){
//            std::cout << k << "\n";
//            k--;
//        }
//    }
    
//    int num3;
//    for (num3=1; num3<=20; num3+=2){
//        std::cout << num3 << "\n";
//    }
    
//    int num4;
//    for (num4=1; num4<=20; num4+=2){
//        if (num4 % 2 != 0){
//            std::cout << num4 << "\n";
//        } else {
//            std::cout << num4 << "\n";
//        }
//    }
//    
//
    
//    int userInput = 0;
//    int sum = 0;
//    while (userInput >=0){
//        std::cout << "please enter a number \n";
//        std::cin >> userInput;
//        if (userInput >= 0){
//            sum = sum + userInput;
//        }
//    }
//    std::cout << sum << "\n";
//    
    int i, j;
    for (i=1; i<=5; i++){
        std::cout << i << "\n";
        for (j=1; j<=5; j++){
            std::cout << i*j;
        }
    }
    return 0;
}
