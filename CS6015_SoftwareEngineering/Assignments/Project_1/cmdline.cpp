//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/13/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "cmdline.h"
#include <iostream>
#include <cstdlib> // For exit()

run_mode_t use_arguments(int argc, char **argv) {
    // Check if the number of arguments is not equal to 2.
    // The program expects exactly 2 arguments: the program name and a flag.
    if (argc != 2) {
        // Print an error message to standard error if the number of arguments is incorrect.
        std::cerr << "Usage: msdscript [--test | --interp | --print | --pretty-print]\n";
        // Exit the program with a non-zero status code (1) to indicate an error.
        exit(1);
    }

    // Extract the second argument (the flag) from the argv array.
    // argv[0] is the program name, and argv[1] is the flag.
    std::string flag = argv[1];

    // Check the value of the flag and return the corresponding run mode.
    if (flag == "--test") {
        return do_test; // If the flag is "--test", return do_test to indicate test mode.
    } else if (flag == "--interp") {
        return do_interp; // If the flag is "--interp", return do_interp to indicate interpretation mode.
    } else if (flag == "--print") {
        return do_print; // If the flag is "--print", return do_print to indicate print mode.
    } else if (flag == "--pretty-print") {
        return do_pretty_print; // If the flag is "--pretty-print", return do_pretty_print to indicate pretty-print mode.
    } else {
        // If the flag is not recognized, print an error message to standard error.
        std::cerr << "Invalid flag. Use --test, --interp, --print, or --pretty-print\n";
        // Exit the program with a non-zero status code (1) to indicate an error.
        exit(1);
    }
}