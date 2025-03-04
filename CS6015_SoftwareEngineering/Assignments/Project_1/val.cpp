//
// Created by Brandon Mountan on 3/4/25.
//

#include "val.h"
#include "expr.h" // Include expr.h for Expr and NumExpr

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