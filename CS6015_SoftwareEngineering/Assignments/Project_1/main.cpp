//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/13/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////
// Define the CATCH_CONFIG_RUNNER macro to enable the Catch2 test framework.
// This allows the program to run tests when the --test flag is provided.
#define CATCH_CONFIG_RUNNER

#include "catch.h"       // Catch2 testing framework
#include "expr.h"        // Expression classes (e.g., Expr, NumExpr, AddExpr)
#include "parse.hpp"     // Parser functions (e.g., parse_expr)
#include "cmdline.h"     // Command-line argument handling (e.g., use_arguments)
#include <iostream>      // Standard input/output (e.g., std::cin, std::cout)
#include <sstream>       // String stream (e.g., std::stringstream)
#include "val.h"
#include "pointer.h"

// Main function
int main(int argc, char* argv[]) {
    try {
        // Parse command-line arguments and determine the run mode
        run_mode_t mode = use_arguments(argc, argv);

        // If the mode is do_test, run the Catch2 test suite
        if (mode == do_test) {
            // Create a Catch2 session and run the tests
            int result = Catch::Session().run();
            // Return the test result (0 for success, non-zero for failures)
            return result;
        }

        // Read input from standard input
        std::string input;
        std::getline(std::cin, input);

        // Create a string stream from the input for parsing
        std::stringstream ss(input);

        // Parse the input expression into an Expr object
        PTR(Expr) expr = parse_expr(ss);

        // Handle the flag based on the run mode
        switch (mode) {
            case do_interp: {
                // If the mode is do_interp, interpret the expression and print the result
                PTR(Val) result = expr->interp();
                std::cout << result->to_string() << "\n";
                break;
            }
            case do_print: {
                // If the mode is do_print, print the expression as a string
                std::cout << expr->to_string() << "\n";
                break;
            }
            case do_pretty_print: {
                // If the mode is do_pretty_print, pretty-print the expression
                std::cout << expr->to_pretty_string() << "\n";
                break;
            }
            default:
                // If the mode is invalid, print an error message
                std::cerr << "Invalid mode.\n";
                // Exit with a non-zero status code to indicate an error
                return 1;
        }

        // Exit with a status code of 0 to indicate success
        return 0;
    } catch (const std::exception& e) {
        // Catch any exceptions that occur during execution
        // Print the error message to standard error
        std::cerr << "Error: " << e.what() << "\n";
        // Exit with a non-zero status code to indicate an error
        return 1;
    }
}