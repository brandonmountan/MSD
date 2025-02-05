// expr.cpp
#include "expr.h"
#include <stdexcept>
#include <sstream>

// ====================== NumExpr ======================

/**
 * @brief Constructs a numeric expression.
 *
 * @param value The numeric value.
 */
NumExpr::NumExpr(int value) {
    this->value = value;
}

/**
 * @brief Evaluates the numeric expression to its value.
 *
 * @return The numeric value.
 */
int NumExpr::interp() {
    return value;
}

/**
 * @brief Checks if the numeric expression contains a variable.
 *
 * @return false (numeric expressions do not contain variables).
 */
bool NumExpr::has_variable() {
    return false;
}

/**
 * @brief Substitutes a variable with another expression (no effect for numeric expressions).
 *
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return This numeric expression (no substitution occurs).
 */
Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    return this;
}

/**
 * @brief Checks if this numeric expression is equal to another expression.
 *
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e);
    return numExpr && this->value == numExpr->value;
}

/**
 * @brief Prints the numeric expression to an output stream.
 *
 * @param ot The output stream to print to.
 */
void NumExpr::printExp(std::ostream& ot) {
    ot << value;
}

/**
 * @brief Pretty-prints the numeric expression to an output stream.
 *
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 */
void NumExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    ot << value;
}

// ====================== AddExpr ======================

/**
 * @brief Constructs an addition expression.
 *
 * @param lhs The left-hand side expression.
 * @param rhs The right-hand side expression.
 */
AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

/**
 * @brief Evaluates the addition expression to its result.
 *
 * @return The sum of the left-hand side and right-hand side expressions.
 */
int AddExpr::interp() {
    return lhs->interp() + rhs->interp();
}

/**
 * @brief Checks if the addition expression contains a variable.
 *
 * @return true if either the left-hand side or right-hand side contains a variable, false otherwise.
 */
bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

/**
 * @brief Substitutes a variable with another expression in the addition expression.
 *
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return A new addition expression with the substitution applied.
 */
Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

/**
 * @brief Checks if this addition expression is equal to another expression.
 *
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e);
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs);
}

/**
 * @brief Prints the addition expression to an output stream.
 *
 * @param ot The output stream to print to.
 */
void AddExpr::printExp(std::ostream& ot) {
    ot << "(";
    this->lhs->printExp(ot);
    ot << "+";
    this->rhs->printExp(ot);
    ot << ")";
}

/**
 * @brief Pretty-prints the addition expression to an output stream with proper precedence handling.
 *
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 */
void AddExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    bool use_parentheses = (prec >= prec_add);

    if (use_parentheses) {
        ot << "(";
    }
    lhs->pretty_print(ot, prec_add);
    ot << " + ";
    rhs->pretty_print(ot, prec_none);
    if (use_parentheses) {
        ot << ")";
    }
}

// ====================== MultExpr ======================

/**
 * @brief Constructs a multiplication expression.
 *
 * @param lhs The left-hand side expression.
 * @param rhs The right-hand side expression.
 */
MultExpr::MultExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

/**
 * @brief Evaluates the multiplication expression to its result.
 *
 * @return The product of the left-hand side and right-hand side expressions.
 */
int MultExpr::interp() {
    return lhs->interp() * rhs->interp();
}

/**
 * @brief Checks if the multiplication expression contains a variable.
 *
 * @return true if either the left-hand side or right-hand side contains a variable, false otherwise.
 */
bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

/**
 * @brief Substitutes a variable with another expression in the multiplication expression.
 *
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return A new multiplication expression with the substitution applied.
 */
Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

/**
 * @brief Checks if this multiplication expression is equal to another expression.
 *
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e);
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs);
}

/**
 * @brief Prints the multiplication expression to an output stream.
 *
 * @param ot The output stream to print to.
 */
void MultExpr::printExp(std::ostream& ot) {
    ot << "(";
    this->lhs->printExp(ot);
    ot << "*";
    this->rhs->printExp(ot);
    ot << ")";
}

/**
 * @brief Pretty-prints the multiplication expression to an output stream with proper precedence handling.
 *
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 */
void MultExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    bool use_parentheses = (prec >= prec_mult);
    if (use_parentheses) {
        ot << "(";
    }
    lhs->pretty_print(ot, prec_mult);
    ot << " * ";
    rhs->pretty_print(ot, prec_add);
    if (use_parentheses) {
        ot << ")";
    }
}

// ====================== VarExpr ======================

/**
 * @brief Constructs a variable expression.
 *
 * @param name The name of the variable.
 */
VarExpr::VarExpr(const std::string& name) {
    this->name = name;
}

/**
 * @brief Evaluates the variable expression (throws an exception since variables have no value).
 *
 * @throws std::runtime_error Always throws an exception.
 */
int VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

/**
 * @brief Checks if the variable expression contains a variable.
 *
 * @return true (variable expressions always contain a variable).
 */
bool VarExpr::has_variable() {
    return true;
}

/**
 * @brief Substitutes a variable with another expression in the variable expression.
 *
 * @param var The variable to substitute.
 * @param replacement The expression to replace the variable with.
 * @return The replacement expression if the variable matches, otherwise this variable expression.
 */
Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (this->name == var) {
        return replacement;
    }
    return this;
}

/**
 * @brief Checks if this variable expression is equal to another expression.
 *
 * @param e The expression to compare with.
 * @return true if the expressions are equal, false otherwise.
 */
bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e);
    return varExpr && this->name == varExpr->name;
}

/**
 * @brief Prints the variable expression to an output stream.
 *
 * @param ot The output stream to print to.
 */
void VarExpr::printExp(std::ostream& ot) {
    ot << name;
}

/**
 * @brief Pretty-prints the variable expression to an output stream.
 *
 * @param ot The output stream to print to.
 * @param prec The precedence level of the parent expression.
 */
void VarExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    ot << name;
}

// ====================== Expr ======================

/**
 * @brief Converts the expression to a string.
 *
 * @return A string representation of the expression.
 */
std::string Expr::to_string() {
    std::stringstream st;
    this->printExp(st);
    return st.str();
}

/**
 * @brief Converts the expression to a pretty-printed string.
 *
 * @return A pretty-printed string representation of the expression.
 */
std::string Expr::to_pretty_string() {
    std::stringstream st;
    this->pretty_print(st, prec_none);
    return st.str();
}