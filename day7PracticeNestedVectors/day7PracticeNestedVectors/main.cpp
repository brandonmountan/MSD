//
//  main.cpp
//  day7PracticeNestedVectors
//
//  Created by Brandon Mountan on 8/27/24.
//

#include <iostream>
#include <vector>

int main(int argc, const char * argv[]) {
    // day 7 nested vectors
    
    std::vector<std::vector<std::string>> table;
    
    std::vector<std::string> fruits = {"Apple", "Orange"};
    
    std::vector<std::string> vegetables = {"Cucumber, Broccoli"};
    
    table.push_back(fruits);
    
    table.push_back(vegetables);
    
    table.push_back({"Milk", "Cheese"});
    
    table[0]; // vector of strings --> fruits
            // +
    fruits[0]; // value of string --> apple
            // =
    table[0][0]; //  -----> Apple
    
//    std::cout << table[0][0] << std::endl;
    
    for (int i = 0; i < table.size(); i++)
        for (int j = 0; j < table[i].size(); j++)
            
            std::cout << table[i][j] << std::endl;
    
    for (std::vector<std::string> row : table){
        for (std::string entry: row){
            std::cout << entry << std::endl;
        }
    }
    
    
    // create program that stores student information
    // name string
    // id number int
    // grades std::vector<double>
    // gpa double
    
    std::vector<
    
    return 0;
}
