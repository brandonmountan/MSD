//
//  main.cpp
//  NumberRepresentations
//
//  Created by Brandon Mountan on 9/4/24.
//

#include <iostream>
#include <cstdint>
#include <cassert>
#include <iomanip>
#include <cmath>
#include <fstream>
#include <string>
#include <vector>
#include <wchar.h>


bool approxEquals (double a, double b, double tolerance){
    
    if (std::abs(a + b) < tolerance){
        
        return true;
    }
    
    return false;
    
}

//void characterAnalyzer (char c){
//    
//    int asciiCount = 0;
//    
//    int nonAscii = 0;
//    
//    if (sizeof(c) <= 127){
//        asciiCount++;
//    } else {
//        nonAscii++;
//    }
//    std::cout << asciiCount << "\n" << nonAscii << "\n";
//}

int main(int argc, const char * argv[]) {

    
    // Part 1
    
//    uint8_t uMin8 = 0x80;
//    std::cout << +uMin8 << std::endl; // 128
//    uint8_t uMax8 = 0xFF;
//    std::cout << +uMax8 << std::endl; // 255
//    uint16_t uMin16 = 0x8000;
//    std::cout << +uMin16 << std::endl; // 32768
//    uint16_t uMax16 = 0xFFFF;
//    std::cout << +uMax16 << std::endl; // 65535
//    uint64_t uMin64 = 0x8000000000000000;
//    std::cout << +uMin64 << std::endl; // 9223372036854775808
//    uint64_t uMax64 = 0xFFFFFFFFFFFFFFFF;
//    std::cout << +uMax64 << std::endl; // 18446744073709551615
    
//    int8_t sMin8 = 0x80;
//    std::cout << +sMin8 << std::endl; // -128
//    int8_t sMax8 = 0x7F;
//    std::cout << +sMax8 << std::endl; // 127
//    int16_t sMin16 = 0x8000;
//    std::cout << +sMin16 << std::endl; // -32768
//    int16_t sMax16 = 0x7FFF;
//    std::cout << +sMax16 << std::endl; // 32767
//    int64_t sMin64 = 0x8000000000000000;
//    std::cout << +sMin64 << std::endl; // -9223372036854775808
//    int64_t sMax64 = 0x7FFFFFFFFFFFFFFF;
//    std::cout << +sMax64 << std::endl; // 9223372036854775807
    
//  Try adding 1 to the max-value variables you defined above and printing it. What happened?
//  adding 1 to the max values of the signed integers makes it equal the min value of the same bit size
    
    
//    Try subtracting 1 from the min values. What happened?
//    subracting 1 from the min values of the signed integers makes it equal the max value of the same bit size
    
//    Try doing the same thing with the the "undefined behavior sanitizer" turned on (it's a couple checkboxes below the address sanitizer in the product->scheme menu). What happened?
//  NumberRepresentations(40327,0x1e1831c40) malloc: nano zone abandoned due to inability to reserve vm space.
    
    
    // Part 2
    
//    int x = 0.1, y = 0.2;
//    
//    double sum = x + y;
//    
//    std::cout << sum << std::endl;
//        
//    assert(sum = 0.3);
//    
//    std::cout << std::setprecision(18);
//    
//    std::cout << sum << std::endl; // 0.299999999999999989
//    
//    std::cout << approxEquals(x, y, sum) << std::endl; // 1 or true
    
    
    // Part 3

    std::ifstream ins{ "UTF-8-demo.txt" };
    
    if (!ins) {
        std::cerr << "File not found. \n";
        return 1;
    }
    
    char c;
    
    int asciiCount = 0;
    
    int nonAscii = 0;

    while( ins >> c ){

        std::cout << c << "\n";
    
        if (isprint(c)){
            asciiCount++;
        } else {
            nonAscii++;
        }
    }
    
    std::cout << asciiCount << "\n" << nonAscii << "\n" << "\u0342" << "\n";
    
//    What do you see? What text is printed in a readble form? What text is garbled? Does this match your expectations? Why or why not?
//    lots of \xyz . unicode will only print if it is \u0xyz as tested above. Not sure why it is not converting it to the correct unicode representation.
    
    return 0;
}
