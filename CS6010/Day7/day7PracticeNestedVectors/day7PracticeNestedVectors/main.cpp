//
//  main.cpp
//  day7PracticeNestedVectors
//
//  Created by Brandon Mountan on 8/27/24.
//

#include <iostream>
#include <vector>

struct Student {
    std::string name;
    int idNumber;
    std::vector<double> grades;
    double gpa;
};

double calculateAverage(Student student){
    std::vector<double> grades = student.grades;
    int sum = 0;
    for (int i = 0; i < grades.size(); i++)
        sum+= grades[i];
    
    return sum/grades.size();
}

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
    
    
    Student student1;
    student1.name = "brandon";
    student1.gpa = 70.1;
    student1.idNumber = 6033375;
    student1.grades.push_back(50);
    student1.grades.push_back(60);

    
    std::vector<Student> students;
    //std::vector <int> numbers
    //number.push_back(4)
    
    students.push_back(student1);
    
    //function that calculates the averaage grade for a student. return average grade for a student
    
    std::cout << "the student average = " << calculateAverage(student1) << std::endl;
    
    student1.gpa = calculateAverage(student1);
    
    return 0;
}
