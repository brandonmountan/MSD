//
//  main.cpp
//  InClassPracticeErrors
//
//  Created by Brandon Mountan on 9/10/24.
//

#include <iostream>
#include <cstdlib>

int sum(int* arr, int size);

int main(int argc, const char * argv[]) {

    
    int size = rand() % 20;
//    int arr[size]; don't do this (recent introduction to C)
    int* arr = new int[size]; // use new to allocate memory
    
    for (int i = 0; i < size; i++){
        arr[i] = i;
    }
    
    std::cout << sum(arr, size) << std::endl;
    
    delete [] arr; // free memory when I'm done
//    sum(arr, size);
    std::cout << sum(arr, size) << std::endl;
    return 0;
}


int sum(int* arr, int size){
    int ret = 0;
    for (int i = 0; i < size; i++){
        /*std::cout << i - size << std::endl;*/ // i - size tells me how far out of bounds I am. won't crash even if I am a million places OOB
        ret += arr[i];
    }
    return ret;
}

// buffer is a chunk of memory. heap buffer means memory on the heap. overflow means I went OOB of what I allocated.
// READ of size 4. --> reading a 4 byte thing.
// is located 4 bytes before 28-byte region means i started 1 before
