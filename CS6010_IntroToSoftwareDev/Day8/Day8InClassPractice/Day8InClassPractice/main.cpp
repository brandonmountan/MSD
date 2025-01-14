//
//  main.cpp
//  Day8InClassPractice
//
//  Created by Brandon Mountan on 8/28/24.
//

#include <iostream>
#include <vector>

// "passing values by reference" --> typing &a and &b instead. first way is "pass by value"
// this swaps the numbers without creating new memory spaces.
void swap (int a, int b){
 int temp;
  temp = a;
  a = b;
  b = temp;
}

void swapText (std::string &str1, std::string &str2){
    std::string temp;
    temp = str1;
    str1 = str2;
    str2 = temp;
}

struct Student {
    int id;
    double gpa;
};

void swapGpa (Student &student1, Student &student2){
    
    double temp = student1.gpa;
    
    student1.gpa = student2.gpa;
    student2.gpa = temp;
    
}

void printValues(const std::vector<int> &numbers){
    for (int num : numbers)
//        numbers[0] = 55; // error code d/t const
        std::cout << num << std::endl;
}

int main(int argc, const char * argv[]) {
    
    int num1, num2;
    num1 = 5;
    num2 = 10;
    // how do you swap values of num1 and num2
    int temp = num1;
    num1 = num2;
    num2 = temp;

    std::string hello = "world";
    std::string goodbye = "test";
    
    swapText(hello, goodbye);
    
    Student student1;
    Student student2;
    
    student1.gpa = 100;
    student2.gpa = 90;
    
    swapGpa(student1, student2);
    
    
    // part 2 for each loops
    
    
    std::vector<int> numbers = {1,2,3,4,5};
    
    
    // &num accesses memory location an replaces
    for (int &num : numbers)
        num=2;
    
    printValues(numbers);
    
//    for (int num : numbers)
//        std::cout << num << std::endl;
    
    
    return 0;
}
