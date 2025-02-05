//
// Created by Brandon Mountan on 1/20/25.
//

#ifndef EXPR_H
#define EXPR_H

#include <string>
#include <stdexcept> // For std::runtime_error
#include <sstream>   // For std::stringstream

/**
 * @enum precedence_t
 * @brief Enumeration for precedence levels in pretty printing.
 *
 * This enumeration defines the precedence levels used to determine the order of operations
 * when pretty-printing expressions.
 */
typedef enum {
    prec_none,      ///< No precedence (default).
    prec_add,       ///< Precedence level for addition.
    prec_mult       ///< Precedence level for multiplication.
} precedence_t;

/**
 * @class Expr
 * @brief Abstract base class for expressions.
 *
 * This class defines the interface for all expression types. It includes pure virtual methods
 * that must be implemented by derived classes.
 */
class Expr {
public:
    /**
     * @brief Checks if this expression is equal to another expression.
     *
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    virtual bool equals(const Expr* e) = 0;

    /**
     * @brief Evaluates the expression to an integer value.
     *
     * @return The result of evaluating the expression.
     */
    virtual int interp() = 0;

    /**
     * @brief Checks if the expression contains a variable.
     *
     * @return true if the expression contains a variable, false otherwise.
     */
    virtual bool has_variable() = 0;

    /**
     * @brief Substitutes a variable with another expression.
     *
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new expression with the substitution applied.
     */
    virtual Expr* subst(const std::string& var, Expr* replacement) = 0;

    /**
     * @brief Prints the expression to an output stream.
     *
     * @param ot The output stream to print to.
     */
    virtual void printExp(std::ostream& ot) = 0;

    /**
     * @brief Converts the expression to a string.
     *
     * @return A string representation of the expression.
     */
    std::string to_string();

    /**
     * @brief Pretty-prints the expression to an output stream with proper precedence handling.
     *
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     */
    virtual void pretty_print(std::ostream& ot, precedence_t prec) = 0;

    /**
     * @brief Converts the expression to a pretty-printed string.
     *
     * @return A pretty-printed string representation of the expression.
     */
    std::string to_pretty_string();
};

/**
 * @class NumExpr
 * @brief Represents a numeric expression.
 *
 * This class represents an expression that consists of a single numeric value.
 */
class NumExpr : public Expr {
    int value; ///< The numeric value of this expression.

public:
    /**
     * @brief Constructs a numeric expression.
     *
     * @param value The numeric value.
     */
    NumExpr(int value);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;

    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec) override;
};

/**
 * @class AddExpr
 * @brief Represents an addition expression.
 *
 * This class represents an expression that adds two sub-expressions.
 */
class AddExpr : public Expr {
    Expr* lhs; ///< The left-hand side expression.
    Expr* rhs; ///< The right-hand side expression.

public:
    /**
     * @brief Constructs an addition expression.
     *
     * @param lhs The left-hand side expression.
     * @param rhs The right-hand side expression.
     */
    AddExpr(Expr* lhs, Expr* rhs);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;

    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec) override;
};

/**
 * @class MultExpr
 * @brief Represents a multiplication expression.
 *
 * This class represents an expression that multiplies two sub-expressions.
 */
class MultExpr : public Expr {
    Expr* lhs; ///< The left-hand side expression.
    Expr* rhs; ///< The right-hand side expression.

public:
    /**
     * @brief Constructs a multiplication expression.
     *
     * @param lhs The left-hand side expression.
     * @param rhs The right-hand side expression.
     */
    MultExpr(Expr* lhs, Expr* rhs);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;

    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec) override;
};

/**
 * @class VarExpr
 * @brief Represents a variable expression.
 *
 * This class represents an expression that consists of a variable.
 */
class VarExpr : public Expr {
    std::string name; ///< The name of the variable.

public:
    /**
     * @brief Constructs a variable expression.
     *
     * @param name The name of the variable.
     */
    VarExpr(const std::string& name);

    bool equals(const Expr* e) override;
    int interp() override;
    bool has_variable() override;
    Expr* subst(const std::string& var, Expr* replacement) override;

    void printExp(std::ostream& ot) override;
    void pretty_print(std::ostream& ot, precedence_t prec) override;
};

#endif // EXPR_H