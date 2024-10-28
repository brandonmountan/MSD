//
//  main.cpp
//  NumberRepresentations
//
//  Created by Brandon Mountan on 9/4/24.
//
//
//#include <iostream>
//#include <cstdint>
//#include <cassert>
//#include <iomanip>
//#include <cmath>
//#include <fstream>
//#include <string>
//#include <vector>
//#include <wchar.h>
//
//bool approxEquals(double a, double b, double tolerance) {
//    return std::abs(a - b) < tolerance;
//}
//
//
//int main() {
//    std::cout << "Size of char: " << sizeof(char) << " byte(s)\n";
//    std::cout << "Size of int: " << sizeof(int) << " byte(s)\n";
//    std::cout << "Size of float: " << sizeof(float) << " byte(s)\n";
//    std::cout << "Size of double: " << sizeof(double) << " byte(s)\n";
//    std::cout << "Size of long: " << sizeof(long) << " byte(s)\n";
//    std::cout << "Size of long long: " << sizeof(long long) << " byte(s)\n";
//
//    // Print sizes of fixed-width integer types
//    std::cout << "Size of uint8_t: " << sizeof(uint8_t) << " byte(s)\n";
//    std::cout << "Size of uint16_t: " << sizeof(uint16_t) << " byte(s)\n";
//    std::cout << "Size of uint64_t: " << sizeof(uint64_t) << " byte(s)\n";
//    
//    uint8_t u8_min = 0x00;
//    uint8_t u8_max = 0xFF;
//    uint16_t u16_min = 0x0000;
//    uint16_t u16_max = 0xFFFF;
//    uint64_t u64_min = 0x0000000000000000;
//    uint64_t u64_max = 0xFFFFFFFFFFFFFFFF;
//
//    std::cout << "uint8_t min: " << +u8_min << ", max: " << +u8_max << "\n";
//    std::cout << "uint16_t min: " << u16_min << ", max: " << u16_max << "\n";
//    std::cout << "uint64_t min: " << u64_min << ", max: " << u64_max << "\n";
//    
//    int8_t s8_min = 0x80;  // -128
//    int8_t s8_max = 0x7F;  // 127
//    int16_t s16_min = 0x8000; // -32768
//    int16_t s16_max = 0x7FFF; // 32767
//    int64_t s64_min = 0x8000000000000000; // -9223372036854775808
//    int64_t s64_max = 0x7FFFFFFFFFFFFFFF; // 9223372036854775807
//
//    std::cout << "int8_t min: " << (int)s8_min << ", max: " << (int)s8_max << "\n";
//    std::cout << "int16_t min: " << s16_min << ", max: " << s16_max << "\n";
//    std::cout << "int64_t min: " << s64_min << ", max: " << s64_max << "\n";
//    
//    std::cout << "uint8_t max + 1: " << +u8_max + 1 << "\n"; // Check for overflow
//    std::cout << "uint8_t min - 1: " << +u8_min - 1 << "\n"; // Check for underflow
//
//    // part 2
//    
//    double result = 0.1 + 0.2;
//    std::cout << "Result of 0.1 + 0.2: " << result << "\n";
//
//    std::cout << std::setprecision(18) << result << " vs 0.3\n";
//
//    std::cout << "Approx equals: " << approxEquals(result, 0.3, 0.00001) << "\n";
//
//    
//    
//    
//    return 0;
//}


// part 3

//void loadFile(const std::string& filename) {
//    std::ifstream ins(filename, std::ios::binary);
//    char c;
//    int asciiCount = 0;
//    int unicodeCount = 0;
//
//    while (ins.get(c)) {
//        std::cout << c << '\n'; // Print each character
//        if (static_cast<unsigned char>(c) <= 127) {
//            asciiCount++;
//        } else {
//            unicodeCount++;
//        }
//    }
//    std::cout << "ASCII count: " << asciiCount << "\n";
//    std::cout << "Unicode count: " << unicodeCount << "\n";
//}
//
//int main() {
//    loadFile("yourfile.txt");
//    return 0;
//}
