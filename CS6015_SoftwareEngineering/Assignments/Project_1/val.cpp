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