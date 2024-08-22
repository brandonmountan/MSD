//
//  main.cpp
//  MagicDates
//
//  Created by Brandon Mountan on 8/22/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // creating userInput string variable
     std::string userInput;
     std::cout << "enter a date in the following format MM/DD/YYYY \n";
     std::cin >> userInput;
     
     std::string slash = "/";
     
     std::size_t found = userInput.find(slash);
         
     std::string month = userInput.substr(0, found);
         
     std::size_t foundTwo = userInput.find(slash, found+1);
             
     std::string day = userInput.substr(found+1, foundTwo-found-1);
         
     std::string year = userInput.substr(foundTwo+1, 4);
         
     int mm = std::stoi(month);
     int dd = std::stoi(day);
     int yyyy = std::stoi(year);
         
     if (mm < 0 || mm > 12){
         std::cout << "invalid date \n";
     } else if (dd < 0 || dd > 31){
         std::cout << "invalid date \n";
     } else if (yyyy < 1000 || yyyy > 9999){
         std::cout << "invalid date \n";
     } else {
         std::string magicYear = std::to_string(yyyy);
         std::string subMagicYear = magicYear.substr(2, 2);
         std::cout << subMagicYear << std::endl;
         int yearAbv = std::stoi(subMagicYear);
         
         if (mm*dd>=yearAbv && mm*dd<=yearAbv){
             std::cout << "Magic Date! :)" << std::endl;
         } else {
             std::cout << "not a Magic Date :(" << std::endl;
         }
     }
    return 0;
}
