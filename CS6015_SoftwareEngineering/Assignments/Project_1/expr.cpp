// Implementation file for expr.h
#include "expr.h"
#include <stdexcept>

// Implementation for NumExpr

// Constructor for NumExpr
// Initializes the numeric value of the expression
NumExpr::NumExpr(int value) {
    this->value = value;
}

// interp(): Evaluates the numeric expression and returns its value
int NumExpr::interp() {
    return value;
}

// has_variable(): Checks if the expression contains a variable
// Since this is a number, it always returns false
bool NumExpr::has_variable() {
    return false;
}

// subst(): Performs substitution on the expression
// For a number, there is nothing to substitute, so it returns itself
Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    return this;
}

// equals(): Compares this expression with another for structural equality
// Checks if the other expression is also a NumExpr with the same value
bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e);
    return numExpr && this->value == numExpr->value;
}

// Implementation for AddExpr

// Constructor for AddExpr
// Initializes the left-hand side (lhs) and right-hand side (rhs) expressions
AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

// interp(): Evaluates the addition expression by evaluating both operands
// and returning their sum
int AddExpr::interp() {
    return lhs->interp() + rhs->interp();
}

// has_variable(): Checks if the addition expression contains a variable
// Returns true if either operand has a variable
bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

// subst(): Performs substitution on the addition expression
// Substitutes variables in both the lhs and rhs operands
Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

// equals(): Compares this expression with another for structural equality
// Checks if the other expression is also an AddExpr and if its operands are equal
bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e);
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs);
}

// Implementation for MultExpr

// Constructor for MultExpr
// Initializes the left-hand side (lhs) and right-hand side (rhs) expressions
MultExpr::MultExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

// interp(): Evaluates the multiplication expression by evaluating both operands
// and returning their product
int MultExpr::interp() {
    return lhs->interp() * rhs->interp();
}

// has_variable(): Checks if the multiplication expression contains a variable
// Returns true if either operand has a variable
bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

// subst(): Performs substitution on the multiplication expression
// Substitutes variables in both the lhs and rhs operands
Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

// equals(): Compares this expression with another for structural equality
// Checks if the other expression is also a MultExpr and if its operands are equal
bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e);
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs);
}

// Implementation for VarExpr

// Constructor for VarExpr
// Initializes the variable name
VarExpr::VarExpr(const std::string& name) {
    this->name = name;
}

// interp(): Throws an exception because a variable cannot be evaluated
int VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

// has_variable(): Checks if the variable expression contains a variable
// Always returns true because this is a variable
bool VarExpr::has_variable() {
    return true;
}

// subst(): Performs substitution on the variable expression
// If the variable name matches, it replaces the variable with the replacement
// expression; otherwise, it returns itself
Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (this->name == var) {
        return replacement;
    }
    return this;
}

// equals(): Compares this expression with another for structural equality
// Checks if the other expression is also a VarExpr with the same variable name
bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e);
    return varExpr && this->name == varExpr->name;
}
