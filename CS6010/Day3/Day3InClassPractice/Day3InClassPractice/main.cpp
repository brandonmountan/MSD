//
//  main.cpp
//  Day3InClassPractice
//
//  Created by Brandon Mountan on 8/21/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    // insert code here...
    for(int i=0; i<5; i++){
        for (int j=0; j<i; j++)
            std::cout<<"helloworld";
        
        std::cout<<"\n";
    }
    return 0;
}

