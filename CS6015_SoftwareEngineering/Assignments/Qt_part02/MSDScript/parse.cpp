//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "parse.hpp"
#include "expr.h"
#include <iostream>
#include <sstream>
#include <cctype>
#include <climits>
#include <cstdint>

void consume(std::istream& in, int expect) {
    int c = in.get();  // Read the next character from the input stream
    if (c != expect) { // Check if the character matches the expected one
        throw std::runtime_error("bad input"); // Throw an error if it doesn't match
    }
}

void skip_whitespace(std::istream& in) {
    while (true) {
        int c = in.peek(); // Peek at the next character without consuming it
        if (!isspace(c)) break; // Exit the loop if the character is not whitespace
        consume(in, c); // Consume the whitespace character
    }
}

PTR(Expr) parse_num(std::istream& in) {
    uint64_t n = 0; // Use uint64_t to handle large numbers
    bool negative = false;

    if (in.peek() == '-') {
        negative = true;
        consume(in, '-');
        if (!isdigit(in.peek())) {
            throw std::runtime_error("invalid input");
        }
    }

    while (true) {
        int c = in.peek();
        if (isdigit(c)) {
            consume(in, c);
//            uint64_t prev = n;
            n = n * 10 + (c - '0');

            // Check for overflow
            if (n > static_cast<uint64_t>(INT_MAX)) {
                throw std::runtime_error("number too large");
            }
        } else {
            break;
        }
    }

    if (negative) {
        n = -n; // Cast to signed value
    }

    return NEW(NumExpr)(static_cast<int>(n)); // Cast back to int for NumExpr
}

PTR(Expr) parse_var(std::istream& in) {
    std::string name; // Initialize an empty string to store the variable name
    while (true) {
        int c = in.peek(); // Peek at the next character
        if (isalpha(c)) {  // If it's an alphabetic character, consume it and add to the name
            consume(in, c);
            name += static_cast<char>(c);
        } else if (c == '_') {
            // If an underscore is encountered, throw an exception
            throw std::runtime_error("invalid input");
        } else {
            break; // Exit the loop if a non-alphabetic character is encountered
        }
    }

    // Ensure the variable name is not empty
    if (name.empty()) {
        throw std::runtime_error("invalid input");
    }

    // Return a NEW( VarExpr object representing the parsed variable
    return NEW(VarExpr)(name);
}

PTR(Expr) parse_let(std::istream& in) {
    skip_whitespace(in);
    consume(in, '_'); // Consume '_'
    consume(in, 'l'); // Consume 'l'
    consume(in, 'e'); // Consume 'e'
    consume(in, 't'); // Consume 't'
    skip_whitespace(in);

    // Parse the variable name
    std::string var;
    while (isalpha(in.peek())) {
        var += in.get();
    }
    skip_whitespace(in);

    // Parse the '='
    consume(in, '=');
    skip_whitespace(in);

    // Parse the right-hand side expression
    PTR(Expr) rhs = parse_expr(in);
    skip_whitespace(in);

    // Parse the '_in' keyword
    consume(in, '_');
    consume(in, 'i');
    consume(in, 'n');
    skip_whitespace(in);

    // Parse the body expression
    PTR(Expr) body = parse_expr(in);

    // Return a NEW( LetExpr object
    return NEW(LetExpr)(var, rhs, body);
}

PTR(Expr) parse_multicand(std::istream& in) {
    PTR(Expr) e = parse_inner(in);

    while (true) {
        skip_whitespace(in);
        int c = in.peek();
        if (c == '(') {
            consume(in, '(');
            PTR(Expr) actual_arg = parse_expr(in);
            skip_whitespace(in);
            consume(in, ')');
            e = NEW(CallExpr)(e, actual_arg);
        }
        else {
            break;
        }
    }

    return e;
}

PTR(Expr) parse_addend(std::istream& in) {
    PTR(Expr) lhs = parse_multicand(in);
    skip_whitespace(in);

    if (in.peek() == '*') {
        consume(in, '*');
        PTR(Expr) rhs = parse_addend(in);
        return NEW(MultExpr)(lhs, rhs);
    }

    return lhs;
}

PTR(Expr) parse_expr(std::istream& in) {
    PTR(Expr) lhs = parse_addend(in);
    skip_whitespace(in);

    int c = in.peek();
    if (c == '+') {
        consume(in, '+');
        PTR(Expr) rhs = parse_expr(in);
        return NEW(AddExpr)(lhs, rhs);
    }
    else if (c == '=' && in.peek() == '=') {
        consume(in, '=');
        consume(in, '=');
        PTR(Expr) rhs = parse_expr(in);
        return NEW(EqExpr)(lhs, rhs);
    }

    return lhs;
}

PTR(Expr) parse_bool(std::istream& in) {
    consume(in, '_'); // Consume '_'
    if (in.peek() == 't') {
        consume(in, 't');
        consume(in, 'r');
        consume(in, 'u');
        consume(in, 'e');
        return NEW(BoolExpr)(true);
    } else if (in.peek() == 'f') {
        consume(in, 'f');
        consume(in, 'a');
        consume(in, 'l');
        consume(in, 's');
        consume(in, 'e');
        return NEW(BoolExpr)(false);
    } else {
        throw std::runtime_error("bad input");
    }
}

PTR(Expr) parse_eq(std::istream& in) {
    PTR(Expr) lhs = parse_expr(in); // Parse the left-hand side of the equality
    skip_whitespace(in);
    if (in.peek() == '=') {
        consume(in, '='); // Consume the first '='
        consume(in, '='); // Consume the second '='
        PTR(Expr) rhs = parse_expr(in); // Parse the right-hand side of the equality
        return NEW(EqExpr)(lhs, rhs); // Return a NEW( EqExpr object
    }
    return lhs; // If no equality operator, return the left-hand side
}

PTR(Expr) parse_if(std::istream& in) {
    skip_whitespace(in); // Skip any leading whitespace

    // Parse the condition
    PTR(Expr) condition = parse_expr(in);
    skip_whitespace(in);

    // Parse the _then keyword
    consume(in, '_');
    consume(in, 't');
    consume(in, 'h');
    consume(in, 'e');
    consume(in, 'n');
    skip_whitespace(in);

    // Parse the then branch
    PTR(Expr) then_branch = parse_expr(in);
    skip_whitespace(in);

    // Parse the _else keyword
    consume(in, '_');
    consume(in, 'e');
    consume(in, 'l');
    consume(in, 's');
    consume(in, 'e');
    skip_whitespace(in);

    // Parse the else branch
    PTR(Expr) else_branch = parse_expr(in);

    // Return a NEW( IfExpr object
    return NEW(IfExpr)(condition, then_branch, else_branch);
}

PTR(Expr) parse(std::istream& in) {
    return parse_expr(in); // Delegate to parse_expr
}

PTR(Expr) parse_str(const std::string& s) {
    std::stringstream ss(s);
    return parse_expr(ss);
}

PTR(Expr) parse_fun(std::istream& in) {
    skip_whitespace(in);

    // Parse formal argument
    consume(in, '(');
    skip_whitespace(in);

    std::string formal_arg;
    while (isalpha(in.peek())) {
        formal_arg += in.get();
    }

    skip_whitespace(in);
    consume(in, ')');
    skip_whitespace(in);

    // Parse function body
    PTR(Expr) body = parse_expr(in);

    return NEW(FunExpr)(formal_arg, body);
}

PTR(Expr) parse_inner(std::istream& in) {
    skip_whitespace(in);
    int c = in.peek();

    // Handle NEW(lines in pretty-printed input
    while (c == '\n') {
        consume(in, '\n');
        skip_whitespace(in);
        c = in.peek();
    }

    if ((c == '-') || isdigit(c)) {
        return parse_num(in);
    }
    else if (c == '(') {
        consume(in, '(');
        PTR(Expr) e = parse_expr(in);
        skip_whitespace(in);
        consume(in, ')');
        return e;
    }
    else if (isalpha(c)) {
        return parse_var(in);
    }
    else if (c == '_') {
        consume(in, '_');
        if (in.peek() == 'f') {
            // Could be _fun or _false
            consume(in, 'f');
            if (in.peek() == 'u') {
                consume(in, 'u');
                consume(in, 'n');
                skip_whitespace(in);
                return parse_fun(in);
            }
            else if (in.peek() == 'a') {
                // Handle _false
                consume(in, 'a');
                consume(in, 'l');
                consume(in, 's');
                consume(in, 'e');
                skip_whitespace(in);
                return NEW(BoolExpr)(false);
            }
            else {
                throw std::runtime_error("bad input");
            }
        }
        else if (in.peek() == 't') {
            // Handle _true
            consume(in, 't');
            consume(in, 'r');
            consume(in, 'u');
            consume(in, 'e');
            skip_whitespace(in);
            return NEW(BoolExpr)(true);
        }
        else if (in.peek() == 'i') {
            // Handle _if
            consume(in, 'i');
            consume(in, 'f');
            return parse_if(in);
        }
        else if (in.peek() == 'l') {
            // Handle _let
            consume(in, 'l');
            consume(in, 'e');
            consume(in, 't');
            skip_whitespace(in);
            return parse_let(in);
        }
        else {
            throw std::runtime_error("bad input");
        }
    }
    else {
        throw std::runtime_error("bad input");
    }
}
