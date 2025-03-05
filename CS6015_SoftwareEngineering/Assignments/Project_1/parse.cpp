#include "parse.hpp"
#include <iostream>
#include <sstream>
#include <cctype>

/**
 * @brief Consumes a specific character from the input stream.
 *
 * @param in The input stream.
 * @param expect The expected character to consume.
 * @throws std::runtime_error if the next character does not match the expected character.
 */
static void consume(std::istream& in, int expect) {
    int c = in.get();  // Read the next character from the input stream
    if (c != expect) { // Check if the character matches the expected one
        throw std::runtime_error("bad input"); // Throw an error if it doesn't match
    }
}

/**
 * @brief Skips whitespace characters in the input stream.
 *
 * @param in The input stream.
 */
void skip_whitespace(std::istream& in) {
    while (true) {
        int c = in.peek(); // Peek at the next character without consuming it
        if (!isspace(c)) break; // Exit the loop if the character is not whitespace
        consume(in, c); // Consume the whitespace character
    }
}

/**
 * @brief Parses a number (integer) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a NumExpr object representing the parsed number.
 * @throws std::runtime_error if the input is invalid (e.g., no digit after '-').
 */
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

/**
 * @brief Parses a variable name from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a VarExpr object representing the parsed variable.
 * @throws std::runtime_error if the variable name contains invalid characters (e.g., '_').
 */
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

/**
 * @brief Parses a _let expression from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a LetExpr object representing the parsed _let expression.
 * @throws std::runtime_error if the input is invalid.
 */
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

/**
 * @brief Parses a boolean value (`_true` or `_false`) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a BoolExpr object representing the parsed boolean value.
 * @throws std::runtime_error if the input is invalid.
 */
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

/**
 * @brief Parses an equality expression (`==`) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to an EqExpr object representing the parsed equality expression.
 * @throws std::runtime_error if the input is invalid.
 */
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

/**
 * @brief Parses an if expression (`_if..._then..._else`) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to an IfExpr object representing the parsed if expression.
 * @throws std::runtime_error if the input is invalid.
 */
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

/**
 * @brief Parses a multicand (number, variable, parenthesized expression, _let expression, or boolean value).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed multicand.
 * @throws std::runtime_error if the input is invalid.
 */
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

/**
 * @brief Parses an addend (multicand or multiplication expression).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed addend.
 */
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

/**
 * @brief Parses an expression (addend or addition expression).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed expression.
 */
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

/**
 * @brief Main parse function that parses an expression from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed expression.
 */
Expr* parse(std::istream& in) {
    return parse_expr(in); // Delegate to parse_expr
}

/**
 * @brief Wrapper for testing that parses a string into an expression.
 *
 * @param s The input string.
 * @return A pointer to an Expr object representing the parsed expression.
 */
Expr* parse_str(const std::string& s) {
    std::stringstream ss(s); // Create a string stream from the input string
    return parse(ss); // Parse the expression from the string stream
}