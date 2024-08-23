//
//  main.cpp
//  Day5Practice
//
//  Created by Brandon Mountan on 8/23/24.
//  calculatePower ( int x, in ex1, int y, int ex2, int z, int ex3, ... )
//  vs.
//  calcluatePower (int x, int ex1 ); call function 3 + times

  int calculatePower ( int base, int exponent ) {
      int result = 1;
      for (int i=0; i<exponent; i++);
      result = result*base;
      return result;
  }

#include <iostream>

int main(int argc, const char * argv[]) {

    // results = x^ex1 + y^ex^ex2 + z^ex3
    
    int x = 2;
    int ex1 = 2;
    int y = 3;
    int ex2 = 2;
    int z = 4;
    int ex3 = 2;
    int result = 0;
    
    result = calculatePower(2, 2) + calculatePower(3, 2) + calculatePower (4, 2);
    
    std::cout << result << std::endl;
    
    return 0;
}
