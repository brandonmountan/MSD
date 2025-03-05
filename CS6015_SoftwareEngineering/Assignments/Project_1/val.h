//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/04/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#ifndef VAL_H
#define VAL_H

#include <string>

class Expr; // Forward declaration of Expr class

class Val {
public:
    virtual bool equals(Val* other) = 0; // Compare two Val objects
    virtual Expr* to_expr() = 0; // Convert Val to Expr
    virtual std::string to_string() = 0; // Convert Val to string
    virtual Val* add_to(Val* other) = 0; // Add two Val objects
    virtual Val* mult_with(Val* other) = 0; // Multiply two Val objects
};

class NumVal : public Val {
    int value; // The numeric value
public:
    NumVal(int value); // Constructor
    bool equals(Val* other) override; // Compare with another Val
    Expr* to_expr() override; // Convert NumVal to NumExpr
    std::string to_string() override; // Convert NumVal to string
    Val* add_to(Val* other) override; // Add NumVal to another Val
    Val* mult_with(Val* other) override; // Multiply NumVal with another Val
};

#endif // VAL_H