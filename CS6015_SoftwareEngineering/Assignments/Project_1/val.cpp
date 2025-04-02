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
#include "pointer.h"

// ====================== NumVal Implementation ======================

NumVal::NumVal(int value) : value(value) {}

bool NumVal::equals(PTR(Val) other) {
    PTR(NumVal) otherNum = CAST(NumVal)(other);
    return otherNum && value == otherNum->value;
}

PTR(Expr) NumVal::to_expr() {
    return NEW(NumExpr)(value); // Convert NumVal to NumExpr
}

std::string NumVal::to_string() {
    return std::to_string(value); // Convert NumVal to string
}

PTR(Val) NumVal::add_to(PTR(Val) other) {
    PTR(NumVal) otherNum = CAST(NumVal)(other);
    if (!otherNum) {
        throw std::runtime_error("Cannot add non-numeric values");
    }
    return NEW(NumVal)(value + otherNum->value);
}

PTR(Val) NumVal::mult_with(PTR(Val) other) {
    PTR(NumVal) otherNum = CAST(NumVal)(other);
    if (!otherNum) {
        throw std::runtime_error("Cannot multiply non-numeric values");
    }
    return NEW(NumVal)(value * otherNum->value);
}

bool NumVal::is_true() {
    throw std::runtime_error("Cannot use a numeric value as a boolean");
}

PTR(Val) NumVal::call(PTR(Val) actual_arg) {
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

PTR(Val) BoolVal::add_to(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("Cannot add boolean values");
}

PTR(Val) BoolVal::mult_with(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("Cannot multiply boolean values");
}

bool BoolVal::equals(PTR(Val) other) {
    PTR(BoolVal) otherBool = CAST(BoolVal)(other);
    return otherBool && value == otherBool->value;
}

PTR(Expr) BoolVal::to_expr() {
    return NEW(BoolExpr)(value); // Convert BoolVal to BoolExpr
}

PTR(Val) BoolVal::call(PTR(Val) actual_arg) {
    (void)actual_arg; // Mark as unused
    throw std::runtime_error("Cannot call a boolean as a function");
}

// ====================== FunVal ======================

FunVal::FunVal(const std::string& formal_arg, PTR(Expr) body)
    : formal_arg(formal_arg), body(body) {}

bool FunVal::equals(PTR(Val) other) {
    PTR(FunVal) f = CAST(FunVal)(other);
    return f && formal_arg == f->formal_arg && body->equals(f->body);
}

PTR(Expr) FunVal::to_expr() {
    return NEW(FunExpr)(formal_arg, body);
}

std::string FunVal::to_string() {
    return "[function]";
}

PTR(Val) FunVal::add_to(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("Cannot add functions");
}

PTR(Val) FunVal::mult_with(PTR(Val) other) {
    (void)other;
    throw std::runtime_error("Cannot multiply functions");
}

bool FunVal::is_true() {
    throw std::runtime_error("Cannot use function as boolean");
}

PTR(Val) FunVal::call(PTR(Val) actual_arg) {
    PTR(Expr) substituted_body = body->subst(formal_arg, actual_arg->to_expr());
    return substituted_body->interp();
}