//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////


#include <iostream>
#include <string>
#include "cmdline.h"
#include "expr.h"
#include "catch.h"

void use_arguments(int argc, char** argv) {
    bool test_seen = false;

    for (int i = 1; i < argc; ++i) {
        std::string arg = argv[i];

        if (arg == "--help") {
            std::cout << "Usage: msdscript [--help] [--test]\n"
                      << "  --help: Display this help message\n"
                      << "  --test: Run tests\n";
            exit(0);
        } else if (arg == "--test") {
            if (test_seen) {
                std::cerr << "Error: --test argument seen more than once\n";
                exit(1);
            }
            test_seen = true;

            // Run Catch2 tests
            int result = Catch::Session().run();
            if (result != 0) {
                exit(1);
            }
        } else {
            std::cerr << "Error: Unknown argument '" << arg << "'\n";
            exit(1);
        }
    }
}