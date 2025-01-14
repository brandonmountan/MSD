//
// Created by Brandon Mountan on 1/13/25.
//
#include <iostream>
#include <string>
#include "cmdline.h"

void use_arguments(int argc, char* argv[]) {
    bool test_seen = false;

    for (int i = 1; i < argc; ++i) {  // Start from 1 since argv[0] is the program name
        std::string arg = argv[i];

        if (arg == "--help") {
            std::cout << "Usage: ./msdscript [options]\n"
                         "Options:\n"
                         "  --help       Display this help message\n"
                         "  --test       Run tests\n";
            exit(0);
        } else if (arg == "--test") {
            if (test_seen) {
                std::cerr << "Error: --test argument repeated\n";
                exit(1);
            }
            test_seen = true;
            std::cout << "Tests passed\n";
        } else {
            std::cerr << "Error: Unknown argument: " << arg << "\n";
            exit(1);
        }
    }
}
