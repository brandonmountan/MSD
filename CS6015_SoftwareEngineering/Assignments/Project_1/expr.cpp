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

std::string Expr::to_string() {
    std::stringstream st(""); // Create a string stream
    this->printExp(st);       // Print the expression to the stream
    return st.str();          // Return the string representation
}

std::string Expr::to_pretty_string() {
    std::stringstream st; // Create a string stream
    this->pretty_print(st, prec_none); // Pretty-print the expression to the stream
    return st.str();      // Return the pretty-printed string
}

void Expr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos);  // Delegate to pretty_print_at
}

void Expr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    (void)prec; // Mark as unused
    (void)last_newline_pos; // Mark as unused
    printExp(ot); // Default implementation: just call printExp
}

// ====================== NumExpr ======================

NumExpr::NumExpr(int value) {
    this->value = value; // Store the value
}


Val* NumExpr::interp() {
    return new NumVal(value);
}

bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e); // Cast to NumExpr
    return numExpr && this->value == numExpr->value; // Compare values
}

bool NumExpr::has_variable() {
    return false;
}

Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    (void)var; // Mark as unused
    (void)replacement; // Mark as unused
    return this; // Numbers do not contain variables, so return the same expression
}

void NumExpr::printExp(std::ostream& ot) {
    ot << value; // Print the number
}

// ====================== AddExpr ======================

AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Store the left-hand side
    this->rhs = rhs; // Store the right-hand side
}


bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e); // Cast to AddExpr
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs); // Compare sub-expressions
}

Val* AddExpr::interp() {
    Val* lhsVal = lhs->interp();
    Val* rhsVal = rhs->interp();
    NumVal* lhsNum = dynamic_cast<NumVal*>(lhsVal);
    NumVal* rhsNum = dynamic_cast<NumVal*>(rhsVal);

    if (!lhsNum || !rhsNum) {
        throw std::runtime_error("Cannot add non-numeric values");
    }

    // Use uint64_t for intermediate calculations
    uint64_t result = static_cast<uint64_t>(lhsNum->value) + static_cast<uint64_t>(rhsNum->value);

    // Check for overflow
    if (result > static_cast<uint64_t>(INT_MAX) || result < static_cast<uint64_t>(INT_MIN)) {
        throw std::runtime_error("arithmetic overflow");
    }

    return new NumVal(static_cast<int>(result));
}

bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // Check both sub-expressions
}

Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

void AddExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "+";       // Print the addition operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

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

MultExpr::MultExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Store the left-hand side
    this->rhs = rhs; // Store the right-hand side
}

bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e); // Cast to MultExpr
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs); // Compare sub-expressions
}

Val* MultExpr::interp() {
    Val* lhsVal = lhs->interp();
    Val* rhsVal = rhs->interp();
    NumVal* lhsNum = dynamic_cast<NumVal*>(lhsVal);
    NumVal* rhsNum = dynamic_cast<NumVal*>(rhsVal);

    if (!lhsNum || !rhsNum) {
        throw std::runtime_error("Cannot multiply non-numeric values");
    }

    // Use uint64_t for intermediate calculations
    uint64_t result = static_cast<uint64_t>(lhsNum->value) * static_cast<uint64_t>(rhsNum->value);

    // Check for overflow
    if (result > static_cast<uint64_t>(INT_MAX) || result < static_cast<uint64_t>(INT_MIN)) {
        throw std::runtime_error("arithmetic overflow");
    }

    return new NumVal(static_cast<int>(result));
}

bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // Check both sub-expressions
}

Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

void MultExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "*";       // Print the multiplication operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

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

VarExpr::VarExpr(const std::string& name) {
    this->name = name; // Store the variable name
}

bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e); // Cast to VarExpr
    return varExpr && this->name == varExpr->name; // Compare variable names
}

Val* VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

bool VarExpr::has_variable() {
    return true;
}

Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (this->name == var) {
        return replacement; // Substitute if the variable matches
    }
    return this; // Otherwise, return the current expression
}

void VarExpr::printExp(std::ostream& ot) {
    ot << name; // Print the variable name
}

// ====================== LetExpr ======================

LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body) {
    this->var = var; // Store the variable
    this->rhs = rhs; // Store the right-hand side
    this->body = body; // Store the body
}


bool LetExpr::equals(const Expr* e) {
    const LetExpr* letExpr = dynamic_cast<const LetExpr*>(e); // Cast to LetExpr
    return letExpr && this->var == letExpr->var && // Compare variables
           this->rhs->equals(letExpr->rhs) && // Compare right-hand sides
           this->body->equals(letExpr->body); // Compare bodies
}

Val* LetExpr::interp() {
    Val* rhsValue = rhs->interp(); // Evaluate the right-hand side
    Expr* substitutedBody = body->subst(var, rhsValue->to_expr()); // Substitute the variable
    return substitutedBody->interp(); // Interpret the substituted body
}

bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable(); // Check both sub-expressions
}

Expr* LetExpr::subst(const std::string& var, Expr* replacement) {
    if (this->var == var) {
        // If the variable to substitute is the bound variable, do not substitute in the body
        return new LetExpr(this->var, rhs->subst(var, replacement), body);
    }
    // Substitute in both rhs and body
    return new LetExpr(this->var, rhs->subst(var, replacement), body->subst(var, replacement));
}

void LetExpr::printExp(std::ostream& ot) {
    ot << "(_let " << var << "="; // Print the let keyword and variable
    rhs->printExp(ot); // Print the right-hand side
    ot << " _in "; // Print the in keyword
    body->printExp(ot); // Print the body
    ot << ")"; // Print closing parenthesis
}

void LetExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos); // Delegate to pretty_print_at
}

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

// ====================== BoolExpr ======================

BoolExpr::BoolExpr(bool value) : value(value) {}

bool BoolExpr::equals(const Expr* e) {
    const BoolExpr* boolExpr = dynamic_cast<const BoolExpr*>(e);
    return boolExpr && this->value == boolExpr->value;
}

Val* BoolExpr::interp() {
    return new BoolVal(value);
}

bool BoolExpr::has_variable() {
    return false;
}

Expr* BoolExpr::subst(const std::string& var, Expr* replacement) {
  	(void)var;
    (void)replacement;
    return this;
}

void BoolExpr::printExp(std::ostream& ot) {
    ot << (value ? "_true" : "_false");
}

void BoolExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
  	(void)prec;
    (void)last_newline_pos;
    ot << (value ? "_true" : "_false");
}

// ====================== IfExpr ======================

IfExpr::IfExpr(Expr* condition, Expr* then_branch, Expr* else_branch)
    : condition(condition), then_branch(then_branch), else_branch(else_branch) {}


bool IfExpr::equals(const Expr* e) {
    const IfExpr* ifExpr = dynamic_cast<const IfExpr*>(e);
    return ifExpr && condition->equals(ifExpr->condition) &&
           then_branch->equals(ifExpr->then_branch) &&
           else_branch->equals(ifExpr->else_branch);
}

Val* IfExpr::interp() {
    Val* condVal = condition->interp();
    BoolVal* boolVal = dynamic_cast<BoolVal*>(condVal);
    if (!boolVal) {
        throw std::runtime_error("Condition must be a boolean");
    }
    if (boolVal->is_true()) {
        return then_branch->interp();
    } else {
        return else_branch->interp();
    }
}

bool IfExpr::has_variable() {
    return condition->has_variable() || then_branch->has_variable() || else_branch->has_variable();
}

Expr* IfExpr::subst(const std::string& var, Expr* replacement) {
    return new IfExpr(condition->subst(var, replacement),
                      then_branch->subst(var, replacement),
                      else_branch->subst(var, replacement));
}

void IfExpr::printExp(std::ostream& ot) {
    ot << "(_if ";
    condition->printExp(ot);
    ot << " _then ";
    then_branch->printExp(ot);
    ot << " _else ";
    else_branch->printExp(ot);
    ot << ")";
}

void IfExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos); // Delegate to pretty_print_at
}

void IfExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec != prec_none); // Determine if parentheses are needed
    if (needs_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }

    std::streampos position1 = last_newline_pos; // Save the position of the last newline
    std::streampos current_pos = ot.tellp(); // Get the current position in the stream

    // Print the _if part
    ot << "_if "; // Print the if keyword
    condition->pretty_print(ot, prec_none); // Pretty-print the condition

    // Track the position after the newline
    ot << "\n"; // Print a newline
    last_newline_pos = ot.tellp(); // Update the position of the last newline

    // Calculate the indentation for _in
    for (int i = position1; i < current_pos; i++) {
        ot << " "; // Print spaces for indentation
    }

    // Print the _then part with proper indentation
    ot << "_then "; // Print the then keyword
    then_branch->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the then branch

    // Track the position after the newline
    ot << "\n"; // Print a newline
    last_newline_pos = ot.tellp(); // Update the position of the last newline

    // Calculate the indentation for _else
    for (int i = position1; i < current_pos; i++) {
        ot << " "; // Print spaces for indentation
    }

    // Print the _else part with proper indentation
    ot << "_else "; // Print the else keyword
    else_branch->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the else branch

    if (needs_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}


// ====================== EqExpr ======================


EqExpr::EqExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool EqExpr::equals(const Expr* e) {
    const EqExpr* eqExpr = dynamic_cast<const EqExpr*>(e);
    return eqExpr && lhs->equals(eqExpr->lhs) && rhs->equals(eqExpr->rhs);
}

Val* EqExpr::interp() {
    Val* lhsVal = lhs->interp();
    Val* rhsVal = rhs->interp();
    return new BoolVal(lhsVal->equals(rhsVal));
}

bool EqExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* EqExpr::subst(const std::string& var, Expr* replacement) {
    return new EqExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

void EqExpr::printExp(std::ostream& ot) {
    ot << "(";
    lhs->printExp(ot);
    ot << "==";
    rhs->printExp(ot);
    ot << ")";
}

void EqExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec >= prec_eq);
    if (needs_parentheses) ot << "(";
    lhs->pretty_print_at(ot, prec_eq, last_newline_pos);
    ot << " == ";
    rhs->pretty_print_at(ot, prec_none, last_newline_pos);
    if (needs_parentheses) ot << ")";
}