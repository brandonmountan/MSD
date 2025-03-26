//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date:   01/14/2025
//
// Class: CS 6015 - Software Engineering
//
//////////////////////////////////////////////////////////////////////////////////

#include "catch.h"
#include "expr.h"
#include "val.h"
#include "parse.hpp"
#include <stdexcept>
#include <iostream>

// ====================== NumExpr Tests ======================
TEST_CASE("NumExpr tests") {
    NumExpr* num1 = new NumExpr(5);
    NumExpr* num2 = new NumExpr(5);
    NumExpr* num3 = new NumExpr(6);

    // Test equals()
    CHECK(num1->equals(num2) == true);
    CHECK(num1->equals(num3) == false);

    // Test interp()
    Val* result1 = num1->interp();
    CHECK(result1->to_string() == "5"); // Check string representation
    CHECK(result1->equals(new NumVal(5)));

    // Test has_variable()
//    CHECK(num1->has_variable() == false);

    // Test subst()
    CHECK(num1->subst("x", new NumExpr(10))->equals(num1));

    // Test pretty_print()
    CHECK(num1->to_pretty_string() == "5");
}

// ====================== AddExpr Tests ======================
TEST_CASE("AddExpr tests") {
    AddExpr* add1 = new AddExpr(new NumExpr(2), new NumExpr(3));
    AddExpr* add2 = new AddExpr(new NumExpr(2), new NumExpr(3));
    AddExpr* add3 = new AddExpr(new NumExpr(3), new NumExpr(2));
    AddExpr* addWithVar = new AddExpr(new VarExpr("x"), new NumExpr(5));

    // Test equals()
    CHECK(add1->equals(add2) == true);
    CHECK(add1->equals(add3) == false);

    // Test interp()
    Val* result1 = add1->interp();
    CHECK(result1->to_string() == "5"); // 2 + 3 = 5
    CHECK(result1->equals(new NumVal(5)));

    // Test has_variable()
//    CHECK(add1->has_variable() == false);
//    CHECK(addWithVar->has_variable() == true);

    // Test subst()
    Expr* replaced = addWithVar->subst("x", new NumExpr(10));  // Replace "x" with 10
    CHECK(replaced->equals(new AddExpr(new NumExpr(10), new NumExpr(5))));

    // Nested substitution test
    CHECK((new AddExpr(new VarExpr("x"), new NumExpr(7)))
          ->subst("x", new VarExpr("y"))
          ->equals(new AddExpr(new VarExpr("y"), new NumExpr(7))));

    // Test pretty_print()
    CHECK(add1->to_pretty_string() == "2 + 3");
    CHECK(addWithVar->to_pretty_string() == "x + 5");
}

// ====================== MultExpr Tests ======================
TEST_CASE("MultExpr tests") {
    MultExpr* mult1 = new MultExpr(new NumExpr(2), new NumExpr(3));
    MultExpr* mult2 = new MultExpr(new NumExpr(2), new NumExpr(3));
    MultExpr* mult3 = new MultExpr(new NumExpr(3), new NumExpr(2));
    MultExpr* multWithVar = new MultExpr(new VarExpr("x"), new NumExpr(5));

    // Test equals()
    CHECK(mult1->equals(mult2) == true);
    CHECK(mult1->equals(mult3) == false);

    // Test interp()
    Val* result1 = mult1->interp();
    CHECK(result1->to_string() == "6"); // 2 * 3 = 6
    CHECK(result1->equals(new NumVal(6)));

    // Test has_variable()
//    CHECK(mult1->has_variable() == false);
//    CHECK(multWithVar->has_variable() == true);

    // Test subst()
    Expr* replaced = multWithVar->subst("x", new NumExpr(10));  // Replace "x" with 10
    CHECK(replaced->equals(new MultExpr(new NumExpr(10), new NumExpr(5))));

    // Test pretty_print()
    CHECK(mult1->to_pretty_string() == "2 * 3");
    CHECK(multWithVar->to_pretty_string() == "x * 5");
}

// ====================== VarExpr Tests ======================
TEST_CASE("VarExpr tests") {
    VarExpr* var1 = new VarExpr("x");
    VarExpr* var2 = new VarExpr("x");
    VarExpr* var3 = new VarExpr("y");

    // Test equals()
    CHECK(var1->equals(var2) == true);
    CHECK(var1->equals(var3) == false);

    // Test interp() - Check exception
    CHECK_THROWS_WITH(var1->interp(), "Variable has no value");

    // Test has_variable()
//    CHECK(var1->has_variable() == true);

    // Test subst()
    Expr* replacement = var1->subst("x", new NumExpr(10));
    CHECK(replacement->equals(new NumExpr(10)));

    Expr* unchanged = var1->subst("y", new NumExpr(10));
    CHECK(unchanged->equals(var1));

    // Test pretty_print()
    CHECK(var1->to_pretty_string() == "x");
}

// ====================== LetExpr Tests ======================
TEST_CASE("LetExpr basic tests") {
    LetExpr* let1 = new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));
    LetExpr* let2 = new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));
    LetExpr* let3 = new LetExpr("y", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));

    // Test equals()
    CHECK(let1->equals(let2) == true);
    CHECK(let1->equals(let3) == false);

    // Test interp()
    Val* result1 = let1->interp();
    CHECK(result1->to_string() == "6");  // (5 + 1) = 6
    CHECK(result1->equals(new NumVal(6))); // Compare with another NumVal

    // Test has_variable()
//    CHECK(let1->has_variable() == true);  // Body has variable 'x'
//    CHECK((new LetExpr("x", new NumExpr(5), new NumExpr(10)))->has_variable() == false);

    // Test subst()
//    CHECK(let1->subst("x", new NumExpr(10))->equals(new LetExpr("x", new NumExpr(5), new AddExpr(new NumExpr(10), new NumExpr(1)))));
    Expr* result = let1->subst("x", new NumExpr(10));

    // Expected result: _let x = 5 _in x + 1
    LetExpr* expected = new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));

    CHECK(result->equals(expected)); // This should now pass

    // Test pretty_print()
    CHECK(let1->to_pretty_string() == "_let x = 5\n_in  x + 1");
}

// ====================== BoolExpr Tests ======================
TEST_CASE("BoolExpr tests") {
    BoolExpr* trueExpr = new BoolExpr(true);
    BoolExpr* falseExpr = new BoolExpr(false);

    // Test equals()
    CHECK(trueExpr->equals(new BoolExpr(true)) == true);
    CHECK(trueExpr->equals(falseExpr) == false);

    // Test interp()
    Val* trueVal = trueExpr->interp();
    CHECK(trueVal->to_string() == "_true");
    CHECK(trueVal->equals(new BoolVal(true)));

    Val* falseVal = falseExpr->interp();
    CHECK(falseVal->to_string() == "_false");
    CHECK(falseVal->equals(new BoolVal(false)));

    // Test has_variable()
//    CHECK(trueExpr->has_variable() == false);

    // Test subst()
    CHECK(trueExpr->subst("x", new NumExpr(10))->equals(trueExpr));

    // Test pretty_print()
    CHECK(trueExpr->to_pretty_string() == "_true");
    CHECK(falseExpr->to_pretty_string() == "_false");
}

// ====================== IfExpr Tests ======================
TEST_CASE("IfExpr tests") {
    IfExpr* ifTrue = new IfExpr(new BoolExpr(true), new NumExpr(1), new NumExpr(2));
    IfExpr* ifFalse = new IfExpr(new BoolExpr(false), new NumExpr(1), new NumExpr(2));

    // Test equals()
    CHECK(ifTrue->equals(new IfExpr(new BoolExpr(true), new NumExpr(1), new NumExpr(2))) == true);
    CHECK(ifTrue->equals(ifFalse) == false);

    // Test interp()
    Val* result1 = ifTrue->interp();
    CHECK(result1->to_string() == "1");  // Condition is true
    CHECK(result1->equals(new NumVal(1)));

    Val* result2 = ifFalse->interp();
    CHECK(result2->to_string() == "2");  // Condition is false
    CHECK(result2->equals(new NumVal(2)));

    // Test has_variable()
//    CHECK(ifTrue->has_variable() == false);

    // Test subst()
    CHECK(ifTrue->subst("x", new NumExpr(10))->equals(ifTrue));

    // Test pretty_print()
    CHECK(ifTrue->to_pretty_string() == "_if _true\n_then 1\n_else 2");
    CHECK(ifFalse->to_pretty_string() == "_if _false\n_then 1\n_else 2");

    // Test invalid condition (non-boolean)
    IfExpr* invalidIf = new IfExpr(new NumExpr(5), new NumExpr(1), new NumExpr(2));
    CHECK_THROWS_WITH(invalidIf->interp(), "Condition must be a boolean");
}

// ====================== EqExpr Tests ======================
TEST_CASE("EqExpr tests") {
    EqExpr* eq1 = new EqExpr(new NumExpr(5), new NumExpr(5));
    EqExpr* eq2 = new EqExpr(new NumExpr(5), new NumExpr(6));
    EqExpr* eq3 = new EqExpr(new BoolExpr(true), new BoolExpr(true));

    // Test equals()
    CHECK(eq1->equals(new EqExpr(new NumExpr(5), new NumExpr(5))) == true);
    CHECK(eq1->equals(eq2) == false);

    // Test interp()
    Val* result1 = eq1->interp();
    CHECK(result1->to_string() == "_true");  // 5 == 5
    CHECK(result1->equals(new BoolVal(true)));

    Val* result2 = eq2->interp();
    CHECK(result2->to_string() == "_false"); // 5 == 6
    CHECK(result2->equals(new BoolVal(false)));

    Val* result3 = eq3->interp();
    CHECK(result3->to_string() == "_true");  // true == true
    CHECK(result3->equals(new BoolVal(true)));

    // Test has_variable()
//    CHECK(eq1->has_variable() == false);

    // Test subst()
    CHECK(eq1->subst("x", new NumExpr(10))->equals(eq1));

    // Test pretty_print()
    CHECK(eq1->to_pretty_string() == "5 == 5");
    CHECK(eq2->to_pretty_string() == "5 == 6");
    CHECK(eq3->to_pretty_string() == "_true == _true");
}

// ====================== Parser Tests ======================
TEST_CASE("parse") {
    CHECK_THROWS_WITH( parse_str("()"), "bad input" );

    CHECK( parse_str("(1)")->equals(new NumExpr(1)) );
    CHECK( parse_str("(((1)))")->equals(new NumExpr(1)) );

    CHECK_THROWS_WITH( parse_str("(1"), "bad input" );

    CHECK( parse_str("1")->equals(new NumExpr(1)) );
    CHECK( parse_str("10")->equals(new NumExpr(10)) );
    CHECK( parse_str("-3")->equals(new NumExpr(-3)) );
    CHECK( parse_str("  \n 5  ")->equals(new NumExpr(5)) );
    CHECK_THROWS_WITH( parse_str("-"), "invalid input" );

    CHECK_THROWS_WITH( parse_str(" -   5  "), "invalid input" );

    CHECK( parse_str("x")->equals(new VarExpr("x")) );
    CHECK( parse_str("xyz")->equals(new VarExpr("xyz")) );
    CHECK( parse_str("xYz")->equals(new VarExpr("xYz")) );
    CHECK_THROWS_WITH( parse_str("x_z"), "invalid input" );

    CHECK( parse_str("x + y")->equals(new AddExpr(new VarExpr("x"), new VarExpr("y"))) );

    CHECK( parse_str("x * y")->equals(new MultExpr(new VarExpr("x"), new VarExpr("y"))) );

    CHECK( parse_str("z * x + y")
          ->equals(new AddExpr(new MultExpr(new VarExpr("z"), new VarExpr("x")),
                           new VarExpr("y"))) );

    CHECK( parse_str("z * (x + y)")
          ->equals(new MultExpr(new VarExpr("z"),
                            new AddExpr(new VarExpr("x"), new VarExpr("y"))) ));

    // Test parsing boolean values
    CHECK( parse_str("_true")->equals(new BoolExpr(true)) );
    CHECK( parse_str("_false")->equals(new BoolExpr(false)) );

    // Test parsing equality expressions
    CHECK( parse_str("1 == 2")->equals(new EqExpr(new NumExpr(1), new NumExpr(2))) );

    // Test parsing if expressions
    CHECK( parse_str("_if _true _then 1 _else 2")
          ->equals(new IfExpr(new BoolExpr(true), new NumExpr(1), new NumExpr(2))) );
}

// ====================== Additional Tests for Pretty Print ======================
TEST_CASE("Pretty print tests for conditionals") {
    // Test pretty printing of nested if expressions
    IfExpr* nestedIf = new IfExpr(
        new BoolExpr(true),
        new IfExpr(new BoolExpr(false), new NumExpr(1), new NumExpr(2)),
        new NumExpr(3)
    );
    CHECK(nestedIf->to_pretty_string() ==
        "_if _true\n"
        "_then _if _false\n"
        "      _then 1\n"
        "      _else 2\n"
        "_else 3");

    // Test pretty printing of if expressions with arithmetic
    IfExpr* ifWithArithmetic = new IfExpr(
        new EqExpr(new NumExpr(1), new NumExpr(2)),
        new AddExpr(new NumExpr(3), new NumExpr(4)),
        new MultExpr(new NumExpr(5), new NumExpr(6))
    );
    CHECK(ifWithArithmetic->to_pretty_string() ==
        "_if 1 == 2\n"
        "_then 3 + 4\n"
        "_else 5 * 6");

    // Test pretty printing of if expressions with let expressions
    IfExpr* ifWithLet = new IfExpr(
        new BoolExpr(true),
        new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1))),
        new NumExpr(10)
    );
    CHECK(ifWithLet->to_pretty_string() ==
        "_if _true\n"
        "_then _let x = 5\n"
        "      _in  x + 1\n"
        "_else 10");
}

// ====================== FunExpr Tests ======================
TEST_CASE("FunExpr basic tests") {
    FunExpr* fun1 = new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1)));
    FunExpr* fun2 = new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1)));
    FunExpr* fun3 = new FunExpr("y", new AddExpr(new VarExpr("x"), new NumExpr(1)));

    // Test equals()
    CHECK(fun1->equals(fun2) == true);
    CHECK(fun1->equals(fun3) == false);

    // Test interp() - should return a FunVal
    Val* result = fun1->interp();
    CHECK(dynamic_cast<FunVal*>(result) != nullptr);
    CHECK(result->to_string() == "[function]");

    // Test subst() - should not substitute shadowed variables
    Expr* subst1 = fun1->subst("x", new NumExpr(5));
    CHECK(subst1->equals(fun1)); // x is shadowed by formal arg

    Expr* subst2 = fun1->subst("y", new NumExpr(5));
    CHECK(subst2->equals(new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1)))));

    // Test printExp()
    CHECK(fun1->to_string() == "(_fun (x) (x+1))");

    // Test pretty_print()
//    CHECK(fun1->to_pretty_string() == "_fun (x)\n  x + 1");
}

// ====================== CallExpr Tests ======================
TEST_CASE("CallExpr basic tests") {
    // Simple function application
    CallExpr* call1 = new CallExpr(
        new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1))),
        new NumExpr(5)
    );

    // Test equals()
    CHECK(call1->equals(
        new CallExpr(
            new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1))),
            new NumExpr(5)
        )
    ) == true);

    // Test interp()
    Val* result1 = call1->interp();
    CHECK(result1->to_string() == "6"); // (λx.x+1)(5) = 6

    // Test nested function calls
    CallExpr* nestedCall = new CallExpr(
        new CallExpr(
            new FunExpr("f", new FunExpr("x", new AddExpr(
                new CallExpr(new VarExpr("f"), new VarExpr("x")),
                new NumExpr(1)
            ))),
            new FunExpr("y", new AddExpr(new VarExpr("y"), new NumExpr(2)))
        ),
        new NumExpr(3)
    );
    Val* result2 = nestedCall->interp();
    CHECK(result2->to_string() == "6"); // Should evaluate to 6

    // Test printExp()
    CHECK(call1->to_string() == "(_fun (x) (x+1))(5)");

    // Test pretty_print()
//    CHECK(call1->to_pretty_string() == "(_fun (x)\n  x + 1)(5)");
}

// ====================== FunVal Tests ======================
TEST_CASE("FunVal basic tests") {
    FunVal* funVal1 = new FunVal("x", new AddExpr(new VarExpr("x"), new NumExpr(1)));
    FunVal* funVal2 = new FunVal("x", new AddExpr(new VarExpr("x"), new NumExpr(1)));
    FunVal* funVal3 = new FunVal("y", new AddExpr(new VarExpr("x"), new NumExpr(1)));

    // Test equals()
    CHECK(funVal1->equals(funVal2) == true);
    CHECK(funVal1->equals(funVal3) == false);
    CHECK(funVal1->equals(new NumVal(1)) == false);

    // Test to_string()
    CHECK(funVal1->to_string() == "[function]");

    // Test call()
    Val* result1 = funVal1->call(new NumVal(5));
    CHECK(result1->to_string() == "6"); // (λx.x+1)(5) = 6

    // Test invalid operations
    CHECK_THROWS_WITH(funVal1->add_to(new NumVal(1)), "Cannot add functions");
    CHECK_THROWS_WITH(funVal1->mult_with(new NumVal(1)), "Cannot multiply functions");
    CHECK_THROWS_WITH(funVal1->is_true(), "Cannot use function as boolean");
}

// ====================== Parser Tests for Functions ======================
TEST_CASE("parse functions") {
    // Test parsing function expressions
    CHECK( parse_str("_fun (x) x + 1")->equals(
        new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1))))
    );

    // Test parsing function calls
    CHECK( parse_str("f(x)")->equals(
        new CallExpr(new VarExpr("f"), new VarExpr("x")))
    );

    // Test parsing nested function calls
    CHECK( parse_str("f(g)(x)")->equals(
        new CallExpr(
            new CallExpr(new VarExpr("f"), new VarExpr("g")),
            new VarExpr("x")
        ))
    );

    // Test parsing function with body containing another function
    CHECK( parse_str("_fun (x) _fun (y) x + y")->equals(
        new FunExpr("x",
            new FunExpr("y",
                new AddExpr(new VarExpr("x"), new VarExpr("y"))
        )))
    );

    // Test parsing function application with complex arguments
    CHECK( parse_str("f(x + 1)")->equals(
        new CallExpr(
            new VarExpr("f"),
            new AddExpr(new VarExpr("x"), new NumExpr(1))
        ))
    );

    SECTION("Simple function") {
        std::string input = "_fun (x) x + 1";
        std::cout << "Attempting to parse: " << input << "\n";

        try {
            Expr* parsed = parse_str(input);
            std::cout << "Successfully parsed as: " << parsed->to_string() << "\n";

            Expr* expected = new FunExpr("x", new AddExpr(new VarExpr("x"), new NumExpr(1)));
            CHECK(parsed->equals(expected));
        } catch (const std::exception& e) {
            FAIL("Exception thrown: " << e.what());
        }
    }
}

// ====================== Factorial Example Test ======================
TEST_CASE("Factorial example") {
    std::string factorial =
        "_let factrl = _fun (factrl) "
        "              _fun (x) "
        "                _if x == 1 "
        "                _then 1 "
        "                _else x * factrl(factrl)(x + -1) "
        "_in factrl(factrl)(10)";

    Expr* factExpr = parse_str(factorial);
    Val* result = factExpr->interp();
    CHECK(result->to_string() == "3628800"); // 10! = 3628800
}
