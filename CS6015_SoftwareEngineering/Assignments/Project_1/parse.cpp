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

Expr* parse_num(std::istream& in) {
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
            uint64_t prev = n;
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

    return new NumExpr(static_cast<int>(n)); // Cast back to int for NumExpr
}

Expr* parse_var(std::istream& in) {
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

    // Return a new VarExpr object representing the parsed variable
    return new VarExpr(name);
}

Expr* parse_let(std::istream& in) {
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
    Expr* rhs = parse_expr(in);
    skip_whitespace(in);

    // Parse the '_in' keyword
    consume(in, '_');
    consume(in, 'i');
    consume(in, 'n');
    skip_whitespace(in);

    // Parse the body expression
    Expr* body = parse_expr(in);

    // Return a new LetExpr object
    return new LetExpr(var, rhs, body);
}

Expr* parse_multicand(std::istream& in) {
    skip_whitespace(in); // Skip any leading whitespace
    int c = in.peek();   // Peek at the next character

    // Handle numbers (including negative numbers)
    if ((c == '-') || isdigit(c)) {
        return parse_num(in);
    }
    // Handle parenthesized expressions
    else if (c == '(') {
        consume(in, '('); // Consume the '(' character
        Expr* e = parse_expr(in); // Parse the inner expression
        skip_whitespace(in); // Skip any trailing whitespace
        consume(in, ')'); // Consume the ')' character
        return e;
    }
    // Handle variables
    else if (isalpha(c)) {
        return parse_var(in);
    }
    // Handle boolean values and if expressions
    else if (c == '_') {
        consume(in, '_'); // Consume the '_'
        if (in.peek() == 't') {
            // Parse _true
            consume(in, 't');
            consume(in, 'r');
            consume(in, 'u');
            consume(in, 'e');
            return new BoolExpr(true);
        } else if (in.peek() == 'f') {
            // Parse _false
            consume(in, 'f');
            consume(in, 'a');
            consume(in, 'l');
            consume(in, 's');
            consume(in, 'e');
            return new BoolExpr(false);
        } else if (in.peek() == 'i') {
            // Parse _if
            consume(in, 'i');
            consume(in, 'f');
            return parse_if(in);
        } else {
            throw std::runtime_error("bad input"); // Invalid input
        }
    }
    // Handle invalid input
    else {
        throw std::runtime_error("bad input");
    }
}

Expr* parse_addend(std::istream& in) {
    Expr* lhs = parse_multicand(in); // Parse the left-hand side of the multiplication
    skip_whitespace(in); // Skip any whitespace
    int c = in.peek();   // Peek at the next character

    // Handle multiplication
    if (c == '*') {
        consume(in, '*'); // Consume the '*' character
        Expr* rhs = parse_addend(in); // Parse the right-hand side of the multiplication
        return new MultExpr(lhs, rhs); // Return a new MultExpr object
    }

    // If no multiplication, return the left-hand side
    return lhs;
}

Expr* parse_expr(std::istream& in) {
    Expr* lhs = parse_addend(in); // Parse the left-hand side of the addition
    skip_whitespace(in); // Skip any whitespace
    int c = in.peek();   // Peek at the next character

    // Handle addition
    if (c == '+') {
        consume(in, '+'); // Consume the '+' character
        Expr* rhs = parse_expr(in); // Parse the right-hand side of the addition
        return new AddExpr(lhs, rhs); // Return a new AddExpr object
    }
    // Handle equality
    else if (c == '=') {
        consume(in, '='); // Consume the first '='
        consume(in, '='); // Consume the second '='
        Expr* rhs = parse_expr(in); // Parse the right-hand side of the equality
        return new EqExpr(lhs, rhs); // Return a new EqExpr object
    }

    // If no addition or equality, return the left-hand side
    return lhs;
}

Expr* parse_bool(std::istream& in) {
    consume(in, '_'); // Consume '_'
    if (in.peek() == 't') {
        consume(in, 't');
        consume(in, 'r');
        consume(in, 'u');
        consume(in, 'e');
        return new BoolExpr(true);
    } else if (in.peek() == 'f') {
        consume(in, 'f');
        consume(in, 'a');
        consume(in, 'l');
        consume(in, 's');
        consume(in, 'e');
        return new BoolExpr(false);
    } else {
        throw std::runtime_error("bad input");
    }
}

Expr* parse_eq(std::istream& in) {
    Expr* lhs = parse_expr(in); // Parse the left-hand side of the equality
    skip_whitespace(in);
    if (in.peek() == '=') {
        consume(in, '='); // Consume the first '='
        consume(in, '='); // Consume the second '='
        Expr* rhs = parse_expr(in); // Parse the right-hand side of the equality
        return new EqExpr(lhs, rhs); // Return a new EqExpr object
    }
    return lhs; // If no equality operator, return the left-hand side
}

Expr* parse_if(std::istream& in) {
    skip_whitespace(in); // Skip any leading whitespace

    // Parse the condition
    Expr* condition = parse_expr(in);
    skip_whitespace(in);

    // Parse the _then keyword
    consume(in, '_');
    consume(in, 't');
    consume(in, 'h');
    consume(in, 'e');
    consume(in, 'n');
    skip_whitespace(in);

    // Parse the then branch
    Expr* then_branch = parse_expr(in);
    skip_whitespace(in);

    // Parse the _else keyword
    consume(in, '_');
    consume(in, 'e');
    consume(in, 'l');
    consume(in, 's');
    consume(in, 'e');
    skip_whitespace(in);

    // Parse the else branch
    Expr* else_branch = parse_expr(in);

    // Return a new IfExpr object
    return new IfExpr(condition, then_branch, else_branch);
}

Expr* parse(std::istream& in) {
    return parse_expr(in); // Delegate to parse_expr
}

Expr* parse_str(const std::string& s) {
    std::stringstream ss(s); // Create a string stream from the input string
    return parse(ss); // Parse the expression from the string stream
}