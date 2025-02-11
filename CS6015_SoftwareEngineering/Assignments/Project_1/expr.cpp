#include "expr.h"
#include <stdexcept>
#include <sstream>

// ====================== NumExpr ======================

NumExpr::NumExpr(int value) : value(value) {}

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
    return numExpr && value == numExpr->value;
}

void NumExpr::printExp(std::ostream& ot) {
    ot << value;
}

void NumExpr::pretty_print(std::ostream& ot, precedence_t prec, std::streampos& newline_pos) {
    ot << value;
}

// ====================== AddExpr ======================

AddExpr::AddExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

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
    lhs->printExp(ot);
    ot << "+";
    rhs->printExp(ot);
    ot << ")";
}

void AddExpr::pretty_print(std::ostream& ot, precedence_t prec, std::streampos& newline_pos) {
    bool use_parentheses = (prec > prec_add);
    if (use_parentheses) ot << "(";
    lhs->pretty_print(ot, prec_add, newline_pos);
    ot << " + ";
    rhs->pretty_print(ot, prec_none, newline_pos);
    if (use_parentheses) ot << ")";
}

// ====================== MultExpr ======================

MultExpr::MultExpr(Expr* lhs, Expr* rhs) : lhs(lhs), rhs(rhs) {}

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
    lhs->printExp(ot);
    ot << "*";
    rhs->printExp(ot);
    ot << ")";
}

void MultExpr::pretty_print(std::ostream& ot, precedence_t prec, std::streampos& newline_pos) {
    bool use_parentheses = (prec > prec_mult);
    if (use_parentheses) ot << "(";
    lhs->pretty_print(ot, prec_mult, newline_pos);
    ot << " * ";
    rhs->pretty_print(ot, prec_add, newline_pos);
    if (use_parentheses) ot << ")";
}

// ====================== VarExpr ======================

VarExpr::VarExpr(const std::string& name) : name(name) {}

int VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

bool VarExpr::has_variable() {
    return true;
}

Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (name == var) {
        return replacement;
    }
    return this;
}

bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e);
    return varExpr && name == varExpr->name;
}

void VarExpr::printExp(std::ostream& ot) {
    ot << name;
}

void VarExpr::pretty_print(std::ostream& ot, precedence_t prec, std::streampos& newline_pos) {
    ot << name;
}

// ====================== LetExpr ======================

LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body) : var(var), rhs(rhs), body(body) {}

int LetExpr::interp() {
    Expr* substituted_body = body->subst(var, new NumExpr(rhs->interp()));
    return substituted_body->interp();
}

bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable();
}

Expr* LetExpr::subst(const std::string& var, Expr* replacement) {
    if (this->var == var) {
        return new LetExpr(var, rhs->subst(var, replacement), body);
    }
    return new LetExpr(var, rhs->subst(var, replacement), body->subst(var, replacement));
}

bool LetExpr::equals(const Expr* e) {
    const LetExpr* letExpr = dynamic_cast<const LetExpr*>(e);
    return letExpr && var == letExpr->var && rhs->equals(letExpr->rhs) && body->equals(letExpr->body);
}

void LetExpr::printExp(std::ostream& ot) {
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    body->printExp(ot);
    ot << ")";
}

void LetExpr::pretty_print(std::ostream& ot, precedence_t prec, std::streampos& newline_pos) {
    bool use_parentheses = (prec > prec_let);
    if (use_parentheses) ot << "(";

    // Print _let and the variable binding
    ot << "_let " << var << " = ";
    rhs->pretty_print(ot, prec_none, newline_pos);

    // Track the position of the newline after _let
    newline_pos = ot.tellp();
    ot << "\n";

    // Print _in and the body with proper indentation
    ot << std::string(newline_pos, ' ') << "_in ";
    body->pretty_print(ot, prec_none, newline_pos);

    if (use_parentheses) ot << ")";
}

// ====================== Expr ======================

std::string Expr::to_string() {
    std::stringstream st;
    this->printExp(st);
    return st.str();
}

std::string Expr::to_pretty_string() {
    std::stringstream st;
    std::streampos newline_pos = 0;
    this->pretty_print(st, prec_none, newline_pos);
    return st.str();
}