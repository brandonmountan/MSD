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
#include <stdexcept> // For std::runtime_error
#include "pointer.h"
#include "expr.h"
#include "val.h"
#include "parse.hpp"
#include "env.h"

/**
 * @class Val
 * @brief Abstract base class for values.
 *
 * This class defines the interface for all value types. It includes pure virtual methods
 * that must be implemented by derived classes.
 */
CLASS(Val) {
public:
    virtual ~Val() = default;

    /**
     * @brief Checks if this value is equal to another value.
     *
     * @param other The value to compare with.
     * @return true if the values are equal, false otherwise.
     */
    virtual bool equals(PTR(Val) other) = 0;

    /**
     * @brief Converts this value to an expression.
     *
     * @return A pointer to an Expr object representing this value.
     */
    virtual PTR(Expr) to_expr() = 0;

    /**
     * @brief Converts this value to a string.
     *
     * @return A string representation of this value.
     */
    virtual std::string to_string() = 0;

    /**
     * @brief Adds this value to another value.
     *
     * @param other The value to add.
     * @return A pointer to a new Val object representing the result.
     */
    virtual PTR(Val) add_to(PTR(Val) other) = 0;

    /**
     * @brief Multiplies this value with another value.
     *
     * @param other The value to multiply with.
     * @return A pointer to a new Val object representing the result.
     */
    virtual PTR(Val) mult_with(PTR(Val) other) = 0;

    /**
     * @brief Checks if this value represents a boolean true.
     *
     * @return true if this value is a boolean true, false otherwise.
     * @throws std::runtime_error if this value is not a boolean.
     */
    virtual bool is_true() = 0;

    virtual PTR(Val) call(PTR(Val) actual_arg) = 0;

};

/**
 * @class NumVal
 * @brief Represents a numeric value.
 *
 * This class represents a numeric value (integer).
 */
class NumVal : public Val {

public:

    int value; // The numeric value


    /**
     * @brief Constructs a NumVal object with the given integer value.
     *
     * @param value The integer value to store in the NumVal object.
     */
    NumVal(int value);

    /**
     * @brief Checks if this NumVal is equal to another Val object.
     *
     * @param other A pointer to the Val object to compare with.
     * @return true If the other object is a NumVal and has the same value.
     * @return false Otherwise.
     */
    bool equals(PTR(Val) other) override; // Compare with another Val

    /**
     * @brief Converts this NumVal to a NumExpr object.
     *
     * @return A pointer to a new NumExpr object representing the same value.
     */
    PTR(Expr) to_expr() override; // Convert NumVal to NumExpr

    /**
     * @brief Converts this NumVal to a string representation.
     *
     * @return A string representation of the integer value.
     */
    std::string to_string() override; // Convert NumVal to string

    /**
     * @brief Adds this NumVal to another Val object.
     *
     * @param other A pointer to the Val object to add to this NumVal.
     * @return A pointer to a new NumVal object representing the sum.
     * @throws std::runtime_error If the other object is not a NumVal.
     */
    PTR(Val) add_to(PTR(Val) other) override; // Add NumVal to another Val

    /**
     * @brief Multiplies this NumVal with another Val object.
     *
     * @param other A pointer to the Val object to multiply with this NumVal.
     * @return A pointer to a new NumVal object representing the product.
     * @throws std::runtime_error If the other object is not a NumVal.
     */
    PTR(Val) mult_with(PTR(Val) other) override; // Multiply NumVal with another Val

    /**
     * @brief Throws an exception since numeric values cannot be used as booleans.
     *
     * @throws std::runtime_error Always throws an exception.
     */
    bool is_true() override;

    PTR(Val) call(PTR(Val) actual_arg) override;
};

/**
 * @class BoolVal
 * @brief Represents a boolean value.
 *
 * This class represents a boolean value (`_true` or `_false`).
 */
class BoolVal : public Val {
    bool value; // The boolean value

public:

    /**
     * @brief Constructs a BoolVal object with the given boolean value.
     *
     * @param value The boolean value to store in the BoolVal object.
     */
    BoolVal(bool value);

    /**
     * @brief Checks if this boolean value is true.
     *
     * @return true if this value is true, false otherwise.
     */
    bool is_true() override;

    /**
     * @brief Converts this BoolVal to a string representation.
     *
     * @return "_true" if true, "_false" if false.
     */
    std::string to_string() override;

    /**
     * @brief Throws an exception since boolean values cannot be added.
     *
     * @throws std::runtime_error Always throws an exception.
     */
    PTR(Val) add_to(PTR(Val) other) override;

    /**
     * @brief Throws an exception since boolean values cannot be multiplied.
     *
     * @throws std::runtime_error Always throws an exception.
     */
    PTR(Val) mult_with(PTR(Val) other) override;


    /**
     * @brief Checks if this BoolVal is equal to another Val object.
     *
     * @param other A pointer to the Val object to compare with.
     * @return true If the other object is a BoolVal and has the same value.
     * @return false Otherwise.
     */
    bool equals(PTR(Val) other) override;

    /**
     * @brief Converts this BoolVal to a BoolExpr object.
     *
     * @return A pointer to a new BoolExpr object representing the same value.
     */
    PTR(Expr) to_expr() override;

    PTR(Val) call(PTR(Val) actual_arg) override;
};

/**
 * @class FunVal
 * @brief Represents a function value in the interpreter.
 *
 * This class encapsulates a function as a value that can be stored in variables,
 * passed as arguments, and returned from other functions. It maintains the
 * function's formal argument and body expression for later application.
 */
class FunVal : public Val {
    std::string formal_arg; ///< The name of the function's formal parameter
    PTR(Expr) body;            ///< The function's body expression (unevaluated)
    PTR(Env) env;
public:
    /**
     * @brief Constructs a function value.
     *
     * @param formal_arg The name of the formal parameter for this function.
     * @param body The expression representing the function body.
     */
    FunVal(const std::string& formal_arg, PTR(Expr) body, PTR(Env) env);

    /**
     * @brief Checks if this function value equals another value.
     *
     * Two function values are considered equal if they have the same formal
     * argument name and their bodies are structurally equal.
     *
     * @param other The value to compare with this function value.
     * @return true if the values represent the same function, false otherwise.
     */
    bool equals(PTR(Val) other) override;

    /**
     * @brief Converts this function value back to an expression.
     *
     * @return A new FunExpr representing this function value.
     */
    PTR(Expr) to_expr() override;

    /**
     * @brief Returns a string representation of the function value.
     *
     * Note: Since functions don't have a literal syntax, this returns a
     * generic indicator rather than reconstructing the original syntax.
     *
     * @return The string "[function]".
     */
    std::string to_string() override;

    /**
     * @brief Throws an error since functions cannot be added.
     *
     * @param other The value to attempt to add (ignored).
     * @throws std::runtime_error Always throws, as function addition is undefined.
     */
    PTR(Val) add_to(PTR(Val) other) override;

    /**
     * @brief Throws an error since functions cannot be multiplied.
     *
     * @param other The value to attempt to multiply with (ignored).
     * @throws std::runtime_error Always throws, as function multiplication is undefined.
     */
    PTR(Val) mult_with(PTR(Val) other) override;

    /**
     * @brief Throws an error since functions cannot be used as booleans.
     *
     * @throws std::runtime_error Always throws, as functions are not booleans.
     */
    bool is_true() override;

    /**
     * @brief Applies the function to an argument value.
     *
     * Implements function calling by:
     * 1. Substituting the formal argument in the body with the actual argument
     * 2. Evaluating the resulting expression
     *
     * @param actual_arg The value to apply the function to.
     * @return The result of evaluating the function body with the argument substituted.
     */
    PTR(Val) call(PTR(Val) actual_arg) override;
};

#endif // VAL_H