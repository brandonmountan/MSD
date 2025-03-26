//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/04/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "val.h"
#include "expr.h" // Include expr.h for Expr and NumExpr
#include "parse.hpp"
#include <stdexcept> // For std::runtime_error

// ====================== NumVal Implementation ======================

NumVal::NumVal(int value) : value(value) {}

bool NumVal::equals(Val* other) {
    NumVal* otherNum = dynamic_cast<NumVal*>(other);
    return otherNum && this->value == otherNum->value;
}

Expr* NumVal::to_expr() {
    return new NumExpr(value); // Convert NumVal to NumExpr
}

std::string NumVal::to_string() {
    return std::to_string(value); // Convert NumVal to string
}

Val* NumVal::add_to(Val* other) {
    NumVal* otherNum = dynamic_cast<NumVal*>(other);
    if (!otherNum) {
        throw std::runtime_error("Cannot add non-numeric values");
    }
    return new NumVal(this->value + otherNum->value);
}

Val* NumVal::mult_with(Val* other) {
    NumVal* otherNum = dynamic_cast<NumVal*>(other);
    if (!otherNum) {
        throw std::runtime_error("Cannot multiply non-numeric values");
    }
    return new NumVal(this->value * otherNum->value);
}

bool NumVal::is_true() {
    throw std::runtime_error("Cannot use a numeric value as a boolean");
}

Val* NumVal::call(Val* actual_arg) {
    (void)actual_arg; // Mark as unused
    throw std::runtime_error("Cannot call a number as a function");
}

// ====================== BoolVal Implementation ======================

BoolVal::BoolVal(bool value) : value(value) {}

bool BoolVal::is_true() {
    return value;
}

std::string BoolVal::to_string() {
    return value ? "_true" : "_false";
}

Val* BoolVal::add_to(Val* other) {
    (void)other;
    throw std::runtime_error("Cannot add boolean values");
}

Val* BoolVal::mult_with(Val* other) {
    (void)other;
    throw std::runtime_error("Cannot multiply boolean values");
}

bool BoolVal::equals(Val* other) {
    BoolVal* otherBool = dynamic_cast<BoolVal*>(other);
    return otherBool && this->value == otherBool->value;
}

Expr* BoolVal::to_expr() {
    return new BoolExpr(value); // Convert BoolVal to BoolExpr
}

Val* BoolVal::call(Val* actual_arg) {
    (void)actual_arg; // Mark as unused
    throw std::runtime_error("Cannot call a boolean as a function");
}

// ====================== FunVal ======================

FunVal::FunVal(const std::string& formal_arg, Expr* body)
    : formal_arg(formal_arg), body(body) {}

bool FunVal::equals(Val* other) {
    FunVal* f = dynamic_cast<FunVal*>(other);
    return f && formal_arg == f->formal_arg && body->equals(f->body);
}

Expr* FunVal::to_expr() {
    return new FunExpr(formal_arg, body);
}

std::string FunVal::to_string() {
    return "[function]";
}

Val* FunVal::add_to(Val* other) {
    (void)other;
    throw std::runtime_error("Cannot add functions");
}

Val* FunVal::mult_with(Val* other) {
    (void)other;
    throw std::runtime_error("Cannot multiply functions");
}

bool FunVal::is_true() {
    throw std::runtime_error("Cannot use function as boolean");
}

Val* FunVal::call(Val* actual_arg) {
    Expr* substituted_body = body->subst(formal_arg, actual_arg->to_expr());
    return substituted_body->interp();
}