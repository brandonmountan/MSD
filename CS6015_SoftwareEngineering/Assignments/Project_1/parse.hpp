//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#ifndef PARSE_HPP
#define PARSE_HPP

#include "expr.h"
#include "val.h"
#include "pointer.h"
#include <istream>
#include <string>

/**
 * @brief Consumes a specific character from the input stream.
 *
 * @param in The input stream.
 * @param expect The expected character to consume.
 * @throws std::runtime_error if the next character does not match the expected character.
 */
void consume(std::istream& in, int expect);

/**
 * @brief Skips whitespace characters in the input stream.
 *
 * @param in The input stream.
 */
void skip_whitespace(std::istream& in);

/**
 * @brief Parses a number (integer) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a NumExpr object representing the parsed number.
 * @throws std::runtime_error if the input is invalid (e.g., no digit after '-').
 */
PTR(Expr) parse_num(std::istream& in);

/**
 * @brief Parses a variable name from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a VarExpr object representing the parsed variable.
 * @throws std::runtime_error if the variable name contains invalid characters (e.g., '_').
 */
PTR(Expr) parse_var(std::istream& in);

/**
 * @brief Parses a _let expression from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a LetExpr object representing the parsed _let expression.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_let(std::istream& in); // Added for parsing _let expressions

/**
 * @brief Parses a multicand (number, variable, parenthesized expression, _let expression, or boolean value).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed multicand.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_multicand(std::istream& in);

/**
 * @brief Parses an addend (multicand or multiplication expression).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed addend.
 */
PTR(Expr) parse_addend(std::istream& in);

/**
 * @brief Parses an expression (addend or addition expression).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed expression.
 */
PTR(Expr) parse_expr(std::istream& in);

/**
 * @brief Parses a boolean value (`_true` or `_false`) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a BoolExpr object representing the parsed boolean value.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_bool(std::istream& in);

/**
 * @brief Parses an equality expression (`==`) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to an EqExpr object representing the parsed equality expression.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_eq(std::istream& in);

/**
 * @brief Parses an if expression (`_if..._then..._else`) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to an IfExpr object representing the parsed if expression.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_if(std::istream& in);

/**
 * @brief Main parse function that parses an expression from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed expression.
 */
PTR(Expr) parse(std::istream& in);

/**
 * @brief Wrapper for testing that parses a string into an expression.
 *
 * @param s The input string.
 * @return A pointer to an Expr object representing the parsed expression.
 */
PTR(Expr) parse_str(const std::string& s);

/**
 * @brief Parses a function expression (_fun (x) body) from the input stream.
 *
 * @param in The input stream.
 * @return A pointer to a FunExpr object representing the parsed function.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_fun(std::istream& in);

/**
 * @brief Parses an inner expression (base case for multicand parsing).
 *
 * @param in The input stream.
 * @return A pointer to an Expr object representing the parsed inner expression.
 * @throws std::runtime_error if the input is invalid.
 */
PTR(Expr) parse_inner(std::istream& in);

#endif // PARSE_HPP