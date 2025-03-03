#ifndef PARSE_HPP
#define PARSE_HPP

#include "expr.h"
#include <istream>
#include <string>

// Function declarations
//static void consume(std::istream& in, int expect);
void skip_whitespace(std::istream& in);
Expr* parse_num(std::istream& in);
Expr* parse_var(std::istream& in);
Expr* parse_multicand(std::istream& in);
Expr* parse_addend(std::istream& in);
Expr* parse_expr(std::istream& in);

// Main parse function
Expr* parse(std::istream& in);

// Wrapper for testing
Expr* parse_str(const std::string& s);

#endif // PARSE_HPP