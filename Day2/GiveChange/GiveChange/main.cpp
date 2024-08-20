//
//  main.cpp
//  GiveChange
//
//  Created by Brandon Mountan on 8/20/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // defining initial inputs
    int itemPrice, moneyInserted;
    
    // input 1
    std::cout << "Please enter price of item in pennies: ";
    std::cin >> itemPrice;
    
    // input 2
    std::cout << "Please enter amount of money deposited in pennies: ";
    std::cin >> moneyInserted;
    
    // calculating based on inputs
    int change = moneyInserted - itemPrice;
    std::cout << "Change: " << change << " cents\n";
    
    
    // modifying variable 'change' based on types of coins
    int quarters = change / 25;
    change = change - (quarters * 25);
    int dimes = change / 10;
    change = change - (dimes * 10);
    int nickels = change / 5;
    change = change - (nickels * 5);
    int pennies = change / 1;
    
    std:: cout << "Quarters: " << quarters << "\n" << "Dimes: " << dimes << "\n" << "Nickels: " << nickels << "\n" << "Pennies: " << pennies << "\n";
    return 0;
}
