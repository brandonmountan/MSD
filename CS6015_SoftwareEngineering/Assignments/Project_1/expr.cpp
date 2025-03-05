//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "expr.h"       // Include the header file for expression classes
#include <stdexcept>    // For std::runtime_error
#include <sstream>      // For std::stringstream
#include "val.h"        // Include val.h for Val and NumVal

// ====================== Expr ======================

/**
 * @brief Converts the expression to a string representation.
 * @return A string representation of the expression.
 */
std::string Expr::to_string() {
    std::stringstream st(""); // Create a string stream
    this->printExp(st);       // Print the expression to the stream
    return st.str();          // Return the string representation
}

/**
 * @brief Converts the expression to a pretty-printed string representation.
 * @return A pretty-printed string representation of the expression.
 */
std::string Expr::to_pretty_string() {
    std::stringstream st; // Create a string stream
    this->pretty_print(st, prec_none); // Pretty-print the expression to the stream
    return st.str();      // Return the pretty-printed string
}

/**
 * @brief Pretty-prints the expression at a specific precedence level.
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 * @param last_newline_pos The position of the last newline in the output stream.
 */
void Expr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    (void)prec; // Mark as unused
    (void)last_newline_pos; // Mark as unused
    printExp(ot); // Default implementation: just call printExp
}

/**
 * @brief Pretty-prints the expression with proper precedence handling.
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 */
void Expr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos);  // Delegate to pretty_print_at
}

// ====================== NumExpr ======================

/**
 * @brief Constructs a numeric expression.
 * @param value The numeric value.
 */
NumExpr::NumExpr(int value) {
    this->value = value; // Store the value
}

/**
 * @brief Interprets the number expression by returning its value.
 * @return A NumVal object representing the number.
 */
Val* NumExpr::interp() {
    return new NumVal(value);
}

/**
 * @brief Checks if the number expression contains a variable.
 * @return false (numbers do not contain variables).
 */
bool NumExpr::has_variable() {
    return false;
}

/**
 * @brief Substitutes a variable with a replacement expression.
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return The current number expression (no substitution needed).
 */
Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    (void)var; // Mark as unused
    (void)replacement; // Mark as unused
    return this; // Numbers do not contain variables, so return the same expression
}

/**
 * @brief Checks if this number expression is equal to another expression.
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e); // Cast to NumExpr
    return numExpr && this->value == numExpr->value; // Compare values
}

/**
 * @brief Prints the number expression to an output stream.
 * @param ot The output stream to print to.
 */
void NumExpr::printExp(std::ostream& ot) {
    ot << value; // Print the number
}

// ====================== AddExpr ======================

/**
 * @brief Constructs an addition expression.
 * @param lhs The left-hand side expression.
 * @param rhs The right-hand side expression.
 */
AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Store the left-hand side
    this->rhs = rhs; // Store the right-hand side
}

/**
 * @brief Interprets the addition expression by evaluating its sub-expressions.
 * @return A Val* object representing the sum of the left and right sub-expressions.
 */
Val* AddExpr::interp() {
    Val* lhsVal = lhs->interp(); // Evaluate the left-hand side
    Val* rhsVal = rhs->interp(); // Evaluate the right-hand side
    Val* result = lhsVal->add_to(rhsVal); // Add the two values
    return result; // Return the result
}

/**
 * @brief Checks if the addition expression contains a variable.
 * @return true if either sub-expression contains a variable, false otherwise.
 */
bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // Check both sub-expressions
}

/**
 * @brief Substitutes a variable with a replacement expression in both sub-expressions.
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return A new AddExpr with the substitution applied.
 */
Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

/**
 * @brief Checks if this addition expression is equal to another expression.
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e); // Cast to AddExpr
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs); // Compare sub-expressions
}

/**
 * @brief Prints the addition expression to an output stream.
 * @param ot The output stream to print to.
 */
void AddExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "+";       // Print the addition operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

/**
 * @brief Pretty-prints the addition expression with proper precedence handling.
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 * @param last_newline_pos The position of the last newline in the output stream.
 */
void AddExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool use_parentheses = (prec >= prec_add); // Determine if parentheses are needed
    if (use_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }
    lhs->pretty_print_at(ot, prec_add, last_newline_pos); // Pretty-print the left-hand side
    ot << " + "; // Print the addition operator
    rhs->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the right-hand side
    if (use_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}

// ====================== MultExpr ======================

/**
 * @brief Constructs a multiplication expression.
 * @param lhs The left-hand side expression.
 * @param rhs The right-hand side expression.
 */
MultExpr::MultExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Store the left-hand side
    this->rhs = rhs; // Store the right-hand side
}

/**
 * @brief Interprets the multiplication expression by evaluating its sub-expressions.
 * @return A Val* object representing the product of the left and right sub-expressions.
 */
Val* MultExpr::interp() {
    Val* lhsVal = lhs->interp(); // Evaluate the left-hand side
    Val* rhsVal = rhs->interp(); // Evaluate the right-hand side
    Val* result = lhsVal->mult_with(rhsVal); // Multiply the two values
    return result;
}

/**
 * @brief Checks if the multiplication expression contains a variable.
 * @return true if either sub-expression contains a variable, false otherwise.
 */
bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // Check both sub-expressions
}

/**
 * @brief Substitutes a variable with a replacement expression in both sub-expressions.
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return A new MultExpr with the substitution applied.
 */
Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

/**
 * @brief Checks if this multiplication expression is equal to another expression.
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e); // Cast to MultExpr
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs); // Compare sub-expressions
}

/**
 * @brief Prints the multiplication expression to an output stream.
 * @param ot The output stream to print to.
 */
void MultExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "*";       // Print the multiplication operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

/**
 * @brief Pretty-prints the multiplication expression with proper precedence handling.
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 * @param last_newline_pos The position of the last newline in the output stream.
 */
void MultExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool use_parentheses = (prec >= prec_mult); // Determine if parentheses are needed
    if (use_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }
    lhs->pretty_print_at(ot, prec_mult, last_newline_pos); // Pretty-print the left-hand side
    ot << " * "; // Print the multiplication operator
    rhs->pretty_print_at(ot, prec_add, last_newline_pos); // Pretty-print the right-hand side
    if (use_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}

// ====================== VarExpr ======================

/**
 * @brief Constructs a variable expression.
 * @param name The name of the variable.
 */
VarExpr::VarExpr(const std::string& name) {
    this->name = name; // Store the variable name
}

/**
 * @brief Interprets the variable expression (throws an error since variables have no value).
 * @throws std::runtime_error because variables cannot be interpreted without a value.
 */
Val* VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

/**
 * @brief Checks if the variable expression contains a variable.
 * @return true (variables always contain themselves).
 */
bool VarExpr::has_variable() {
    return true;
}

/**
 * @brief Substitutes the variable with a replacement expression if it matches the variable name.
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return The replacement expression if the variable matches, otherwise the current expression.
 */
Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (this->name == var) {
        return replacement; // Substitute if the variable matches
    }
    return this; // Otherwise, return the current expression
}

/**
 * @brief Checks if this variable expression is equal to another expression.
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e); // Cast to VarExpr
    return varExpr && this->name == varExpr->name; // Compare variable names
}

/**
 * @brief Prints the variable expression to an output stream.
 * @param ot The output stream to print to.
 */
void VarExpr::printExp(std::ostream& ot) {
    ot << name; // Print the variable name
}

// ====================== LetExpr ======================

/**
 * @brief Constructs a let expression.
 * @param var The variable to bind.
 * @param rhs The right-hand side expression.
 * @param body The body expression.
 */
LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body) {
    this->var = var; // Store the variable
    this->rhs = rhs; // Store the right-hand side
    this->body = body; // Store the body
}

/**
 * @brief Checks if this let expression is equal to another expression.
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool LetExpr::equals(const Expr* e) {
    const LetExpr* letExpr = dynamic_cast<const LetExpr*>(e); // Cast to LetExpr
    return letExpr && this->var == letExpr->var && // Compare variables
           this->rhs->equals(letExpr->rhs) && // Compare right-hand sides
           this->body->equals(letExpr->body); // Compare bodies
}

/**
 * @brief Interprets the let expression by evaluating the right-hand side and substituting it into the body.
 * @return A Val* object representing the result of interpreting the substituted body expression.
 */
Val* LetExpr::interp() {
    Val* rhsValue = rhs->interp(); // Evaluate the right-hand side
    Expr* substitutedBody = body->subst(var, rhsValue->to_expr()); // Substitute the variable
    return substitutedBody->interp(); // Interpret the substituted body
}

/**
 * @brief Checks if the let expression contains a variable.
 * @return true if either the right-hand side or body contains a variable, false otherwise.
 */
bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable(); // Check both sub-expressions
}

/**
 * @brief Substitutes a variable with a replacement expression in the let expression.
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return A new LetExpr with the substitution applied.
 */
Expr* LetExpr::subst(const std::string& var, Expr* replacement) {
    if (this->var == var) {
        // If the variable to substitute is the bound variable, do not substitute in the body
        return new LetExpr(this->var, rhs->subst(var, replacement), body);
    }
    // Substitute in both rhs and body
    return new LetExpr(this->var, rhs->subst(var, replacement), body->subst(var, replacement));
}

/**
 * @brief Prints the let expression to an output stream.
 * @param ot The output stream to print to.
 */
void LetExpr::printExp(std::ostream& ot) {
    ot << "(_let " << var << "="; // Print the let keyword and variable
    rhs->printExp(ot); // Print the right-hand side
    ot << " _in "; // Print the in keyword
    body->printExp(ot); // Print the body
    ot << ")"; // Print closing parenthesis
}

/**
 * @brief Pretty-prints the let expression with proper indentation.
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 */
void LetExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos); // Delegate to pretty_print_at
}

/**
 * @brief Pretty-prints the let expression with proper indentation and precedence handling.
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 * @param last_newline_pos The position of the last newline in the output stream.
 */
void LetExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec != prec_none); // Determine if parentheses are needed
    if (needs_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }

    std::streampos position1 = last_newline_pos; // Save the position of the last newline
    std::streampos current_pos = ot.tellp(); // Get the current position in the stream

    // Print the _let part
    ot << "_let " << var << " = "; // Print the let keyword and variable
    rhs->pretty_print(ot, prec_none); // Pretty-print the right-hand side

    // Track the position after the newline
    ot << "\n"; // Print a newline
    last_newline_pos = ot.tellp(); // Update the position of the last newline

    // Calculate the indentation for _in
    for (int i = position1; i < current_pos; i++) {
        ot << " "; // Print spaces for indentation
    }

    // Print the _in part with proper indentation
    ot << "_in  "; // Print the in keyword

    // Print the body with proper context
    body->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the body

    if (needs_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}