//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "expr.h"       // Include the header file for expression classes
#include <stdexcept>    // For std::runtime_error
#include <sstream>      // For std::stringstream
#include "val.h"        // Include val.h for Val and NumVal
#include "parse.hpp"
#include "pointer.h"

// ====================== Expr ======================

std::string Expr::to_string() {
    std::stringstream st(""); // Create a string stream
    THIS->printExp(st);       // Print the expression to the stream
    return st.str();          // Return the string representation
}

std::string Expr::to_pretty_string() {
    std::stringstream st; // Create a string stream
    THIS->pretty_print(st, prec_none); // Pretty-print the expression to the stream
    return st.str();      // Return the pretty-printed string
}

void Expr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos);  // Delegate to pretty_print_at
}

void Expr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    (void)prec; // Mark as unused
    (void)last_newline_pos; // Mark as unused
    printExp(ot); // Default implementation: just call printExp
}

// ====================== NumExpr ======================

NumExpr::NumExpr(int value) : value(value) {}

PTR(Val) NumExpr::interp(PTR(Env) env) {
    (void)env;
    return NEW(NumVal)(value);
}

bool NumExpr::equals(const PTR(Expr) e) {
    PTR(const NumExpr) numExpr = CAST(const NumExpr)(e); // Cast to NumExpr
    return numExpr && value == numExpr->value; // Compare values
}

//PTR(Expr) NumExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    (void)var; // Mark as unused
//    (void)replacement; // Mark as unused
//    return THIS; // Numbers do not contain variables, so return the same expression
//}

void NumExpr::printExp(std::ostream& ot) {
    ot << value; // Print the number
}

// ====================== AddExpr ======================

AddExpr::AddExpr(PTR(Expr) lhs, PTR(Expr) rhs) : lhs(lhs), rhs(rhs) {}

bool AddExpr::equals(const PTR(Expr) e) {
    PTR(const AddExpr) addExpr = CAST(const AddExpr)(e); // Cast to AddExpr
    return addExpr && lhs->equals(addExpr->lhs) && rhs->equals(addExpr->rhs); // Compare sub-expressions
}

PTR(Val) AddExpr::interp(PTR(Env) env) {
    PTR(Val) lhsVal = lhs->interp(env);
    PTR(Val) rhsVal = rhs->interp(env);
    PTR(NumVal) lhsNum = CAST(NumVal)(lhsVal);
    PTR(NumVal) rhsNum = CAST(NumVal)(rhsVal);

    if (!lhsNum || !rhsNum) {
        throw std::runtime_error("Cannot add non-numeric values");
    }

    int a = lhsNum->value;
    int b = rhsNum->value;

    // Check for overflow in addition
    if (b > 0 && a > INT_MAX - b) {
        throw std::runtime_error("arithmetic overflow");
    }
    if (b < 0 && a < INT_MIN - b) {
        throw std::runtime_error("arithmetic overflow");
    }

    int result = a + b;
    return NEW(NumVal)(result);
}

//PTR(Expr) AddExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    return NEW(AddExpr)(lhs->subst(var, replacement), rhs->subst(var, replacement));
//}

void AddExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "+";       // Print the addition operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

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

MultExpr::MultExpr(PTR(Expr) lhs, PTR(Expr) rhs) : lhs(lhs), rhs(rhs) {};

bool MultExpr::equals(const PTR(Expr) e) {
    PTR(const MultExpr) multExpr = CAST(const MultExpr)(e); // Cast to MultExpr
    return multExpr && lhs->equals(multExpr->lhs) && rhs->equals(multExpr->rhs); // Compare sub-expressions
}

PTR(Val) MultExpr::interp(PTR(Env) env) {
    PTR(Val) lhsVal = lhs->interp(env);
    PTR(Val) rhsVal = rhs->interp(env);
    PTR(NumVal) lhsNum = CAST(NumVal)(lhsVal);
    PTR(NumVal) rhsNum = CAST(NumVal)(rhsVal);

    if (!lhsNum || !rhsNum) {
        throw std::runtime_error("Cannot multiply non-numeric values");
    }

    int a = lhsNum->value;
    int b = rhsNum->value;

    // Check for overflow in multiplication
    if (a > 0) {
        if (b > 0 && a > INT_MAX / b) {
            throw std::runtime_error("arithmetic overflow");
        }
        if (b < 0 && b < INT_MIN / a) {
            throw std::runtime_error("arithmetic overflow");
        }
    } else if (a < 0) {
        if (b > 0 && a < INT_MIN / b) {
            throw std::runtime_error("arithmetic overflow");
        }
        if (b < 0 && b < INT_MAX / a) {
            throw std::runtime_error("arithmetic overflow");
        }
    }

    int result = a * b;
    return NEW(NumVal)(result);
}

//PTR(Expr) MultExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    return NEW(MultExpr)(lhs->subst(var, replacement), rhs->subst(var, replacement));
//}

void MultExpr::printExp(std::ostream& ot) {
    ot << "(";       // Print opening parenthesis
    lhs->printExp(ot); // Print the left-hand side
    ot << "*";       // Print the multiplication operator
    rhs->printExp(ot); // Print the right-hand side
    ot << ")";       // Print closing parenthesis
}

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

VarExpr::VarExpr(const std::string& name) : name(name) {}

bool VarExpr::equals(const PTR(Expr) e) {
    PTR(const VarExpr) varExpr = CAST(const VarExpr)(e); // Cast to VarExpr
    return varExpr && name == varExpr->name; // Compare variable names
}

PTR(Val) VarExpr::interp(PTR(Env) env) {
    return env->lookup(name);  // Look up variable in environment
}

//PTR(Expr) VarExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    if (name == var) {
//        return replacement; // Substitute if the variable matches
//    }
//    return THIS; // Otherwise, return the current expression
//}

void VarExpr::printExp(std::ostream& ot) {
    ot << name; // Print the variable name
}

// ====================== LetExpr ======================

LetExpr::LetExpr(const std::string& var, PTR(Expr) rhs, PTR(Expr) body)
    : var(var), rhs(rhs), body(body) {}

bool LetExpr::equals(const PTR(Expr) e) {
    PTR(const LetExpr) letExpr = CAST(const LetExpr)(e); // Cast to LetExpr
    return letExpr && var == letExpr->var && // Compare variables
           rhs->equals(letExpr->rhs) && // Compare right-hand sides
           body->equals(letExpr->body); // Compare bodies
}

PTR(Val) LetExpr::interp(PTR(Env) env) {
    // 1. Evaluate the right-hand side in the current environment
    PTR(Val) rhs_val = rhs->interp(env);

    // 2. Create a new environment that extends the current one
    //    with the new variable binding
    PTR(Env) new_env = NEW(ExtendedEnv)(var, rhs_val, env);

    // 3. Evaluate the body in the extended environment
    return body->interp(new_env);
}

//PTR(Expr) LetExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    if (var == var) {
//        // If the variable to substitute is the bound variable, do not substitute in the body
//        return NEW(LetExpr)(var, rhs->subst(var, replacement), body);
//    }
//    // Substitute in both rhs and body
//    return NEW(LetExpr)(var, rhs->subst(var, replacement), body->subst(var, replacement));
//}

void LetExpr::printExp(std::ostream& ot) {
    ot << "(_let " << var << "="; // Print the let keyword and variable
    rhs->printExp(ot); // Print the right-hand side
    ot << " _in "; // Print the in keyword
    body->printExp(ot); // Print the body
    ot << ")"; // Print closing parenthesis
}

void LetExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos); // Delegate to pretty_print_at
}

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

// ====================== BoolExpr ======================

BoolExpr::BoolExpr(bool value) : value(value) {}

bool BoolExpr::equals(const PTR(Expr) e) {
    PTR(const BoolExpr) boolExpr = CAST(const BoolExpr)(e);
    return boolExpr && value == boolExpr->value;
}

PTR(Val) BoolExpr::interp(PTR(Env) env) {
    (void)env;
    return NEW(BoolVal)(value);
}

//PTR(Expr) BoolExpr::subst(const std::string& var, PTR(Expr) replacement) {
//  	(void)var;
//    (void)replacement;
//    return THIS;
//}

void BoolExpr::printExp(std::ostream& ot) {
    ot << (value ? "_true" : "_false");
}

void BoolExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
  	(void)prec;
    (void)last_newline_pos;
    ot << (value ? "_true" : "_false");
}

// ====================== IfExpr ======================

IfExpr::IfExpr(PTR(Expr) condition, PTR(Expr) then_branch, PTR(Expr) else_branch)
    : condition(condition), then_branch(then_branch), else_branch(else_branch) {}


bool IfExpr::equals(const PTR(Expr) e) {
    PTR(const IfExpr) ifExpr = CAST(const IfExpr)(e);
    return ifExpr && condition->equals(ifExpr->condition) &&
           then_branch->equals(ifExpr->then_branch) &&
           else_branch->equals(ifExpr->else_branch);
}

PTR(Val) IfExpr::interp(PTR(Env) env) {
    // 1. Evaluate the condition in the current environment
    PTR(Val) condVal = condition->interp(env);
    PTR(BoolVal) boolVal = CAST(BoolVal)(condVal);

    // 2. Verify it's a boolean value
    if (!boolVal) {
        throw std::runtime_error("Condition must be a boolean");
    }

    // 3. Evaluate the appropriate branch in the same environment
    if (boolVal->is_true()) {
        return then_branch->interp(env);
    } else {
        return else_branch->interp(env);
    }
}

//PTR(Expr) IfExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    return NEW(IfExpr)(condition->subst(var, replacement),
//                      then_branch->subst(var, replacement),
//                      else_branch->subst(var, replacement));
//}

void IfExpr::printExp(std::ostream& ot) {
    ot << "(_if ";
    condition->printExp(ot);
    ot << " _then ";
    then_branch->printExp(ot);
    ot << " _else ";
    else_branch->printExp(ot);
    ot << ")";
}

void IfExpr::pretty_print(std::ostream& ot, precedence_t prec) {
    std::streampos last_newline_pos = ot.tellp(); // Get the current position in the stream
    pretty_print_at(ot, prec, last_newline_pos); // Delegate to pretty_print_at
}

void IfExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec != prec_none); // Determine if parentheses are needed
    if (needs_parentheses) {
        ot << "("; // Print opening parenthesis if needed
    }

    std::streampos position1 = last_newline_pos; // Save the position of the last newline
    std::streampos current_pos = ot.tellp(); // Get the current position in the stream

    // Print the _if part
    ot << "_if "; // Print the if keyword
    condition->pretty_print(ot, prec_none); // Pretty-print the condition

    // Track the position after the newline
    ot << "\n"; // Print a newline
    last_newline_pos = ot.tellp(); // Update the position of the last newline

    // Calculate the indentation for _in
    for (int i = position1; i < current_pos; i++) {
        ot << " "; // Print spaces for indentation
    }

    // Print the _then part with proper indentation
    ot << "_then "; // Print the then keyword
    then_branch->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the then branch

    // Track the position after the newline
    ot << "\n"; // Print a newline
    last_newline_pos = ot.tellp(); // Update the position of the last newline

    // Calculate the indentation for _else
    for (int i = position1; i < current_pos; i++) {
        ot << " "; // Print spaces for indentation
    }

    // Print the _else part with proper indentation
    ot << "_else "; // Print the else keyword
    else_branch->pretty_print_at(ot, prec_none, last_newline_pos); // Pretty-print the else branch

    if (needs_parentheses) {
        ot << ")"; // Print closing parenthesis if needed
    }
}


// ====================== EqExpr ======================


EqExpr::EqExpr(PTR(Expr) lhs, PTR(Expr) rhs) : lhs(lhs), rhs(rhs) {}

bool EqExpr::equals(const PTR(Expr) e) {
    PTR(const EqExpr) eqExpr = CAST(const EqExpr)(e);
    return eqExpr && lhs->equals(eqExpr->lhs) && rhs->equals(eqExpr->rhs);
}

PTR(Val) EqExpr::interp(PTR(Env) env) {
    PTR(Val) lhsVal = lhs->interp(env);
    PTR(Val) rhsVal = rhs->interp(env);
    return NEW(BoolVal)(lhsVal->equals(rhsVal));
}

//PTR(Expr) EqExpr::subst(const std::string& var, PTR(Expr) replacement) {
//    return NEW(EqExpr)(lhs->subst(var, replacement), rhs->subst(var, replacement));
//}

void EqExpr::printExp(std::ostream& ot) {
    ot << "(";
    lhs->printExp(ot);
    ot << "==";
    rhs->printExp(ot);
    ot << ")";
}

void EqExpr::pretty_print_at(std::ostream& ot, precedence_t prec, std::streampos& last_newline_pos) {
    bool needs_parentheses = (prec >= prec_eq);
    if (needs_parentheses) ot << "(";
    lhs->pretty_print_at(ot, prec_eq, last_newline_pos);
    ot << " == ";
    rhs->pretty_print_at(ot, prec_none, last_newline_pos);
    if (needs_parentheses) ot << ")";
}

// ====================== FunExpr ======================

FunExpr::FunExpr(const std::string& formal_arg, PTR(Expr) body)
    : formal_arg(formal_arg), body(body) {}

bool FunExpr::equals(const PTR(Expr) e) {
    PTR(const FunExpr) f = CAST(const FunExpr)(e);
    return f && formal_arg == f->formal_arg && body->equals(f->body);
}

PTR(Val) FunExpr::interp(PTR(Env) env) {
    // Create a closure that captures the current environment
    return NEW(FunVal)(formal_arg, body, env);
}

void FunExpr::printExp(std::ostream& ot) {
    ot << "(_fun (" << formal_arg << ") ";
    body->printExp(ot);
    ot << ")";
}

// ====================== CallExpr ======================

CallExpr::CallExpr(PTR(Expr) to_be_called, PTR(Expr) actual_arg)
    : to_be_called(to_be_called), actual_arg(actual_arg) {}

bool CallExpr::equals(const PTR(Expr) e) {
    PTR(const CallExpr) c = CAST(const CallExpr)(e);
    return c && to_be_called->equals(c->to_be_called)
           && actual_arg->equals(c->actual_arg);
}

PTR(Val) CallExpr::interp(PTR(Env) env) {
    // 1. Evaluate the function expression in the current environment
    PTR(Val) fun_val = to_be_called->interp(env);

    // 2. Evaluate the argument expression in the same environment
    PTR(Val) arg_val = actual_arg->interp(env);

    // 3. Call the function with the argument
    return fun_val->call(arg_val);
}

void CallExpr::printExp(std::ostream& ot) {
    to_be_called->printExp(ot);
    ot << "(";
    actual_arg->printExp(ot);
    ot << ")";
}