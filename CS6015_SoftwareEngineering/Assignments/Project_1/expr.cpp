// expr.cpp
#include "expr.h"
#include <stdexcept>
#include <sstream>

// ====================== NumExpr ======================

NumExpr::NumExpr(int value) {
    this->value = value;
}

int NumExpr::interp() {
    return value;
}

bool NumExpr::has_variable() {
    return false;
}

Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    return this;
}

bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e);
    return numExpr && this->value == numExpr->value;
}

void NumExpr::printExp(std::ostream& ot) {
    ot << value;
}

void NumExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    ot << value;
}

// ====================== AddExpr ======================

AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

int AddExpr::interp() {
    return lhs->interp() + rhs->interp();
}

bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e);
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs);
}

void AddExpr::printExp(std::ostream& ot) {
  	ot << "(";
    this->lhs->printExp(ot);
    ot << "+";
    this->rhs->printExp(ot);
    ot << ")";
}

void AddExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    bool use_parentheses = (prec >= prec_add);

    if (use_parentheses) {
        ot << "(";
    }
    lhs -> pretty_print(ot, prec_add);
    ot << " + ";
    rhs -> pretty_print(ot, prec_none);
    if (use_parentheses) {
        ot << ")";
    }
}

// ====================== MultExpr ======================

MultExpr::MultExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

int MultExpr::interp() {
    return lhs->interp() * rhs->interp();
}

bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable();
}

Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e);
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs);
}

void MultExpr::printExp(std::ostream& ot) {
    ot << "(";
    this->lhs->printExp(ot);
    ot << "*";
    this->rhs->printExp(ot);
    ot << ")";
}

void MultExpr::pretty_print(std::ostream& ot, precedence_t prec) {
  	bool use_parentheses = (prec >= prec_mult);
    if (use_parentheses) {
        ot << "(";
    }
    lhs -> pretty_print(ot, prec_mult);
    ot << " * ";
    rhs -> pretty_print(ot, prec_add);
    if (use_parentheses) {
        ot << ")";
    }
}
// ====================== VarExpr ======================

VarExpr::VarExpr(const std::string& name) {
    this->name = name;
}

int VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

bool VarExpr::has_variable() {
    return true;
}

Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (this->name == var) {
        return replacement;
    }
    return this;
}

bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e);
    return varExpr && this->name == varExpr->name;
}

void VarExpr::printExp(std::ostream& ot) {
    ot << name;
}

void VarExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    ot << name;
}

// ====================== Expr ======================

std::string Expr::to_string() {
    std::stringstream st;
    this->printExp(st);
    return st.str();
}

std::string Expr::to_pretty_string() {
    std::stringstream st;
    this->pretty_print(st, prec_none);
    return st.str();
}
