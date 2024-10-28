//
//  main.cpp
//  DateFormats
//
//  Created by Brandon Mountan on 8/22/24.
//

#include <iostream>
#include <string>

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
        std::string jan = "January";
        std::string feb = "February";
        std::string mar = "March";
        std::string apr = "April";
        std::string may = "May";
        std::string june = "June";
        std::string july = "July";
        std::string aug = "August";
        std::string sep = "September";
        std::string oct = "October";
        std::string nov = "November";
        std::string dec = "December";
        
        if (mm >= 1 && mm <= 1){
            std::cout << jan << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 2 && mm <= 2){
            std::cout << feb << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 3 && mm <= 3){
            std::cout << mar << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 4 && mm <= 4){
            std::cout << apr << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 5 && mm <= 5){
            std::cout << may << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 6 && mm <= 6){
            std::cout << june << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 7 && mm <= 7){
            std::cout << july << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 8 && mm <= 8){
            std::cout << aug << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 9 && mm <= 9){
            std::cout << sep << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 10 && mm <= 10){
            std::cout << oct << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 11 && mm <= 11){
            std::cout << nov << " " << dd << ", " << yyyy << std::endl;
        } else if (mm >= 12 && mm <= 12){
            std::cout << dec << " " << dd << ", " << yyyy << std::endl;
        }
    }
    return 0;
}
