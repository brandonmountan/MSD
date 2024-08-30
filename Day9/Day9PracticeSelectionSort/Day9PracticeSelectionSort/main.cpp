//
//  main.cpp
//  Day9PracticeSelectionSort
//
//  Created by Brandon Mountan on 8/29/24.
//

#include <iostream>
#include <vector>

void selectionSort (std::vector<int> &numbers){
    int min;
    for (int i = 0; i < numbers.size(); i++){
        min = i;
        for (int j = i + 1; j < numbers.size(); j++){
            if(numbers[min] > numbers[j]){
                min = j; // not swapping, just moving index
            }
            
        }
        
    }
    // swap numbers[i] with numbers[min]
}

int main(int argc, const char * argv[]) {
    // insert code here...
    std::cout << "Hello, World!\n";
    return 0;
}
