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

class Expr; // Forward declaration of Expr class

/**
 * @class Val
 * @brief Abstract base class for values.
 *
 * This class defines the interface for all value types. It includes pure virtual methods
 * that must be implemented by derived classes.
 */
class Val {
public:
    /**
     * @brief Checks if this value is equal to another value.
     *
     * @param other The value to compare with.
     * @return true if the values are equal, false otherwise.
     */
    virtual bool equals(Val* other) = 0;

    /**
     * @brief Converts this value to an expression.
     *
     * @return A pointer to an Expr object representing this value.
     */
    virtual Expr* to_expr() = 0;

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
    virtual Val* add_to(Val* other) = 0;

    /**
     * @brief Multiplies this value with another value.
     *
     * @param other The value to multiply with.
     * @return A pointer to a new Val object representing the result.
     */
    virtual Val* mult_with(Val* other) = 0;

    /**
     * @brief Checks if this value represents a boolean true.
     *
     * @return true if this value is a boolean true, false otherwise.
     * @throws std::runtime_error if this value is not a boolean.
     */
    virtual bool is_true() = 0;
};

/**
 * @class NumVal
 * @brief Represents a numeric value.
 *
 * This class represents a numeric value (integer).
 */
class NumVal : public Val {
    int value; // The numeric value

public:

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
    bool equals(Val* other) override; // Compare with another Val

    /**
     * @brief Converts this NumVal to a NumExpr object.
     *
     * @return A pointer to a new NumExpr object representing the same value.
     */
    Expr* to_expr() override; // Convert NumVal to NumExpr

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
    Val* add_to(Val* other) override; // Add NumVal to another Val

    /**
     * @brief Multiplies this NumVal with another Val object.
     *
     * @param other A pointer to the Val object to multiply with this NumVal.
     * @return A pointer to a new NumVal object representing the product.
     * @throws std::runtime_error If the other object is not a NumVal.
     */
    Val* mult_with(Val* other) override; // Multiply NumVal with another Val

    /**
     * @brief Throws an exception since numeric values cannot be used as booleans.
     *
     * @throws std::runtime_error Always throws an exception.
     */
    bool is_true() override;
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
    Val* add_to(Val* other) override;

    /**
     * @brief Throws an exception since boolean values cannot be multiplied.
     *
     * @throws std::runtime_error Always throws an exception.
     */
    Val* mult_with(Val* other) override;


    /**
     * @brief Checks if this BoolVal is equal to another Val object.
     *
     * @param other A pointer to the Val object to compare with.
     * @return true If the other object is a BoolVal and has the same value.
     * @return false Otherwise.
     */
    bool equals(Val* other) override;

    /**
     * @brief Converts this BoolVal to a BoolExpr object.
     *
     * @return A pointer to a new BoolExpr object representing the same value.
     */
    Expr* to_expr() override;
};

#endif // VAL_H