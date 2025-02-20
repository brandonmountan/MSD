#include "expr.h"
#include <stdexcept>
#include <sstream>

// ====================== Expr ======================

std::string Expr::to_string() {
    std::stringstream st("");
    this->printExp(st);
    return st.str();
}

std::string Expr::to_pretty_string() {
    std::stringstream st;
    this->pretty_print(st, prec_none);
    return st.str();
}

void Expr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
	printExp(ot);
}

void Expr::pretty_print(std::ostream& ot, precedence_t prec) {
    // Default implementation: just call printExp
    std::streampos last_newline_pos = ot.tellp();
    pretty_print_at(ot, prec, last_newline_pos);
}

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
    lhs->printExp(ot);
    ot << "+";
    rhs->printExp(ot);
    ot << ")";
}

void AddExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
  bool use_parentheses = (prec >= prec_add);
  if (use_parentheses) {
    ot << "(";
  }
  lhs->pretty_print_at(ot, prec_add, last_newline_pos);
  ot << " + ";
  rhs->pretty_print_at(ot, prec_none, last_newline_pos);
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
    lhs->printExp(ot);
    ot << "*";
    rhs->printExp(ot);
    ot << ")";
}

void MultExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
  bool use_parentheses = (prec >= prec_mult);
  if (use_parentheses) {
    ot << "(";
  }
  lhs->pretty_print_at(ot, prec_mult, last_newline_pos);
  ot << " * ";
  rhs->pretty_print_at(ot, prec_add, last_newline_pos);
  if (use_parentheses) {
    ot << ")";
  }
}

//void MultExpr::pretty_print(std::ostream& ot, precedence_t prec) {
//    std::streampos last_newline_pos = ot.tellp();
//    pretty_print_at(ot, prec, last_newline_pos);
//}



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

// ====================== LetExpr ======================

LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body) {
    this->var = var;
    this->rhs = rhs;
    this->body = body;
}

bool LetExpr::equals(const Expr* e) {
    const LetExpr* letExpr = dynamic_cast<const LetExpr*>(e);
    return letExpr && this->var == letExpr->var &&
           this->rhs->equals(letExpr->rhs) &&
           this->body->equals(letExpr->body);
}

int LetExpr::interp() {
    int rhsValue = rhs->interp();
    Expr* substitutedBody = body->subst(var, new NumExpr(rhsValue));
    return substitutedBody->interp();
}

bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable();
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
    ot << "(_let " << var << "=";
    rhs->printExp(ot);
    ot << " _in ";
    body->printExp(ot);
    ot << ")";
}

void LetExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp();
    pretty_print_at(ot, prec, last_newline_pos);
}

void LetExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec != prec_none);

    if (needs_parentheses) {
        ot << "(";
    }

    std::streampos position1 = last_newline_pos;
    std::streampos current_pos = ot.tellp();

    // Print the _let part
    ot << "_let " << var << " = ";
    rhs->pretty_print(ot, prec_none);

    // Track the position after the newline
    ot << "\n";
    last_newline_pos = ot.tellp();

    // Calculate the indentation for _in
    int indent = static_cast<int>(current_pos - last_newline_pos);
    for (int i = position1; i < current_pos; i++) {
        ot << " ";
    }

    // Print the _in part with proper indentation
    ot << "_in  ";

    // Print the body with proper context
    body->pretty_print_at(ot, prec_none, last_newline_pos);

    if (needs_parentheses) {
      ot << ")";
    }
}