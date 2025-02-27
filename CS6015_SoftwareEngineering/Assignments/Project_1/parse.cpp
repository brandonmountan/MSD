// Include necessary headers
#include "parse.hpp"  // Header for parsing functions
#include <iostream>   // Standard input/output (e.g., std::cin, std::cout)
#include <sstream>    // String stream (e.g., std::stringstream)
#include <cctype>     // Character handling functions (e.g., isdigit, isalpha)

// Function: consume
// Purpose: Consumes a specific character from the input stream.
// Parameters:
//   - in: The input stream.
//   - expect: The expected character to consume.
// Throws: std::runtime_error if the next character does not match the expected character.
static void consume(std::istream& in, int expect) {
    int c = in.get();  // Read the next character from the input stream
    if (c != expect) { // Check if the character matches the expected one
        throw std::runtime_error("bad input"); // Throw an error if it doesn't match
    }
}

// Function: skip_whitespace
// Purpose: Skips whitespace characters in the input stream.
// Parameters:
//   - in: The input stream.
void skip_whitespace(std::istream& in) {
    while (true) {
        int c = in.peek(); // Peek at the next character without consuming it
        if (!isspace(c)) break; // Exit the loop if the character is not whitespace
        consume(in, c); // Consume the whitespace character
    }
}

// Function: parse_num
// Purpose: Parses a number (integer) from the input stream.
// Parameters:
//   - in: The input stream.
// Returns: A pointer to a NumExpr object representing the parsed number.
// Throws: std::runtime_error if the input is invalid (e.g., no digit after '-').
Expr* parse_num(std::istream& in) {
    int n = 0;          // Initialize the number to 0
    bool negative = false; // Flag to indicate if the number is negative

    // Check if the number is negative
    if (in.peek() == '-') {
        negative = true; // Set the negative flag
        consume(in, '-'); // Consume the '-' character

        // Ensure there is a digit after the '-'
        if (!isdigit(in.peek())) {
            throw std::runtime_error("invalid input"); // Throw an error if no digit follows
        }
    }

    // Parse the digits of the number
    while (true) {
        int c = in.peek(); // Peek at the next character
        if (isdigit(c)) {  // If it's a digit, consume it and add to the number
            consume(in, c);
            n = n * 10 + (c - '0'); // Convert the character to a digit and add to the number
        } else {
            break; // Exit the loop if a non-digit character is encountered
        }
    }

    // Apply the negative sign if necessary
    if (negative) {
        n = -n;
    }

    // Return a new NumExpr object representing the parsed number
    return new NumExpr(n);
}

// Function: parse_var
// Purpose: Parses a variable name from the input stream.
// Parameters:
//   - in: The input stream.
// Returns: A pointer to a VarExpr object representing the parsed variable.
// Throws: std::runtime_error if the variable name contains invalid characters (e.g., '_').
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

// Function: parse_multicand
// Purpose: Parses a multicand (number, variable, or parenthesized expression).
// Parameters:
//   - in: The input stream.
// Returns: A pointer to an Expr object representing the parsed multicand.
// Throws: std::runtime_error if the input is invalid.
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
    // Handle invalid input
    else {
        throw std::runtime_error("bad input");
    }
}

// Function: parse_addend
// Purpose: Parses an addend (multicand or multiplication expression).
// Parameters:
//   - in: The input stream.
// Returns: A pointer to an Expr object representing the parsed addend.
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

// Function: parse_expr
// Purpose: Parses an expression (addend or addition expression).
// Parameters:
//   - in: The input stream.
// Returns: A pointer to an Expr object representing the parsed expression.
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

    // If no addition, return the left-hand side
    return lhs;
}

// Function: parse
// Purpose: Main parse function that parses an expression from the input stream.
// Parameters:
//   - in: The input stream.
// Returns: A pointer to an Expr object representing the parsed expression.
Expr* parse(std::istream& in) {
    return parse_expr(in); // Delegate to parse_expr
}

// Function: parse_str
// Purpose: Wrapper for testing that parses a string into an expression.
// Parameters:
//   - s: The input string.
// Returns: A pointer to an Expr object representing the parsed expression.
Expr* parse_str(const std::string& s) {
    std::stringstream ss(s); // Create a string stream from the input string
    return parse(ss); // Parse the expression from the string stream
}