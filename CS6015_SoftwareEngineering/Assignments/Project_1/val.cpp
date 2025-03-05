//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   03/04/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "val.h"
#include "expr.h" // Include expr.h for Expr and NumExpr
#include <stdexcept> // For std::runtime_error

// ====================== NumVal Implementation ======================

/**
 * @brief Constructs a NumVal object with the given integer value.
 *
 * @param value The integer value to store in the NumVal object.
 */
NumVal::NumVal(int value) : value(value) {}

/**
 * @brief Checks if this NumVal is equal to another Val object.
 *
 * @param other A pointer to the Val object to compare with.
 * @return true If the other object is a NumVal and has the same value.
 * @return false Otherwise.
 */
bool NumVal::equals(Val* other) {
    NumVal* otherNum = dynamic_cast<NumVal*>(other);
    return otherNum && this->value == otherNum->value;
}

/**
 * @brief Converts this NumVal to a NumExpr object.
 *
 * @return A pointer to a new NumExpr object representing the same value.
 */
Expr* NumVal::to_expr() {
    return new NumExpr(value); // Convert NumVal to NumExpr
}

/**
 * @brief Converts this NumVal to a string representation.
 *
 * @return A string representation of the integer value.
 */
std::string NumVal::to_string() {
    return std::to_string(value); // Convert NumVal to string
}

/**
 * @brief Adds this NumVal to another Val object.
 *
 * @param other A pointer to the Val object to add to this NumVal.
 * @return A pointer to a new NumVal object representing the sum.
 * @throws std::runtime_error If the other object is not a NumVal.
 */
Val* NumVal::add_to(Val* other) {
    NumVal* otherNum = dynamic_cast<NumVal*>(other);
    if (!otherNum) {
        throw std::runtime_error("Cannot add non-numeric values");
    }
    return new NumVal(this->value + otherNum->value);
}

/**
 * @brief Multiplies this NumVal with another Val object.
 *
 * @param other A pointer to the Val object to multiply with this NumVal.
 * @return A pointer to a new NumVal object representing the product.
 * @throws std::runtime_error If the other object is not a NumVal.
 */
Val* NumVal::mult_with(Val* other) {
    NumVal* otherNum = dynamic_cast<NumVal*>(other);
    if (!otherNum) {
        throw std::runtime_error("Cannot multiply non-numeric values");
    }
    return new NumVal(this->value * otherNum->value);
}

/**
 * @brief Throws an exception since numeric values cannot be used as booleans.
 *
 * @throws std::runtime_error Always throws an exception.
 */
bool NumVal::is_true() {
    throw std::runtime_error("Cannot use a numeric value as a boolean");
}

// ====================== BoolVal Implementation ======================

/**
 * @brief Constructs a BoolVal object with the given boolean value.
 *
 * @param value The boolean value to store in the BoolVal object.
 */
BoolVal::BoolVal(bool value) : value(value) {}

/**
 * @brief Checks if this boolean value is true.
 *
 * @return true If this value is true, false otherwise.
 */
bool BoolVal::is_true() {
    return value;
}

/**
 * @brief Converts this BoolVal to a string representation.
 *
 * @return "_true" if true, "_false" if false.
 */
std::string BoolVal::to_string() {
    return value ? "_true" : "_false";
}

/**
 * @brief Throws an exception since boolean values cannot be added.
 *
 * @throws std::runtime_error Always throws an exception.
 */
Val* BoolVal::add_to(Val* other) {
    (void)other;
    throw std::runtime_error("Cannot add boolean values");
}

/**
 * @brief Throws an exception since boolean values cannot be multiplied.
 *
 * @throws std::runtime_error Always throws an exception.
 */
Val* BoolVal::mult_with(Val* other) {
    (void)other;
    throw std::runtime_error("Cannot multiply boolean values");
}

/**
 * @brief Checks if this BoolVal is equal to another Val object.
 *
 * @param other A pointer to the Val object to compare with.
 * @return true If the other object is a BoolVal and has the same value.
 * @return false Otherwise.
 */
bool BoolVal::equals(Val* other) {
    BoolVal* otherBool = dynamic_cast<BoolVal*>(other);
    return otherBool && this->value == otherBool->value;
}

/**
 * @brief Converts this BoolVal to a BoolExpr object.
 *
 * @return A pointer to a new BoolExpr object representing the same value.
 */
Expr* BoolVal::to_expr() {
    return new BoolExpr(value); // Convert BoolVal to BoolExpr
}