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
#include "expr.h"
#include "val.h"
#include "pointer.h"

typedef enum {
    do_nothing,
    do_test,
    do_interp,
    do_print,
    do_pretty_print
} run_mode_t;

/**
 * @brief Parses command-line arguments and returns the corresponding run mode.
 *
 * This function checks the command-line arguments and determines the mode of operation
 * based on the provided flag. If the arguments are invalid, it prints an error message
 * and exits the program with a non-zero status code.
 *
 * @param argc The number of command-line arguments.
 * @param argv An array of C-style strings representing the command-line arguments.
 * @return A run_mode_t value indicating the mode of operation (e.g., do_test, do_interp, etc.).
 *
 * @throws std::runtime_error If the number of arguments is incorrect or the flag is invalid.
 */
run_mode_t use_arguments(int argc, char **argv);

#endif // CMDLINE_H