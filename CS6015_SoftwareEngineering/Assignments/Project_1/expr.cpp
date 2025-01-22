//
// Created by Brandon Mountan on 1/20/25.
//
#include "expr.h"

// Implementation for NumExpr
NumExpr::NumExpr(int value){
  this->value = value;
}

bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e); // Check if e is a NumExpr
    return numExpr && this->value == numExpr->value; // Compare values if cast is successful
}

// Implementation for AddExpr
AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Assign the left-hand side expression to the member variable
    this->rhs = rhs; // Assign the right-hand side expression to the member variable
}

bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e); // Check if e is an AddExpr
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs); // Compare operands
}

// Implementation for MultExpr
MultExpr::MultExpr(Expr* lhs, Expr* rhs){
  this->lhs = lhs;
  this->rhs = rhs;
}

bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e); // Check if e is a MultExpr
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs); // Compare operands
}

// Implementation for VarExpr
VarExpr::VarExpr(const std::string& name){
  this->name = name;
}

bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e); // Check if e is a VarExpr
    return varExpr && this->name == varExpr->name; // Compare variable names
}