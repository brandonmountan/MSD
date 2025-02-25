#define CATCH_CONFIG_RUNNER
#include "catch.h"
#include "expr.h"
#include "parse.hpp"
#include "cmdline.h"
#include <iostream>
#include <sstream>

int main(int argc, char* argv[]) {
    try {
        run_mode_t mode = use_arguments(argc, argv);

        if (mode == do_test) {
            // Run tests
            int result = Catch::Session().run();
            return result;
        }

        // Read input from standard input
        std::string input;
        std::getline(std::cin, input);
        std::stringstream ss(input);

        // Parse the input expression
        Expr* expr = parse_expr(ss);

        // Handle the flag
        switch (mode) {
            case do_interp: {
                int result = expr->interp();
                std::cout << result << "\n";
                break;
            }
            case do_print: {
                std::cout << expr->to_string() << "\n";
                break;
            }
            case do_pretty_print: {
                std::cout << expr->to_pretty_string() << "\n";
                break;
            }
            default:
                std::cerr << "Invalid mode.\n";
            delete expr;
            return 1;
        }

        // Clean up
        delete expr;
        return 0;
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << "\n";
        return 1;
    }
}