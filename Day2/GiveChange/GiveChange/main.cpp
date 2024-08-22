//
//  main.cpp
//  GiveChange
//
//  Created by Brandon Mountan on 8/20/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // Part 1 (day 2 lab)
    // defining initial inputs
//    int itemPrice, moneyInserted;
//    
//    // input 1
//    std::cout << "Please enter price of item in pennies: ";
//    std::cin >> itemPrice;
//    
//    // input 2
//    std::cout << "Please enter amount of money deposited in pennies: ";
//    std::cin >> moneyInserted;
//    
//    // calculating based on inputs
//    int change = moneyInserted - itemPrice;
//    std::cout << "Change: " << change << " cents\n";
//    
//    
//    // modifying variable 'change' based on types of coins
//    int quarters = change / 25;
//    change = change - (quarters * 25);
//    int dimes = change / 10;
//    change = change - (dimes * 10);
//    int nickels = change / 5;
//    change = change - (nickels * 5);
//    int pennies = change / 1;
//    
//    std::cout << "Quarters: " << quarters << "\n" << "Dimes: " << dimes << "\n" <<               "Nickels: " << nickels << "\n" << "Pennies: " << pennies << "\n";
    
    // Part 2 (part 1 of day 3 lab)
//    int itemPrice, moneyInserted;
//
//    // input 1
//    std::cout << "Please enter price of item in pennies: ";
//    std::cin >> itemPrice;
//
//    // input 2
//    std::cout << "Please enter amount of money deposited in pennies: ";
//    std::cin >> moneyInserted;
//    
//    // calculating based on inputs
//    int change = moneyInserted - itemPrice;
//    
//    if (moneyInserted > 0 && itemPrice > 0 && change > 0){
//        std::cout << "Change: " << change << " cents\n";
//        
//        int quarters = change / 25;
//        change = change - (quarters * 25);
//        int dimes = change / 10;
//        change = change - (dimes * 10);
//        int nickels = change / 5;
//        change = change - (nickels * 5);
//        int pennies = change / 1;
//        
//        std:: cout << "Quarters: " << quarters << "\n" << "Dimes: " << dimes << "\n" << "Nickels: " << nickels << "\n" << "Pennies: " << pennies << "\n";
//    } else if (moneyInserted > 0 && itemPrice > 0 && change < 0){
//        std::cout << "insufficient funds \n";
//    } else if (moneyInserted < 0 || itemPrice < 0){
//        std::cout << "user inputs cannot be negative \n";
//    }
    
    // Part 3 (part 2 of day 3 lab)
    int itemPrice, moneyInserted;

    // input 1
    std::cout << "Please enter price of item in pennies: ";
    std::cin >> itemPrice;

    // input 2
    std::cout << "Please enter amount of money deposited in pennies: ";
    std::cin >> moneyInserted;

    // calculating based on inputs
    int change = moneyInserted - itemPrice;

    if (moneyInserted > 0 && itemPrice > 0 && change > 0){
        std::cout << "Change: " << change << " cents\n";

    // modifying variable 'change' based on types of coins
    int quarters = change / 25;
    if (quarters > 2){
        quarters = 2;
    }
    change = change - (quarters * 25);
    
    int dimes = change / 10;
    if (dimes > 2){
        dimes = 2;
    }
    change = change - (dimes * 10);
        
    int nickels = change / 5;
    if (nickels > 2){
        nickels = 2;
    }
    change = change - (nickels * 5);
        
    int pennies = change / 1;
    if (pennies > 2){
        std::cout << "unable to return change, out of coins \n";
    }
        std::cout << "Quarters: " << quarters << "\n" << "Dimes: " << dimes << "\n" <<      "Nickels: " << nickels << "\n" << "Pennies: " << pennies << "\n";
    } else if (moneyInserted > 0 && itemPrice > 0 && change < 0){
        std::cout << "insufficient funds \n";
    } else if (moneyInserted < 0 || itemPrice < 0){
        std::cout << "user inputs cannot be negative \n";
    }
    
    return 0;
}
