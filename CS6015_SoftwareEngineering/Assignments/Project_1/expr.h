//
// Created by Brandon Mountan on 1/20/25.
//

#ifndef EXPR_H
#define EXPR_H

#include <string>
#include <stdexcept> // For std::runtime_error
#include <sstream>   // For std::stringstream

// Enum for precedence levels in pretty printing
typedef enum {
    prec_none,      // = 0
    prec_add,       // = 1
    prec_mult       // = 2
} precedence_t;

// Abstract base class for expressions
class Expr {
public:
    virtual bool equals(const Expr* e) = 0; // Pure virtual method for equality check
    virtual int interp() = 0;              // Pure virtual method to evaluate the expression
    virtual bool has_variable() = 0;       // Pure virtual method to check for variables
    virtual Expr* subst(const std::string& var, Expr* replacement) = 0; // Pure virtual method for substitution

    // New methods
    virtual void printExp(std::ostream& ot) = 0; // Pure virtual method to print the expression
    std::string to_string();                     // Non-virtual method to convert expression to string
    virtual void pretty_print(std::ostream& ot, precedence_t prec) = 0; // Virtual method for pretty printing
    std::string to_pretty_string();

};

// Number expression
class NumExpr : public Expr {
    int value; // Stores the numeric value of this expression

public:
    NumExpr(int value); // Constructor to initialize the numeric value
    bool equals(const Expr* e); // Overridden equals method
    int interp();               // Overridden interp method
    bool has_variable();        // Overridden has_variable method
    Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method

    // New methods
    void printExp(std::ostream& ot); // Overridden printExp method
    void pretty_print(std::ostream& ot, precedence_t prec);      // Overridden pretty_print method
};

// Addition expression
class AddExpr : public Expr {
    Expr* lhs; // Left-hand side expression
    Expr* rhs; // Right-hand side expression

public:
    AddExpr(Expr* lhs, Expr* rhs); // Constructor to initialize the operands
    bool equals(const Expr* e); // Overridden equals method
    int interp();               // Overridden interp method
    bool has_variable();        // Overridden has_variable method
    Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method

    // New methods
    void printExp(std::ostream& ot); // Overridden printExp method
    void pretty_print(std::ostream& ot, precedence_t prec);
};

// Multiplication expression
class MultExpr : public Expr {
    Expr* lhs; // Left-hand side expression
    Expr* rhs; // Right-hand side expression

public:
    MultExpr(Expr* lhs, Expr* rhs); // Constructor to initialize the operands
    bool equals(const Expr* e); // Overridden equals method
    int interp();               // Overridden interp method
    bool has_variable();        // Overridden has_variable method
    Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method

    // New methods
    void printExp(std::ostream& ot); // Overridden printExp method
	void pretty_print(std::ostream& ot, precedence_t prec);
};

// Variable expression
class VarExpr : public Expr {
    std::string name; // Stores the name of the variable

public:
    VarExpr(const std::string& name); // Constructor to initialize the variable name
    bool equals(const Expr* e); // Overridden equals method
    int interp();               // Overridden interp method (throws an exception)
    bool has_variable() ;        // Overridden has_variable method
    Expr* subst(const std::string& var, Expr* replacement); // Overridden subst method

    // New methods
    void printExp(std::ostream& ot); // Overridden printExp method
    void pretty_print(std::ostream& ot, precedence_t prec);      // Overridden pretty_print method
};

#endif // EXPR_H