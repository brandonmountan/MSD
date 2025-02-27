#include "expr.h"       // Include the header file for expression classes
#include <stdexcept>    // For std::runtime_error
#include <sstream>      // For std::stringstream

// ====================== Expr ======================

// Function: to_string
// Purpose: Converts the expression to a string representation.
// Returns: A string representation of the expression.
std::string Expr::to_string() {
    std::stringstream st(""); // Create a string stream
    this->printExp(st);       // Print the expression to the stream
    return st.str();          // Return the string representation
}

// Function: to_pretty_string
// Purpose: Converts the expression to a pretty-printed string representation.
// Returns: A pretty-printed string representation of the expression.
std::string Expr::to_pretty_string() {
    std::stringstream st; // Create a string stream
    this->pretty_print(st, prec_none); // Pretty-print the expression to the stream
    return st.str();      // Return the pretty-printed string
}

// Function: pretty_print_at
// Purpose: Pretty-prints the expression at a specific precedence level.
// Parameters:
//   - ot: The output stream to print to.
//   - prec: The precedence level of the parent expression.
//   - last_newline_pos: The position of the last newline in the output stream.
void Expr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    printExp(ot); // Default implementation: just call printExp
}

// Function: pretty_print
// Purpose: Pretty-prints the expression with proper precedence handling.
// Parameters:
//   - ot: The output stream to print to.
//   - prec: The precedence level of the parent expression.
void Expr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos);  // Delegate to pretty_print_at
}

// ====================== NumExpr ======================

// Constructor: NumExpr
// Purpose: Initializes a NumExpr with a given integer value.
// Parameters:
//   - value: The integer value of the number.
NumExpr::NumExpr(int value) {
    this->value = value; // Store the value
}

// Function: interp
// Purpose: Interprets the number expression by returning its value.
// Returns: The integer value of the number.
int NumExpr::interp() {
    return value;
}

// Function: has_variable
// Purpose: Checks if the number expression contains a variable.
// Returns: false (numbers do not contain variables).
bool NumExpr::has_variable() {
    return false;
}

// Function: subst
// Purpose: Substitutes a variable with a replacement expression.
// Parameters:
//   - var: The variable to substitute.
//   - replacement: The expression to replace the variable with.
// Returns: The current number expression (no substitution needed).
Expr* NumExpr::subst(const std::string& var, Expr* replacement) {
    return this; // Numbers do not contain variables, so return the same expression
}

// Function: equals
// Purpose: Checks if this number expression is equal to another expression.
// Parameters:
//   - e: The expression to compare with.
// Returns: true if the expressions are equal, false otherwise.
bool NumExpr::equals(const Expr* e) {
    const NumExpr* numExpr = dynamic_cast<const NumExpr*>(e); // Cast to NumExpr
    return numExpr && this->value == numExpr->value; // Compare values
}

// Function: printExp
// Purpose: Prints the number expression to an output stream.
// Parameters:
//   - ot: The output stream to print to.
void NumExpr::printExp(std::ostream& ot) {
    ot << value; // Print the number
}

// ====================== AddExpr ======================

// Constructor: AddExpr
// Purpose: Initializes an AddExpr with left and right sub-expressions.
// Parameters:
//   - lhs: The left-hand side expression.
//   - rhs: The right-hand side expression.
AddExpr::AddExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Store the left-hand side
    this->rhs = rhs; // Store the right-hand side
}

// Function: interp
// Purpose: Interprets the addition expression by evaluating its sub-expressions.
// Returns: The sum of the left and right sub-expressions.
int AddExpr::interp() {
    return lhs->interp() + rhs->interp(); // Add the results of the sub-expressions
}

// Function: has_variable
// Purpose: Checks if the addition expression contains a variable.
// Returns: true if either sub-expression contains a variable, false otherwise.
bool AddExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // Check both sub-expressions
}

// Function: subst
// Purpose: Substitutes a variable with a replacement expression in both sub-expressions.
// Parameters:
//   - var: The variable to substitute.
//   - replacement: The expression to replace the variable with.
// Returns: A new AddExpr with the substitution applied.
Expr* AddExpr::subst(const std::string& var, Expr* replacement) {
    return new AddExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

// Function: equals
// Purpose: Checks if this addition expression is equal to another expression.
// Parameters:
//   - e: The expression to compare with.
// Returns: true if the expressions are equal, false otherwise.
bool AddExpr::equals(const Expr* e) {
    const AddExpr* addExpr = dynamic_cast<const AddExpr*>(e); // Cast to AddExpr
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs); // Compare sub-expressions
}

// Function: printExp
// Purpose: Prints the addition expression to an output stream.
// Parameters:
//   - ot: The output stream to print to.
void AddExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "+";       // Print the addition operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

// Function: pretty_print_at
// Purpose: Pretty-prints the addition expression with proper precedence handling.
// Parameters:
//   - ot: The output stream to print to.
//   - prec: The precedence level of the parent expression.
//   - last_newline_pos: The position of the last newline in the output stream.
void AddExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool use_parentheses = (prec >= prec_add); // Determine if parentheses are needed
    if (use_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }
    lhs->pretty_print_at(ot, prec_add, last_newline_pos); // Pretty-print the left-hand side
    ot << " + "; // Print the addition operator
    rhs->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the right-hand side
    if (use_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}

// ====================== MultExpr ======================

// Constructor: MultExpr
// Purpose: Initializes a MultExpr with left and right sub-expressions.
// Parameters:
//   - lhs: The left-hand side expression.
//   - rhs: The right-hand side expression.
MultExpr::MultExpr(Expr* lhs, Expr* rhs) {
    this->lhs = lhs; // Store the left-hand side
    this->rhs = rhs; // Store the right-hand side
}

// Function: interp
// Purpose: Interprets the multiplication expression by evaluating its sub-expressions.
// Returns: The product of the left and right sub-expressions.
int MultExpr::interp() {
    return lhs->interp() * rhs->interp(); // Multiply the results of the sub-expressions
}

// Function: has_variable
// Purpose: Checks if the multiplication expression contains a variable.
// Returns: true if either sub-expression contains a variable, false otherwise.
bool MultExpr::has_variable() {
    return lhs->has_variable() || rhs->has_variable(); // Check both sub-expressions
}

// Function: subst
// Purpose: Substitutes a variable with a replacement expression in both sub-expressions.
// Parameters:
//   - var: The variable to substitute.
//   - replacement: The expression to replace the variable with.
// Returns: A new MultExpr with the substitution applied.
Expr* MultExpr::subst(const std::string& var, Expr* replacement) {
    return new MultExpr(lhs->subst(var, replacement), rhs->subst(var, replacement));
}

// Function: equals
// Purpose: Checks if this multiplication expression is equal to another expression.
// Parameters:
//   - e: The expression to compare with.
// Returns: true if the expressions are equal, false otherwise.
bool MultExpr::equals(const Expr* e) {
    const MultExpr* multExpr = dynamic_cast<const MultExpr*>(e); // Cast to MultExpr
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs); // Compare sub-expressions
}

// Function: printExp
// Purpose: Prints the multiplication expression to an output stream.
// Parameters:
//   - ot: The output stream to print to.
void MultExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "*";       // Print the multiplication operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

// Function: pretty_print_at
// Purpose: Pretty-prints the multiplication expression with proper precedence handling.
// Parameters:
//   - ot: The output stream to print to.
//   - prec: The precedence level of the parent expression.
//   - last_newline_pos: The position of the last newline in the output stream.
void MultExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool use_parentheses = (prec >= prec_mult); // Determine if parentheses are needed
    if (use_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }
    lhs->pretty_print_at(ot, prec_mult, last_newline_pos); // Pretty-print the left-hand side
    ot << " * "; // Print the multiplication operator
    rhs->pretty_print_at(ot, prec_add, last_newline_pos); // Pretty-print the right-hand side
    if (use_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}

// ====================== VarExpr ======================

// Constructor: VarExpr
// Purpose: Initializes a VarExpr with a variable name.
// Parameters:
//   - name: The name of the variable.
VarExpr::VarExpr(const std::string& name) {
    this->name = name; // Store the variable name
}

// Function: interp
// Purpose: Interprets the variable expression (throws an error since variables have no value).
// Throws: std::runtime_error because variables cannot be interpreted without a value.
int VarExpr::interp() {
    throw std::runtime_error("Variable has no value");
}

// Function: has_variable
// Purpose: Checks if the variable expression contains a variable.
// Returns: true (variables always contain themselves).
bool VarExpr::has_variable() {
    return true;
}

// Function: subst
// Purpose: Substitutes the variable with a replacement expression if it matches the variable name.
// Parameters:
//   - var: The variable to substitute.
//   - replacement: The expression to replace the variable with.
// Returns: The replacement expression if the variable matches, otherwise the current expression.
Expr* VarExpr::subst(const std::string& var, Expr* replacement) {
    if (this->name == var) {
        return replacement; // Substitute if the variable matches
    }
    return this; // Otherwise, return the current expression
}

// Function: equals
// Purpose: Checks if this variable expression is equal to another expression.
// Parameters:
//   - e: The expression to compare with.
// Returns: true if the expressions are equal, false otherwise.
bool VarExpr::equals(const Expr* e) {
    const VarExpr* varExpr = dynamic_cast<const VarExpr*>(e); // Cast to VarExpr
    return varExpr && this->name == varExpr->name; // Compare variable names
}

// Function: printExp
// Purpose: Prints the variable expression to an output stream.
// Parameters:
//   - ot: The output stream to print to.
void VarExpr::printExp(std::ostream& ot) {
    ot << name; // Print the variable name
}

// ====================== LetExpr ======================

// Constructor: LetExpr
// Purpose: Initializes a LetExpr with a variable, right-hand side expression, and body expression.
// Parameters:
//   - var: The variable to bind.
//   - rhs: The right-hand side expression.
//   - body: The body expression.
LetExpr::LetExpr(const std::string& var, Expr* rhs, Expr* body) {
    this->var = var; // Store the variable
    this->rhs = rhs; // Store the right-hand side
    this->body = body; // Store the body
}

// Function: equals
// Purpose: Checks if this let expression is equal to another expression.
// Parameters:
//   - e: The expression to compare with.
// Returns: true if the expressions are equal, false otherwise.
bool LetExpr::equals(const Expr* e) {
    const LetExpr* letExpr = dynamic_cast<const LetExpr*>(e); // Cast to LetExpr
    return letExpr && this->var == letExpr->var && // Compare variables
           this->rhs->equals(letExpr->rhs) && // Compare right-hand sides
           this->body->equals(letExpr->body); // Compare bodies
}

// Function: interp
// Purpose: Interprets the let expression by evaluating the right-hand side and substituting it into the body.
// Returns: The result of interpreting the substituted body expression.
int LetExpr::interp() {
    int rhsValue = rhs->interp(); // Evaluate the right-hand side
    Expr* substitutedBody = body->subst(var, new NumExpr(rhsValue)); // Substitute the variable
    return substitutedBody->interp(); // Interpret the substituted body
}

// Function: has_variable
// Purpose: Checks if the let expression contains a variable.
// Returns: true if either the right-hand side or body contains a variable, false otherwise.
bool LetExpr::has_variable() {
    return rhs->has_variable() || body->has_variable(); // Check both sub-expressions
}

// Function: subst
// Purpose: Substitutes a variable with a replacement expression in the let expression.
// Parameters:
//   - var: The variable to substitute.
//   - replacement: The expression to replace the variable with.
// Returns: A new LetExpr with the substitution applied.
Expr* LetExpr::subst(const std::string& var, Expr* replacement) {
    if (this->var == var) {
        // If the variable to substitute is the bound variable, do not substitute in the body
        return new LetExpr(this->var, rhs->subst(var, replacement), body);
    }
    // Substitute in both rhs and body
    return new LetExpr(this->var, rhs->subst(var, replacement), body->subst(var, replacement));
}

// Function: printExp
// Purpose: Prints the let expression to an output stream.
// Parameters:
//   - ot: The output stream to print to.
void LetExpr::printExp(std::ostream& ot) {
    ot << "(_let " << var << "="; // Print the let keyword and variable
    rhs->printExp(ot); // Print the right-hand side
    ot << " _in "; // Print the in keyword
    body->printExp(ot); // Print the body
    ot << ")"; // Print closing parenthesis
}

// Function: pretty_print
// Purpose: Pretty-prints the let expression with proper indentation.
// Parameters:
//   - ot: The output stream to print to.
//   - prec: The precedence level of the parent expression.
void LetExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos); // Delegate to pretty_print_at
}

// Function: pretty_print_at
// Purpose: Pretty-prints the let expression with proper indentation and precedence handling.
// Parameters:
//   - ot: The output stream to print to.
//   - prec: The precedence level of the parent expression.
//   - last_newline_pos: The position of the last newline in the output stream.
void LetExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec != prec_none); // Determine if parentheses are needed
    if (needs_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }

    std::streampos position1 = last_newline_pos; // Save the position of the last newline
    std::streampos current_pos = ot.tellp(); // Get the current position in the stream

    // Print the _let part
    ot << "_let " << var << " = "; // Print the let keyword and variable
    rhs->pretty_print(ot, prec_none); // Pretty-print the right-hand side

    // Track the position after the newline
    ot << "\n"; // Print a newline
    last_newline_pos = ot.tellp(); // Update the position of the last newline

    // Calculate the indentation for _in
    int indent = static_cast<int>(current_pos - last_newline_pos); // Calculate the indentation level
    for (int i = position1; i < current_pos; i++) {
        ot << " "; // Print spaces for indentation
    }

    // Print the _in part with proper indentation
    ot << "_in  "; // Print the in keyword

    // Print the body with proper context
    body->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the body

    if (needs_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}