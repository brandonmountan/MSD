//
//  main.cpp
//  roadTripCalculator
//
//  Created by Brandon Mountan on 8/20/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // insert code here...
    // defining initial inputs
    int distance;
    int mpg;
    float gasPrice;
    // asking for initial inputs
    std::cout << "Enter driving distance in miles as a whole number \n";
    std::cin >> distance;
    std::cout << "Enter your car's miles per gallon to nearest whole number \n";
    std::cin >> mpg;
    std::cout << "Enter cost of gas per gallon to nearest penny \n";
    std::cin >> gasPrice;
    // performing calculations
    float gallons = distance / mpg;
    float totalCost = gallons * gasPrice;
    // output
    std::cout << "Gallons used: " << gallons << "\nTotal cost of gas: " << totalCost << "$" << "\n";
    return 0;
}
