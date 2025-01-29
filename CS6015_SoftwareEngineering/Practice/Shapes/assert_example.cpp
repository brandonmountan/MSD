#include <iostream>     
//#define NDEBUG //uncomment to disable assertions
#include <cassert>

using namespace std;

void printNumber(int* myNum) {
    assert (myNum!=nullptr);
    cout<<"myInt contains value" << " = "<<*myNum<<endl;
}

int main (){
    
    int myVal=5;
    int * secondPtr= nullptr;
    int * thirdPtr = nullptr;
    
    secondPtr=&myVal;
    
    printNumber (secondPtr);
    printNumber (thirdPtr);
    
    return 0;
}
