#include "parse.hpp"
#include <iostream>
#include <sstream>
#include <cctype> // For isdigit

// Consume a specific character from the input stream
static void consume(std::istream& in, int expect) {
    int c = in.get();
    if (c != expect) {
        throw std::runtime_error("consume mismatch");
    }
}

// Skip whitespace characters in the input stream
void skip_whitespace(std::istream& in) {
    while (true) {
        int c = in.peek();
        if (!isspace(c)) break;
        consume(in, c);
    }
}

// Parse a number (integer) from the input stream
Expr* parse_num(std::istream& in) {
    int n = 0;
    bool negative = false;

    if (in.peek() == '-') {
        negative = true;
        consume(in, '-');
    }

    while (true) {
        int c = in.peek();
        if (isdigit(c)) {
            consume(in, c);
            n = n * 10 + (c - '0');
        } else {
            break;
        }
    }

    if (negative) {
        n = -n;
    }

    return new NumExpr(n);
}

// Parse a variable from the input stream
Expr* parse_var(std::istream& in) {
    std::string name;
    while (true) {
        int c = in.peek();
        if (isalpha(c)) {
            consume(in, c);
            name += static_cast<char>(c);
        } else {
            break;
        }
    }
    return new VarExpr(name);
}

// Parse a multicand (number, variable, or parenthesized expression)
Expr* parse_multicand(std::istream& in) {
    skip_whitespace(in);
    int c = in.peek();

    if ((c == '-') || isdigit(c)) {
        return parse_num(in);
    } else if (c == '(') {
        consume(in, '(');
        Expr* e = parse_expr(in);
        skip_whitespace(in);
        consume(in, ')');
        return e;
    } else if (isalpha(c)) {
        return parse_var(in);
    } else {
        throw std::runtime_error("Invalid input");
    }
}

// Parse an addend (multicand or multiplication expression)
Expr* parse_addend(std::istream& in) {
    Expr* lhs = parse_multicand(in);
    skip_whitespace(in);
    int c = in.peek();

    if (c == '*') {
        consume(in, '*');
        Expr* rhs = parse_addend(in);
        return new MultExpr(lhs, rhs);
    }

    return lhs;
}

// Parse an expression (addend or addition expression)
Expr* parse_expr(std::istream& in) {
    Expr* lhs = parse_addend(in);
    skip_whitespace(in);
    int c = in.peek();

    if (c == '+') {
        consume(in, '+');
        Expr* rhs = parse_expr(in);
        return new AddExpr(lhs, rhs);
    }

    return lhs;
}

// Wrapper for testing: parse a string into an expression
Expr* parse_str(const std::string& s) {
    std::stringstream ss(s);
    return parse_expr(ss);
}