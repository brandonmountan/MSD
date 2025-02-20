//
// Created by Brandon Mountan on 1/13/25.
//
#include <iostream>
#include <string>
#include <cstdlib> // For exit()

void use_arguments(int argc, char* argv[]) {
    bool test_seen = false;

    for (int i = 1; i < argc; ++i) {
        std::string arg = argv[i];

        if (arg == "--help") {
            std::cout << "Usage:\n"
                      << "  ./msdscript --help       Show this help message\n"
                      << "  ./msdscript --test       Run the tests\n"
                      << "  [other arguments]        Not allowed\n";
            exit(0);
        } else if (arg == "--test") {
            if (test_seen) {
                std::cerr << "Error: '--test' argument provided more than once.\n";
                exit(1);
            }
            std::cout << "Tests passed\n";
            test_seen = true;
        } else {
            std::cerr << "Error: Unknown argument '" << arg << "'.\n";
            exit(1);
        }
    }
}
