//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

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
    prec_eq,        ///< Precedence level for equality (==).
    prec_add,       ///< Precedence level for addition (+).
    prec_mult       ///< Precedence level for multiplication (*).
} precedence_t;

class Val; // Forward declaration of Val class

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
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    virtual bool equals(const Expr* e) = 0;

    /**
     * @brief Evaluates the expression to an integer value.
     * @return The result of evaluating the expression.
     */
    virtual Val* interp() = 0;

    /**
     * @brief Substitutes a variable with another expression.
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with
     * @return A new expression with the substitution applied.
     */
    virtual Expr* subst(const std::string& var, Expr* replacement) = 0;

    /**
     * @brief Prints the expression to an output stream.
     * @param ot The output stream to print to.
     */
    virtual void printExp(std::ostream& ot) = 0;

    /**
     * @ brief Coverts the expression to a string.
     * @return A string representation of the expression.
     */
    std::string to_string();

    /**
     * @brief Converts the expression to a pretty-printed string.
     * @return A pretty-printed string representation of the expression.
     */
    std::string to_pretty_string();

    /**
     * @brief Pretty-prints the expression to an output stream with proper precedence handling.
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     */
    virtual void pretty_print(std::ostream& ot, precedence_t prec);

	/**
 	 * @brief Pretty-prints the expression at a specific precedence level.
 	 * @param ot The output stream to print to.
 	 * @param prec The precedence level of the parent expression.
	 * @param last_newline_pos The position of the last newline in the output stream.
 	 */
    virtual void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos);
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
     * @param value The numeric value.
     */
    NumExpr(int value);

    /**
     * @brief Checks if this number expression is equal to another expression.
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the number expression by returning its value.
     * @return A NumVal object representing the number.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression.
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return The current number expression (no substitution needed).
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the number expression to an output stream.
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;
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

    /**
     * @brief Checks if this addition expression is equal to another expression.
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the addition expression by evaluating its sub-expressions.
     * @return A Val* object representing the sum of the left and right sub-expressions.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in both sub-expressions.
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new AddExpr with the substitution applied.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the addition expression to an output stream.
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;

    /**
     * @brief Pretty-prints the addition expression with proper precedence handling.
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     * @param last_newline_pos The position of the last newline in the output stream.
     */
    void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) override;

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

    /**
     * @brief Checks if this multiplication expression is equal to another expression.
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the multiplication expression by evaluating its sub-expressions.
     * @return A Val* object representing the product of the left and right sub-expressions.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in both sub-expressions.
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new MultExpr with the substitution applied.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the multiplication expression to an output stream.
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;

    /**
     * @brief Pretty-prints the multiplication expression with proper precedence handling.
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     * @param last_newline_pos The position of the last newline in the output stream.
     */
    void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) override;
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

    /**
     * @brief Checks if this variable expression is equal to another expression.
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the variable expression (throws an error since variables have no value).
     * @throws std::runtime_error because variables cannot be interpreted without a value.
     */
    Val* interp() override;

    /**
     * @brief Substitutes the variable with a replacement expression if it matches the variable name.
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return The replacement expression if the variable matches, otherwise the current expression.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the variable expression to an output stream.
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;
};

class LetExpr : public Expr {
    std::string var;       // The variable to bind
    Expr* rhs;             // The right-hand side expression
    Expr* body;            // The body expression

public:
    /**
     * @brief Constructs a let expression.
     *
     * @param var The variable to bind.
     * @param rhs The right-hand side expression.
     * @param body The body expression.
     */
    LetExpr(const std::string& var, Expr* rhs, Expr* body);

    /**
     * @brief Checks if this let expression is equal to another expression.
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the let expression by evaluating the right-hand side and substituting it into the body.
     * @return A Val* object representing the result of interpreting the substituted body expression.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in the let expression.
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new LetExpr with the substitution applied.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the let expression to an output stream.
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;

    /**
     * @brief Pretty-prints the let expression with proper indentation.
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     */
    void pretty_print(std::ostream& ot, precedence_t prec) override;

    /**
     * @brief Pretty-prints the let expression with proper indentation and precedence handling.
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     * @param last_newline_pos The position of the last newline in the output stream.
     */
    void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) override;
};

/**
 * @brief Represents a boolean expression.
 *
 * This class encapsulates a boolean value (true or false) and provides methods
 * for interpretation, substitution, equality checking, and pretty printing.
 */
class BoolExpr : public Expr {
public:
    /**
     * @brief Constructs a boolean expression.
     *
     * @param value The boolean value (true or false).
     */
    BoolExpr(bool value);

    /**
     * @brief Checks if this boolean expression is equal to another expression.
     *
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the boolean expression.
     *
     * @return A Val* object representing the boolean value.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in the boolean expression.
     *
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new BoolExpr with the same value, as boolean expressions do not contain variables.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the boolean expression to an output stream.
     *
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;

    /**
     * @brief Pretty-prints the boolean expression with proper indentation and precedence handling.
     *
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     * @param last_newline_pos The position of the last newline in the output stream.
     */
    void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) override;

private:
    bool value; // The boolean value (true or false).
};

/**
 * @brief Represents an if-then-else expression.
 *
 * This class encapsulates a condition, a then-branch, and an else-branch, and provides
 * methods for interpretation, substitution, equality checking, and pretty printing.
 */
class IfExpr : public Expr {
public:
    /**
     * @brief Constructs an if-then-else expression.
     *
     * @param condition The condition expression.
     * @param then_branch The expression to evaluate if the condition is true.
     * @param else_branch The expression to evaluate if the condition is false.
     */
    IfExpr(Expr* condition, Expr* then_branch, Expr* else_branch);

    /**
     * @brief Checks if this if-then-else expression is equal to another expression.
     *
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the if-then-else expression.
     *
     * @return A Val* object representing the result of evaluating the appropriate branch.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in the if-then-else expression.
     *
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new IfExpr with the substitution applied to the condition, then-branch, and else-branch.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the if-then-else expression to an output stream.
     *
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;

    /**
     * @brief Pretty-prints the if-then-else expression with proper indentation.
     *
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     */
    void pretty_print(std::ostream& ot, precedence_t prec) override;

    /**
     * @brief Pretty-prints the if-then-else expression with proper indentation and precedence handling.
     *
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     * @param last_newline_pos The position of the last newline in the output stream.
     */
    void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) override;

private:
    Expr* condition;    // The condition expression.
    Expr* then_branch;  // The expression to evaluate if the condition is true.
    Expr* else_branch;  // The expression to evaluate if the condition is false.
};

/**
 * @brief Represents an equality expression.
 *
 * This class encapsulates two expressions (left-hand side and right-hand side) and provides
 * methods for interpretation, substitution, equality checking, and pretty printing.
 */
class EqExpr : public Expr {
public:
    /**
     * @brief Constructs an equality expression.
     *
     * @param lhs The left-hand side expression.
     * @param rhs The right-hand side expression.
     */
    EqExpr(Expr* lhs, Expr* rhs);

    /**
     * @brief Checks if this equality expression is equal to another expression.
     *
     * @param e The expression to compare with.
     * @return true if the expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the equality expression.
     *
     * @return A Val* object representing the result of comparing the left-hand side and right-hand side.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in the equality expression.
     *
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new EqExpr with the substitution applied to the left-hand side and right-hand side.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the equality expression to an output stream.
     *
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;

    /**
     * @brief Pretty-prints the equality expression with proper indentation and precedence handling.
     *
     * @param ot The output stream to print to.
     * @param prec The precedence level of the parent expression.
     * @param last_newline_pos The position of the last newline in the output stream.
     */
    void pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) override;

private:
    Expr* lhs; // The left-hand side expression.
    Expr* rhs; // The right-hand side expression.
};

/**
 * @class FunExpr
 * @brief Represents a function definition expression.
 *
 * This class represents a function definition with a formal argument and body expression.
 * Functions are first-class values that can be passed as arguments and returned from other functions.
 */
class FunExpr : public Expr {
    std::string formal_arg; ///< The formal argument name of the function
    Expr* body;             ///< The body expression of the function

public:
    /**
     * @brief Constructs a function expression.
     *
     * @param formal_arg The name of the formal argument.
     * @param body The body expression of the function.
     */
    FunExpr(const std::string& formal_arg, Expr* body);

    /**
     * @brief Checks if this function expression is equal to another expression.
     *
     * @param e The expression to compare with.
     * @return true if the expressions are equal (same formal arg and body), false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the function expression by creating a function value.
     *
     * @return A FunVal object representing the function.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in the function body.
     *
     * Does not substitute if the variable matches the formal argument (shadowing).
     *
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new FunExpr with the substitution applied to its body.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the function expression to an output stream.
     *
     * Follows the format: (_fun (formal_arg) body)
     *
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;
};

/**
 * @class CallExpr
 * @brief Represents a function call expression.
 *
 * This class represents the application of a function to an argument expression.
 */
class CallExpr : public Expr {
    Expr* to_be_called; ///< The expression evaluating to the function to call
    Expr* actual_arg;   ///< The argument expression to pass to the function

public:
    /**
     * @brief Constructs a function call expression.
     *
     * @param to_be_called The expression that evaluates to the function.
     * @param actual_arg The argument expression to pass to the function.
     */
    CallExpr(Expr* to_be_called, Expr* actual_arg);

    /**
     * @brief Checks if this call expression is equal to another expression.
     *
     * @param e The expression to compare with.
     * @return true if both the function and argument expressions are equal, false otherwise.
     */
    bool equals(const Expr* e) override;

    /**
     * @brief Interprets the function call by evaluating the function and argument,
     *        then applying the function to the argument value.
     *
     * @return The result of applying the function to the argument.
     * @throws std::runtime_error if to_be_called doesn't evaluate to a function.
     */
    Val* interp() override;

    /**
     * @brief Substitutes a variable with a replacement expression in both
     *        the function and argument expressions.
     *
     * @param var The variable to substitute.
     * @param replacement The expression to replace the variable with.
     * @return A new CallExpr with substitutions applied to both subexpressions.
     */
    Expr* subst(const std::string& var, Expr* replacement) override;

    /**
     * @brief Prints the function call expression to an output stream.
     *
     * Follows the format: function(arg)
     *
     * @param ot The output stream to print to.
     */
    void printExp(std::ostream& ot) override;
};

#endif // EXPR_H