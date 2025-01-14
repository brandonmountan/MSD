//
//  main.cpp
//  TripleTemplate
//
//  Created by Brandon Mountan on 9/12/24.
//

#include <iostream>
#include <string>
#include <vector>

template <typename T>
struct Triple {
    T a;
    T b;
    T c;

    Triple(T d, T e, T f){
        a = d;
        b = e;
        c = f;
    }
    
    T sum(){
        return a + b + c;
    }
    
};

int main(int argc, const char * argv[]) {

    // Use Triple with strings
    Triple<std::string> stringObj( "Hello", "World", "Goodbye" );
    std::cout << stringObj.a << " \n" << stringObj.b << "\n" << stringObj.c << "\n";
    
    // Use Triple with integers
    Triple<int> intObj( 4, 5, 6 );
    std::cout << intObj.a << "\n" << intObj.b << "\n" << intObj.c << "\n";
    
    std::cout << stringObj.sum() << "\n";
    
    std::cout << intObj.sum() << "\n";
    
    Triple<std::vector<int>> tripleVec({1, 2, 3}, {4, 5, 6}, {7, 8, 9});
//    std::cout << tripleVec.a << "\n" << tripleVec.b << "\n" << tripleVec.c << "\n";


//    std::cout << tripleVec.sum() << "\n";

    
    return 0;
}
