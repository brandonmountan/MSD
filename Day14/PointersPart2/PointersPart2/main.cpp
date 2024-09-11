//
//  main.cpp
//  PointersPart2
//
//  Created by Brandon Mountan on 9/10/24.
//

#include <iostream>


//double* copy(double* myArray, int arraySize){
//
//    myArray = new double[arraySize];
//    
//    for(int i = 0; i < arraySize; i++){
//        myArray[i] = i;
//    }
//    
//    return myArray;
//}
//
//int main(int argc, const char * argv[]) {
//
//    double* mainArray = nullptr;
//    
//    int arraySize = 5;
//    
//    std::cout << copy(mainArray, arraySize) << std::endl;
//    
//    delete mainArray;
//    
//    return 0;
//}


//
//  main.cpp
//  PointersPart2
//
//  Created by Brandon Mountan on 9/10/24.
//

#include <iostream>


double* copy(double*& myArray, int arraySize){

    myArray = new double[arraySize];
    
    for(int i = 0; i < arraySize; i++){
        myArray[i] = i;
    }
    return myArray;
}

int main(int argc, const char * argv[]) {

    double* mainArray;
    
    int arraySize = 5;
    
    std::cout << copy(mainArray, arraySize) << std::endl;
    
    delete mainArray;
    
    return 0;
}
