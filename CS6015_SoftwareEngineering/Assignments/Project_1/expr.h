//
// Created by Brandon Mountan on 1/20/25.
//

#ifndef EXPR_H
#define EXPR_H

#include <string>

// Abstract base class for expressions
class Expr {
public:
  virtual bool equals(const Expr* e) = 0; // Pure virtual method for equality check
};

// Number expression
class NumExpr : public Expr {
  int value; // Stores the numeric value of this expression

public:
  NumExpr(int value); // Constructor to initialize the numeric value
  bool equals(const Expr* e); // Overridden equals method
};

// Addition expression
class AddExpr : public Expr {
  Expr* lhs; // Left-hand side expression
  Expr* rhs; // Right-hand side expression

public:
  AddExpr(Expr* lhs, Expr* rhs); // Constructor to initialize the operands
  bool equals(const Expr* e); // Overridden equals method
};

// Multiplication expression
class MultExpr : public Expr {
  Expr* lhs; // Left-hand side expression
  Expr* rhs; // Right-hand side expression

public:
  MultExpr(Expr* lhs, Expr* rhs); // Constructor to initialize the operands
  bool equals(const Expr* e); // Overridden equals method
};

// Variable expression
class VarExpr : public Expr {
  std::string name; // Stores the name of the variable

public:
  VarExpr(const std::string& name); // Constructor to initialize the variable name
  bool equals(const Expr* e); // Overridden equals method
};

#endif // EXPR_H
