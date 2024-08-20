//
//  main.cpp
//  VolumeConvert
//
//  Created by Brandon Mountan and Thomas Ho on 8/20/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // insert code here...
    float ounces;
    std::cout << "Please enter number of ounces: \n";
    std::cin >> ounces;
    float cups, pints, gallons, liters, cubicIn;
    cups = ounces/8;
    pints = ounces/16;
    gallons = ounces/128;
    liters = ounces*.0296;
    cubicIn = ounces*1.8;
    std::cout << "Ounces: " << ounces << "\n Cups: " << cups << "\n Pints: " << pints << "\n Gallons: " << gallons << "\n Liters: " << liters << "\n Cubic Inches: " << cubicIn << "\n";
    return 0;
}
