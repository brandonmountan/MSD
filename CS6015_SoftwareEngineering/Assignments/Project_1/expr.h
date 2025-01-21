//
// Created by Brandon Mountan on 1/20/25.
//
#ifndef EXPR_H
#define EXPR_H

// Include necessary libraries
#include <string>  // For std::string to represent variable names
#include <memory>  // For std::shared_ptr to manage expression objects dynamically

// Abstract base class for expressions
class Expr {
public:
  // Virtual destructor to ensure proper cleanup of derived class objects
  virtual ~Expr() {}

  // Pure virtual method for checking equality between expressions
  // Each subclass must implement this method
  virtual bool equals(std::shared_ptr<Expr> e) const = 0;
};

// Class representing a numeric expression (e.g., a single integer value)
class NumExpr : public Expr {
  int value;  // The numeric value of the expression

public:
  // Constructor to initialize the numeric value
  explicit NumExpr(int value);

  // Implementation of the equals method to compare numeric expressions
  bool equals(std::shared_ptr<Expr> e) const override;
};

// Class representing an addition expression (e.g., lhs + rhs)
class AddExpr : public Expr {
  // Shared pointers to the left-hand side and right-hand side expressions
  std::shared_ptr<Expr> lhs, rhs;

public:
  // Constructor to initialize the left and right expressions
  AddExpr(std::shared_ptr<Expr> lhs, std::shared_ptr<Expr> rhs);

  // Implementation of the equals method to compare addition expressions
  bool equals(std::shared_ptr<Expr> e) const override;
};

// Class representing a multiplication expression (e.g., lhs * rhs)
class MultExpr : public Expr {
  // Shared pointers to the left-hand side and right-hand side expressions
  std::shared_ptr<Expr> lhs, rhs;

public:
  // Constructor to initialize the left and right expressions
  MultExpr(std::shared_ptr<Expr> lhs, std::shared_ptr<Expr> rhs);

  // Implementation of the equals method to compare multiplication expressions
  bool equals(std::shared_ptr<Expr> e) const override;
};

// Class representing a variable expression (e.g., a variable like "x" or "y")
class VarExpr : public Expr {
  std::string name;  // Name of the variable

public:
  // Constructor to initialize the variable's name
  explicit VarExpr(const std::string &name);

  // Implementation of the equals method to compare variable expressions
  bool equals(std::shared_ptr<Expr> e) const override;
};

// End of include guard
#endif // EXPR_H
