//
// Created by Brandon Mountan on 1/13/25.
//
#include "cmdline.h"
#include <iostream>
#include <cstdlib> // For exit()

run_mode_t use_arguments(int argc, char **argv) {
    if (argc != 2) {
        std::cerr << "Usage: msdscript [--test | --interp | --print | --pretty-print]\n";
        exit(1);
    }

    std::string flag = argv[1];

    if (flag == "--test") {
        return do_test;
    } else if (flag == "--interp") {
        return do_interp;
    } else if (flag == "--print") {
        return do_print;
    } else if (flag == "--pretty-print") {
        return do_pretty_print;
    } else {
        std::cerr << "Invalid flag. Use --test, --interp, --print, or --pretty-print\n";
        exit(1);
    }
}