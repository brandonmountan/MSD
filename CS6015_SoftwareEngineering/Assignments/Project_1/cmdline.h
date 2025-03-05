//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/13/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#ifndef CMDLINE_H
#define CMDLINE_H

#include <stdexcept>
#include <string>

typedef enum {
    do_nothing,
    do_test,
    do_interp,
    do_print,
    do_pretty_print
} run_mode_t;

run_mode_t use_arguments(int argc, char **argv);

#endif // CMDLINE_H