//
// Created by Brandon Mountan on 1/20/25.
//

#ifndef EXPR_H
#define EXPR_H

#include <string>
#include <stdexcept> // For std::runtime_error

// Abstract base class for expressions
class Expr {
public:
  virtual bool equals(const Expr* e) = 0; // Pure virtual method for equality check
  virtual int interp() = 0; // Pure virtual method to evaluate the expression
  virtual bool has_variable() = 0; // Pure virtual method to check for variables
  virtual Expr* subst(const std::string& var, Expr* replacement) = 0; // Pure virtual method for substitution
};

// Number expression
class NumExpr : public Expr {
  int value; // Stores the numeric value of this expression

public:
  NumExpr(int value); // Constructor to initialize the numeric value
  bool equals(const Expr* e); // Overridden equals method
  int interp(); // Overridden interp method
  bool has_variable(); // Overridden has_variable method
  Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method
};

// Addition expression
class AddExpr : public Expr {
  Expr* lhs; // Left-hand side expression
  Expr* rhs; // Right-hand side expression

public:
  AddExpr(Expr* lhs, Expr* rhs); // Constructor to initialize the operands
  bool equals(const Expr* e); // Overridden equals method
  int interp(); // Overridden interp method
  bool has_variable(); // Overridden has_variable method
  Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method
};

// Multiplication expression
class MultExpr : public Expr {
  Expr* lhs; // Left-hand side expression
  Expr* rhs; // Right-hand side expression

public:
  MultExpr(Expr* lhs, Expr* rhs); // Constructor to initialize the operands
  bool equals(const Expr* e); // Overridden equals method
  int interp(); // Overridden interp method
  bool has_variable(); // Overridden has_variable method
  Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method
};

// Variable expression
class VarExpr : public Expr {
  std::string name; // Stores the name of the variable

public:
  VarExpr(const std::string& name); // Constructor to initialize the variable name
  bool equals(const Expr* e) override; // Overridden equals method
  int interp() override; // Overridden interp method (throws an exception)
  bool has_variable() override; // Overridden has_variable method
  Expr* subst(const std::string& var, Expr* replacement) override; // Overridden subst method
};

#endif // EXPR_H
