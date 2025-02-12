#ifndef EXPR_H
#define EXPR_H

#include <string>
#include <stdexcept>
#include <sstream>
#include <iostream>

/**
 * @enum precedence_t
 * @brief Enumeration for operator precedence levels in pretty printing
 *
 * Determines when parentheses are needed during expression formatting
 */
typedef enum {
    prec_none,      ///< No surrounding context (default)
    prec_add,       ///< Precedence level for addition operations
    prec_mult,      ///< Precedence level for multiplication operations
    prec_let        ///< Precedence level for _let expressions
} precedence_t;

/**
 * @class Expr
 * @brief Abstract base class for all expression types
 *
 * Defines common interface for numeric expressions, variables,
 * operations, and let-bindings
 */
class Expr {
public:
    /**
     * @brief Compare expression structures for equality
     * @param e Other expression to compare with
     * @return true if expressions are structurally identical
     */
    virtual bool equals(const Expr* e) = 0;

    /**
     * @brief Evaluate expression to integer value
     * @return Integer result of evaluation
     * @throws std::runtime_error if expression contains unbound variables
     */
    virtual int interp() = 0;

    /**
     * @brief Check if expression contains any variables
     * @return true if expression contains at least one variable
     */
    virtual bool has_variable() = 0;

    /**
     * @brief Substitute variable with another expression
     * @param var Variable name to replace
     * @param replacement Expression to substitute in
     * @return New expression with substitution applied
     */
    virtual Expr* subst(const std::string& var, Expr* replacement) = 0;

    /**
     * @brief Output expression to stream with full parentheses
     * @param ot Output stream to write to
     */
    virtual void printExp(std::ostream& ot) = 0;

    /**
     * @brief Convert expression to string with full parentheses
     * @return String representation of expression
     */
    std::string to_string();

    /**
     * @brief Pretty-print expression with minimal parentheses
     * @param ot Output stream to write to
     * @param prec Precedence level of surrounding context
     * @param newline_pos Reference to track indentation position
     */
    virtual void pretty_print(std::ostream& ot, precedence_t prec,
                             std::streampos& newline_pos) = 0;

    /**
     * @brief Convert expression to pretty-printed string
     * @return Formatted string with minimal parentheses
     */
    std::string to_pretty_string();
};

/**
 * @class NumExpr
 * @brief Represents a numeric constant value
 */
class NumExpr : public Expr {
    int value; ///< Stored numeric value

public:
    /**
     * @brief Construct a numeric expression
     * @param value Integer value to store
     */
    NumExpr(int value);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;
    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec,
                     std::streampos& newline_pos) override;
};

/**
 * @class AddExpr
 * @brief Represents addition of two expressions
 */
class AddExpr : public Expr {
    Expr* lhs; ///< Left-hand side expression
    Expr* rhs; ///< Right-hand side expression

public:
    /**
     * @brief Construct an addition expression
     * @param lhs Left operand
     * @param rhs Right operand
     */
    AddExpr(Expr* lhs, Expr* rhs);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;
    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec,
                     std::streampos& newline_pos) override;
};

/**
 * @class MultExpr
 * @brief Represents multiplication of two expressions
 */
class MultExpr : public Expr {
    Expr* lhs; ///< Left-hand side expression
    Expr* rhs; ///< Right-hand side expression

public:
    /**
     * @brief Construct a multiplication expression
     * @param lhs Left operand
     * @param rhs Right operand
     */
    MultExpr(Expr* lhs, Expr* rhs);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;
    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec,
                     std::streampos& newline_pos) override;
};

/**
 * @class VarExpr
 * @brief Represents a variable identifier
 */
class VarExpr : public Expr {
    std::string name; ///< Variable name

public:
    /**
     * @brief Construct a variable expression
     * @param name Name of variable
     */
    VarExpr(const std::string& name);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;
    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec,
                     std::streampos& newline_pos) override;
};

/**
 * @class LetExpr
 * @brief Represents a let-binding expression
 *
 * Binds a variable to a value in a local scope
 */
class LetExpr : public Expr {
    std::string var; ///< Variable name to bind
    Expr* rhs;       ///< Expression to bind to variable
    Expr* body;      ///< Body expression using the binding

public:
    /**
     * @brief Construct a let expression
     * @param var Variable name
     * @param rhs Right-hand side expression
     * @param body Body expression
     */
    LetExpr(const std::string& var, Expr* rhs, Expr* body);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;
    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec,
                     std::streampos& newline_pos) override;
};

#endif // EXPR_H

#include "expr.h"
#include <stdexcept>
#include <sstream>

// ====================== NumExpr ======================

NumExpr::NumExpr(int value) : value(value) {}

/**
 * @brief Evaluate numeric expression
 * @return Stored integer value
 */
int NumExpr::interp() {
    return value;
}

/**
 * @brief Check if expression contains variables
 * @return false for numeric expressions
 */
bool NumExpr::has_variable() {
    return false;
}

/**
 * @brief Substitute variables (no-op for numbers)
 */
Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    return this;
}

/**
 * @brief Compare numeric expressions for equality
 */
bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e);
    return numExpr && value == numExpr->value;
}

/**
 * @brief Print numeric value with parentheses
 */
void NumExpr::printExp(std::ostream& ot) {
    ot << value;
}

/**
 * @brief Print numeric value without parentheses
 */
void NumExpr::pretty_print(std::ostream& ot, precedence_t prec,
                          std::streampos& newline_pos) {
    ot << value;
}

// ====================== AddExpr ======================

AddExpr::AddExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

/**
 * @brief Evaluate addition expression
 * @return Sum of left and right operands
 */
int AddExpr::interp() {
    return lhs->interp() + rhs->interp();
}

/**
 * @brief Check if either operand contains variables
 */
bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

/**
 * @brief Substitute variables in both operands
 */
Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

/**
 * @brief Compare addition expressions for equality
 */
bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e);
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs);
}

/**
 * @brief Print addition expression with parentheses
 */
void AddExpr::printExp(std::ostream& ot) {
    ot << "(";
    lhs->printExp(ot);
    ot << "+";
    rhs->printExp(ot);
    ot << ")";
}

/**
 * @brief Pretty-print addition with operator precedence handling
 * @param prec Parent context precedence level
 * @param newline_pos Reference to current indentation position
 */
void AddExpr::pretty_print(std::ostream& ot, precedence_t prec,
                          std::streampos& newline_pos) {
    bool use_parentheses = (prec > prec_add);
    if (use_parentheses) ot << "(";
    lhs->pretty_print(ot, prec_add, newline_pos);
    ot << " + ";
    rhs->pretty_print(ot, prec_none, newline_pos);
    if (use_parentheses) ot << ")";
}

// ====================== MultExpr ======================

MultExpr::MultExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

/**
 * @brief Evaluate multiplication expression
 * @return Product of left and right operands
 */
int MultExpr::interp() {
    return lhs->interp() * rhs->interp();
}

/**
 * @brief Check if either operand contains variables
 */
bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

/**
 * @brief Substitute variables in both operands
 */
Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

/**
 * @brief Compare multiplication expressions for equality
 */
bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e);
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs);
}

/**
 * @brief Print multiplication expression with parentheses
 */
void MultExpr::printExp(std::ostream& ot) {
    ot << "(";
    lhs->printExp(ot);
    ot << "*";
    rhs->printExp(ot);
    ot << ")";
}

/**
 * @brief Pretty-print multiplication with precedence handling
 * @details Handles right associativity for nested multiplications
 * @param prec Parent context precedence level
 * @param newline_pos Reference to current indentation position
 */
void MultExpr::pretty_print(std::ostream& ot, precedence_t prec,
                           std::streampos& newline_pos) {
    bool use_parentheses = (prec >= prec_mult);
    if (use_parentheses) ot << "(";
    lhs->pretty_print(ot, prec_mult, newline_pos);
    ot << " * ";

    // Handle right associativity for nested multiplications
    precedence_t rhs_prec = dynamic_cast<MultExpr*>(rhs) ? prec_add : prec_mult;
    rhs->pretty_print(ot, rhs_prec, newline_pos);

    if (use_parentheses) ot << ")";
}

// ====================== VarExpr ======================

VarExpr::VarExpr(const std::string& name) : name(name) {}

/**
 * @brief Evaluate variable (throws error)
 * @throws std::runtime_error Variables cannot be evaluated directly
 */
int VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

/**
 * @brief Check if expression contains variables
 * @return true for variable expressions
 */
bool VarExpr::has_variable() {
    return true;
}

/**
 * @brief Substitute variable if name matches
 */
Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (name == var) {
        return replacement;
    }
    return this;
}

/**
 * @brief Compare variable expressions for equality
 */
bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e);
    return varExpr && name == varExpr->name;
}

/**
 * @brief Print variable name
 */
void VarExpr::printExp(std::ostream& ot) {
    ot << name;
}

/**
 * @brief Print variable name without formatting
 */
void VarExpr::pretty_print(std::ostream& ot, precedence_t prec,
                          std::streampos& newline_pos) {
    ot << name;
}

// ====================== LetExpr ======================

LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body)
    : var(var), rhs(rhs), body(body) {}

/**
 * @brief Evaluate let expression by substitution
 * @return Value of body expression after substitution
 */
int LetExpr::interp() {
    Expr* substituted_body = body->subst(var, new NumExpr(rhs->interp()));
    return substituted_body->interp();
}

/**
 * @brief Check if RHS or body contain variables
 */
bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable();
}

/**
 * @brief Substitute variables in binding and body
 * @details Avoids variable capture in nested bindings
 */
Expr* LetExpr::subst(const std::string& var, Expr* replacement) {
    if (this->var == var) {
        // Directly replace the RHS when substituting the bound variable
        return new LetExpr(this->var, replacement, body);
    } else {
        // Substitute in both RHS and body
        return new LetExpr(this->var, rhs->subst(var, replacement), body->subst(var, replacement));
    }
}

/**
 * @brief Compare let expressions for equality
 */
bool LetExpr::equals(const Expr* e) {
    const LetExpr* letExpr = dynamic_cast<const LetExpr*>(e);
    return letExpr && var == letExpr->var &&
           rhs->equals(letExpr->rhs) &&
           body->equals(letExpr->body);
}

/**
 * @brief Print let expression with parentheses
 */
void LetExpr::printExp(std::ostream& ot) {
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    body->printExp(ot);
    ot << ")";
}

/**
 * @brief Pretty-print let expression with indentation
 * @details Formats _let/_in blocks with proper alignment
 * @param newline_pos Tracks indentation level for multi-line formatting
 */
void LetExpr::pretty_print(std::ostream& ot, precedence_t prec, std::streampos& newline_pos) {
    bool use_parentheses = (prec > prec_let);  // Determine if parentheses are needed
    if (use_parentheses) ot << "(";

    // Capture the starting column of `_let`
    std::streampos let_pos = ot.tellp();
    int indent_level = static_cast<int>(let_pos); // Convert to int for spacing

    ot << "_let " << var << " = ";
    rhs->pretty_print(ot, prec_none, newline_pos);

    // Insert newline before `_in`
    ot << "\n";

    // Correct indentation for `_in`, aligning it under `_let`
    ot << std::string(indent_level, ' ') << "_in ";

    // Print the body correctly aligned
    std::streampos body_pos = ot.tellp();
    body->pretty_print(ot, prec_none, body_pos);

    if (use_parentheses) ot << ")";
}

// ====================== Expr ======================

/**
 * @brief Convert expression to parenthesized string
 */
std::string Expr::to_string() {
    std::stringstream st;
    this->printExp(st);
    return st.str();
}

/**
 * @brief Convert expression to formatted string with minimal parentheses
 */
std::string Expr::to_pretty_string() {
    std::stringstream st;
    std::streampos newline_pos = 0;
    this->pretty_print(st, prec_none, newline_pos);
    return st.str();
}