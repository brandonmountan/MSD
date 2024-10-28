//
//  main.cpp
//  CommandLineArgs
//
//  Created by Brandon Mountan on 8/30/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {

    // insert code here...
//    for (int i = 0; i < argc; i++)
//    std::cout << argv[i] << std::endl;
    // Print the number of arguments
    std::cout << "Number of arguments: " << argc << std::endl;

    // Print each argument
    for (int i = 0; i < argc; ++i) {
        std::cout << "Argument " << i << ": " << argv[i] << std::endl;
    }
    
//    Steps to Compile and Run
    //    Open Terminal and navigate to your project directory using cd and tab completion.
    //    Compile the code:
    //    bash
    //    Copy code
    //    clang++ -std=c++11 -c main.cpp
    //    Link the compiled file:
    //    bash
    //    Copy code
    //    clang++ -o main main.o
    //    Run your program without arguments:
    //    bash
    //    Copy code
    //    ./main
    //    Run your program with arguments:
    //    bash
    //    Copy code
    //    ./main these are command line arguments
//    This will display the program name and the command line arguments passed to it.

    return 0;
}
