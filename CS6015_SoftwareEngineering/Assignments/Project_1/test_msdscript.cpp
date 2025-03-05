//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "exec.h"
#include <iostream>
#include <cstdlib>
#include <ctime>
#include <vector>
#include <string>

// Function to generate a random expression
std::string generate_random_expression(int depth = 0) {
    if (depth > 3) {
        // Base case: return a number or variable
        if (rand() % 2 == 0) {
            return std::to_string(rand() % 100); // Random number
        } else {
            return "x"; // Variable
        }
    }

    // Recursive case: generate a more complex expression
    switch (rand() % 4) {
        case 0:
            return generate_random_expression(depth + 1) + " + " + generate_random_expression(depth + 1);
        case 1:
            return generate_random_expression(depth + 1) + " * " + generate_random_expression(depth + 1);
        case 2:
            return "(" + generate_random_expression(depth + 1) + ")";
        case 3:
            return "_let x = " + generate_random_expression(depth + 1) + " _in " + generate_random_expression(depth + 1);
        default:
            return generate_random_expression(depth + 1);
    }
}

// Function to test a single msdscript executable
void test_single(const std::string& msdscript_path) {
    srand(time(nullptr)); // Seed the random number generator

    for (int i = 0; i < 100; ++i) {
        std::string expr = generate_random_expression();
        std::cout << "Testing expression: " << expr << "\n";

        // Test --interp
        const char* command[] = {msdscript_path.c_str(), "--interp", nullptr};
        ExecResult interp_result = exec_program(2, command, expr);
        if (interp_result.exit_code != 0) {
            std::cerr << "Error in --interp mode:\n" << interp_result.err << "\n";
            exit(1);
        }

        // Test --print
        const char* print_command[] = {msdscript_path.c_str(), "--print", nullptr};
        ExecResult print_result = exec_program(2, print_command, expr);
        if (print_result.exit_code != 0) {
            std::cerr << "Error in --print mode:\n" << print_result.err << "\n";
            exit(1);
        }

        // Test --pretty-print
        const char* pretty_print_command[] = {msdscript_path.c_str(), "--pretty-print", nullptr};
        ExecResult pretty_print_result = exec_program(2, pretty_print_command, expr);
        if (pretty_print_result.exit_code != 0) {
            std::cerr << "Error in --pretty-print mode:\n" << pretty_print_result.err << "\n";
            exit(1);
        }

        std::cout << "All modes passed for expression: " << expr << "\n";
    }
}

// Function to compare two msdscript executables
void test_compare(const std::string& msdscript1_path, const std::string& msdscript2_path) {
    srand(time(nullptr)); // Seed the random number generator

    for (int i = 0; i < 100; ++i) {
        std::string expr = generate_random_expression();
        std::cout << "Comparing expression: " << expr << "\n";

        // Test --interp
        const char* command1[] = {msdscript1_path.c_str(), "--interp", nullptr};
        const char* command2[] = {msdscript2_path.c_str(), "--interp", nullptr};
        ExecResult result1 = exec_program(2, command1, expr);
        ExecResult result2 = exec_program(2, command2, expr);

        if (result1.out != result2.out) {
            std::cerr << "Discrepancy in --interp mode:\n"
                      << msdscript1_path << " output: " << result1.out << "\n"
                      << msdscript2_path << " output: " << result2.out << "\n";
            exit(1);
        }

        // Test --print
        const char* print_command1[] = {msdscript1_path.c_str(), "--print", nullptr};
        const char* print_command2[] = {msdscript2_path.c_str(), "--print", nullptr};
        result1 = exec_program(2, print_command1, expr);
        result2 = exec_program(2, print_command2, expr);

        if (result1.out != result2.out) {
            std::cerr << "Discrepancy in --print mode:\n"
                      << msdscript1_path << " output: " << result1.out << "\n"
                      << msdscript2_path << " output: " << result2.out << "\n";
            exit(1);
        }

        // Test --pretty-print
        const char* pretty_print_command1[] = {msdscript1_path.c_str(), "--pretty-print", nullptr};
        const char* pretty_print_command2[] = {msdscript2_path.c_str(), "--pretty-print", nullptr};
        result1 = exec_program(2, pretty_print_command1, expr);
        result2 = exec_program(2, pretty_print_command2, expr);

        if (result1.out != result2.out) {
            std::cerr << "Discrepancy in --pretty-print mode:\n"
                      << msdscript1_path << " output: " << result1.out << "\n"
                      << msdscript2_path << " output: " << result2.out << "\n";
            exit(1);
        }

        std::cout << "No discrepancies found for expression: " << expr << "\n";
    }
}

int main(int argc, char* argv[]) {
    if (argc == 2) {
        // Single argument mode: test the given msdscript executable
        test_single(argv[1]);
    } else if (argc == 3) {
        // Two argument mode: compare two msdscript executables
        test_compare(argv[1], argv[2]);
    } else {
        std::cerr << "Usage: " << argv[0] << " <msdscript_path> [<msdscript2_path>]\n";
        return 1;
    }

    return 0;
}