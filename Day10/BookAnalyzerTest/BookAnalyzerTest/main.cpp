//
//  main.cpp
//  BookAnalyzerTest
//
//  Created by Brandon Mountan on 8/31/24.
//

#include <iostream>
#include <fstream>

int main() {
//    // ofstream is used for writing files
//    // We'll make a file called Sample.txt
//    std::ofstream outf{ "Sample.txt" };
//
//    // If we couldn't open the output file stream for writing
//    if (!outf)
//    {
//        // Print an error and exit
//        std::cerr << "Uh oh, Sample.txt could not be opened for writing!\n";
//        return 1;
//    }
//
//    // We'll write two lines into this file
//    outf << "This is line 1\n";
//    outf << "This is line 2\n";
//
//    return 0;
//
//    // When outf goes out of scope, the ofstream
//    // destructor will close the file
    // ifstream is used for reading files
    // We'll read from a file called Sample.txt
    std::ifstream inf{ "Sample.txt" };

    // If we couldn't open the output file stream for reading
    if (!inf)
    {
        // Print an error and exit
        std::cerr << "Uh oh, Sample.txt could not be opened for reading!\n";
        return 1;
    }

    // While there's still stuff left to read
    std::string strInput{};
    while (inf >> strInput)
        std::cout << strInput << '\n';

    // When inf goes out of scope, the ifstream
    // destructor will close the file
    
    return 0;
}
