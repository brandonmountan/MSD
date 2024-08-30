#include <iostream>

// void incrementArray (int numbers [], int size){
//   for (int i = 0; i < size; i++)
//   numbers[i] += 1;
// }

// int * createNumbers (){
// int numbers[5];
// numbers[0] = 2;
 
// return numbers;
// }

// void swap (int &a, int &b){
//   int temp = a;
//   a = b;
//   b = temp;
// }

void swap (int* a, int* b){
  int temp = *a;
  *a = *b; // assigning address of b to a
  *b = temp;
}



int main(int argc, const char * argv[]) {
    
    // adding schemes on launch
    
    std::cout << argv[1] << "\n" << argv[2] << std::endl;
  
  // int numbers[5] = {1, 2, 3, 4, 5};
  
  // incrementArray(numbers, 5);
  
  // for (int i = 0; i < 5; i++){
  //   std::cout << numbers[i];
  // }
  
  
  // int numbers[] ==== int * numbers
  // int * p;
  
  // int x = 5;
  
  // p = &x;
  
  // std::cout << x << std::endl;
  
  // std::cout << &p << std::endl;
  
  // std::cout << p << std::endl; // the address of x
  
  // std::cout << *p << std::endl; // the value that p
  
//  int num1 = 2, num2 = 55;
//  
//  swap(&num1, &num2);
//  
//  std::cout << "a: " << num1 << "\n" << "b: " << num2 << std::endl;

  return 0;
}
