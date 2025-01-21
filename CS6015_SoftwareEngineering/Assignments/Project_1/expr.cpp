//
// Created by Brandon Mountan on 1/20/25.
//
#include "expr.h" // Include the header file where the class declarations are located

// Implementation of NumExpr

// Constructor: Initializes the NumExpr with a given integer value
NumExpr::NumExpr(int value) : value(value) {}

// Implementation of the equals method for NumExpr
// Compares this NumExpr with another expression to check if they are equal
bool NumExpr::equals(std::shared_ptr<Expr> e) const {
    // Attempt to cast the input expression to a NumExpr
    auto numExpr = std::dynamic_pointer_cast<NumExpr>(e);
    // Return true if the cast is successful and the values are equal
    return numExpr && this->value == numExpr->value;
}

// Implementation of AddExpr

// Constructor: Initializes AddExpr with left-hand side (lhs) and right-hand side (rhs) expressions
AddExpr::AddExpr(std::shared_ptr<Expr> lhs, std::shared_ptr<Expr> rhs) : lhs(lhs), rhs(rhs) {}

// Implementation of the equals method for AddExpr
// Compares this AddExpr with another expression to check if they are equal
bool AddExpr::equals(std::shared_ptr<Expr> e) const {
    // Attempt to cast the input expression to an AddExpr
    auto addExpr = std::dynamic_pointer_cast<AddExpr>(e);
    // Return true if the cast is successful and both lhs and rhs are equal
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs);
}

// Implementation of MultExpr

// Constructor: Initializes MultExpr with left-hand side (lhs) and right-hand side (rhs) expressions
MultExpr::MultExpr(std::shared_ptr<Expr> lhs, std::shared_ptr<Expr> rhs) : lhs(lhs), rhs(rhs) {}

// Implementation of the equals method for MultExpr
// Compares this MultExpr with another expression to check if they are equal
bool MultExpr::equals(std::shared_ptr<Expr> e) const {
    // Attempt to cast the input expression to a MultExpr
    auto multExpr = std::dynamic_pointer_cast<MultExpr>(e);
    // Return true if the cast is successful and both lhs and rhs are equal
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs);
}

// Implementation of VarExpr

// Constructor: Initializes VarExpr with a given variable name
VarExpr::VarExpr(const std::string &name) : name(name) {}

// Implementation of the equals method for VarExpr
// Compares this VarExpr with another expression to check if they are equal
bool VarExpr::equals(std::shared_ptr<Expr> e) const {
    // Attempt to cast the input expression to a VarExpr
    auto varExpr = std::dynamic_pointer_cast<VarExpr>(e);
    // Return true if the cast is successful and the variable names are equal
    return varExpr && this->name == varExpr->name;
}
