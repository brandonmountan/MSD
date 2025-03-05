//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/20/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "expr.h"

// NumExpr implementation
NumExpr::NumExpr(int val) : val(val) {}

bool NumExpr::equals(Expr* e) {
    if (NumExpr* num = dynamic_cast<NumExpr*>(e)) {
        return val == num->val;
    }
    return false;
}

// AddExpr implementation
AddExpr::AddExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool AddExpr::equals(Expr* e) {
    if (AddExpr* add = dynamic_cast<AddExpr*>(e)) {
        return lhs->equals(add->lhs) && rhs->equals(add->rhs);
    }
    return false;
}

// MultExpr implementation
MultExpr::MultExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

bool MultExpr::equals(Expr* e) {
    if (MultExpr* mult = dynamic_cast<MultExpr*>(e)) {
        return lhs->equals(mult->lhs) && rhs->equals(mult->rhs);
    }
    return false;
}

// VarExpr implementation
VarExpr::VarExpr(std::string name) : name(name) {}

bool VarExpr::equals(Expr* e) {
    if (VarExpr* var = dynamic_cast<VarExpr*>(e)) {
        return name == var->name;
    }
    return false;
}