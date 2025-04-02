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
#include "pointer.h"
#include <stdexcept>
#include <iostream>

// ====================== NumExpr Tests ======================
TEST_CASE("NumExpr tests") {
    PTR(NumExpr) num1 = NEW(NumExpr)(5);
    PTR(NumExpr) num2 = NEW(NumExpr)(5);
    PTR(NumExpr) num3 = NEW(NumExpr)(6);

    // Test equals()
    CHECK(num1->equals(num2) == true);
    CHECK(num1->equals(num3) == false);

    // Test interp()
    PTR(Val) result1 = num1->interp();
    CHECK(result1->to_string() == "5"); // Check string representation
    CHECK(result1->equals(NEW(NumVal)(5)));

    // Test has_variable()
//    CHECK(num1->has_variable() == false);

    // Test subst()
    CHECK(num1->subst("x", NEW(NumExpr)(10))->equals(num1));

    // Test pretty_print()
    CHECK(num1->to_pretty_string() == "5");
}

// ====================== AddExpr Tests ======================
TEST_CASE("AddExpr tests") {
    PTR(AddExpr) add1 = NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3));
    PTR(AddExpr) add2 = NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3));
    PTR(AddExpr) add3 = NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2));
    PTR(AddExpr) addWithVar = NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5));

    // Test equals()
    CHECK(add1->equals(add2) == true);
    CHECK(add1->equals(add3) == false);

    // Test interp()
    PTR(Val) result1 = add1->interp();
    CHECK(result1->to_string() == "5"); // 2 + 3 = 5
    CHECK(result1->equals(NEW(NumVal)(5)));

    // Test has_variable()
//    CHECK(add1->has_variable() == false);
//    CHECK(addWithVar->has_variable() == true);

    // Test subst()
    PTR(Expr) replaced = addWithVar->subst("x", NEW(NumExpr)(10));  // Replace "x" with 10
    CHECK(replaced->equals(NEW(AddExpr)(NEW(NumExpr)(10), NEW(NumExpr)(5))));

    // Nested substitution test
    CHECK((NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
          ->subst("x", NEW(VarExpr)("y"))
          ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))));

    // Test pretty_print()
    CHECK(add1->to_pretty_string() == "2 + 3");
    CHECK(addWithVar->to_pretty_string() == "x + 5");
}

// ====================== MultExpr Tests ======================
TEST_CASE("MultExpr tests") {
    PTR(MultExpr) mult1 = NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3));
    PTR(MultExpr) mult2 = NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3));
    PTR(MultExpr) mult3 = NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2));
    PTR(MultExpr) multWithVar = NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5));

    // Test equals()
    CHECK(mult1->equals(mult2) == true);
    CHECK(mult1->equals(mult3) == false);

    // Test interp()
    PTR(Val) result1 = mult1->interp();
    CHECK(result1->to_string() == "6"); // 2 * 3 = 6
    CHECK(result1->equals(NEW(NumVal)(6)));

    // Test has_variable()
//    CHECK(mult1->has_variable() == false);
//    CHECK(multWithVar->has_variable() == true);

    // Test subst()
    PTR(Expr) replaced = multWithVar->subst("x", NEW(NumExpr)(10));  // Replace "x" with 10
    CHECK(replaced->equals(NEW(MultExpr)(NEW(NumExpr)(10), NEW(NumExpr)(5))));

    // Test pretty_print()
    CHECK(mult1->to_pretty_string() == "2 * 3");
    CHECK(multWithVar->to_pretty_string() == "x * 5");
}

// ====================== VarExpr Tests ======================
TEST_CASE("VarExpr tests") {
    PTR(VarExpr) var1 = NEW(VarExpr)("x");
    PTR(VarExpr) var2 = NEW(VarExpr)("x");
    PTR(VarExpr) var3 = NEW(VarExpr)("y");

    // Test equals()
    CHECK(var1->equals(var2) == true);
    CHECK(var1->equals(var3) == false);

    // Test interp() - Check exception
    CHECK_THROWS_WITH(var1->interp(), "Variable has no value");

    // Test has_variable()
//    CHECK(var1->has_variable() == true);

    // Test subst()
    PTR(Expr) replacement = var1->subst("x", NEW(NumExpr)(10));
    CHECK(replacement->equals(NEW(NumExpr)(10)));

    PTR(Expr) unchanged = var1->subst("y", NEW(NumExpr)(10));
    CHECK(unchanged->equals(var1));

    // Test pretty_print()
    CHECK(var1->to_pretty_string() == "x");
}

// ====================== LetExpr Tests ======================
TEST_CASE("LetExpr basic tests") {
    PTR(LetExpr) let1 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    PTR(LetExpr) let2 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    PTR(LetExpr) let3 = NEW(LetExpr)("y", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

    // Test equals()
    CHECK(let1->equals(let2) == true);
    CHECK(let1->equals(let3) == false);

    // Test interp()
    PTR(Val) result1 = let1->interp();
    CHECK(result1->to_string() == "6");  // (5 + 1) = 6
    CHECK(result1->equals(NEW(NumVal)(6))); // Compare with another NumVal

    // Test has_variable()
//    CHECK(let1->has_variable() == true);  // Body has variable 'x'
//    CHECK((NEW(LetExpr("x", NEW(NumExpr(5), NEW(NumExpr(10)))->has_variable() == false);

    // Test subst()
//    CHECK(let1->subst("x", NEW(NumExpr(10))->equals(NEW(LetExpr("x", NEW(NumExpr(5), NEW(AddExpr(NEW(NumExpr(10), NEW(NumExpr(1)))));
    PTR(Expr) result = let1->subst("x", NEW(NumExpr)(10));

    // Expected result: _let x = 5 _in x + 1
    PTR(LetExpr) expected = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

    CHECK(result->equals(expected)); // This should now pass

    // Test pretty_print()
    CHECK(let1->to_pretty_string() == "_let x = 5\n_in  x + 1");
}

// ====================== BoolExpr Tests ======================
TEST_CASE("BoolExpr tests") {
    PTR(BoolExpr) trueExpr = NEW(BoolExpr)(true);
    PTR(BoolExpr) falseExpr = NEW(BoolExpr)(false);

    // Test equals()
    CHECK(trueExpr->equals(NEW(BoolExpr)(true)) == true);
    CHECK(trueExpr->equals(falseExpr) == false);

    // Test interp()
    PTR(Val) trueVal = trueExpr->interp();
    CHECK(trueVal->to_string() == "_true");
    CHECK(trueVal->equals(NEW(BoolVal)(true)));

    PTR(Val) falseVal = falseExpr->interp();
    CHECK(falseVal->to_string() == "_false");
    CHECK(falseVal->equals(NEW(BoolVal)(false)));

    // Test has_variable()
//    CHECK(trueExpr->has_variable() == false);

    // Test subst()
    CHECK(trueExpr->subst("x", NEW(NumExpr)(10))->equals(trueExpr));

    // Test pretty_print()
    CHECK(trueExpr->to_pretty_string() == "_true");
    CHECK(falseExpr->to_pretty_string() == "_false");
}

// ====================== IfExpr Tests ======================
TEST_CASE("IfExpr tests") {
    PTR(IfExpr) ifTrue = NEW(IfExpr)(NEW(BoolExpr)(true), NEW(NumExpr)(1), NEW(NumExpr)(2));
    PTR(IfExpr) ifFalse = NEW(IfExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(1), NEW(NumExpr)(2));

    // Test equals()
    CHECK(ifTrue->equals(NEW(IfExpr)(NEW(BoolExpr)(true), NEW(NumExpr)(1), NEW(NumExpr)(2))) == true);
    CHECK(ifTrue->equals(ifFalse) == false);

    // Test interp()
    PTR(Val) result1 = ifTrue->interp();
    CHECK(result1->to_string() == "1");  // Condition is true
    CHECK(result1->equals(NEW(NumVal)(1)));

    PTR(Val) result2 = ifFalse->interp();
    CHECK(result2->to_string() == "2");  // Condition is false
    CHECK(result2->equals(NEW(NumVal)(2)));

    // Test has_variable()
//    CHECK(ifTrue->has_variable() == false);

    // Test subst()
    CHECK(ifTrue->subst("x", NEW(NumExpr)(10))->equals(ifTrue));

    // Test pretty_print()
    CHECK(ifTrue->to_pretty_string() == "_if _true\n_then 1\n_else 2");
    CHECK(ifFalse->to_pretty_string() == "_if _false\n_then 1\n_else 2");

    // Test invalid condition (non-boolean)
    PTR(IfExpr) invalidIf = NEW(IfExpr)(NEW(NumExpr)(5), NEW(NumExpr)(1), NEW(NumExpr)(2));
    CHECK_THROWS_WITH(invalidIf->interp(), "Condition must be a boolean");
}

// ====================== EqExpr Tests ======================
TEST_CASE("EqExpr tests") {
    PTR(EqExpr) eq1 = NEW(EqExpr)(NEW(NumExpr)(5), NEW(NumExpr)(5));
    PTR(EqExpr) eq2 = NEW(EqExpr)(NEW(NumExpr)(5), NEW(NumExpr)(6));
    PTR(EqExpr) eq3 = NEW(EqExpr)(NEW(BoolExpr)(true), NEW(BoolExpr)(true));

    // Test equals()
    CHECK(eq1->equals(NEW(EqExpr)(NEW(NumExpr)(5), NEW(NumExpr)(5))) == true);
    CHECK(eq1->equals(eq2) == false);

    // Test interp()
    PTR(Val) result1 = eq1->interp();
    CHECK(result1->to_string() == "_true");  // 5 == 5
    CHECK(result1->equals(NEW(BoolVal)(true)));

    PTR(Val) result2 = eq2->interp();
    CHECK(result2->to_string() == "_false"); // 5 == 6
    CHECK(result2->equals(NEW(BoolVal)(false)));

    PTR(Val) result3 = eq3->interp();
    CHECK(result3->to_string() == "_true");  // true == true
    CHECK(result3->equals(NEW(BoolVal)(true)));

    // Test subst()
    CHECK(eq1->subst("x", NEW(NumExpr)(10))->equals(eq1));

    // Test pretty_print()
    CHECK(eq1->to_pretty_string() == "5 == 5");
    CHECK(eq2->to_pretty_string() == "5 == 6");
    CHECK(eq3->to_pretty_string() == "_true == _true");
}

// ====================== Parser Tests ======================
TEST_CASE("parse") {
    CHECK_THROWS_WITH( parse_str("()"), "bad input" );

    CHECK( parse_str("(1)")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("(((1)))")->equals(NEW(NumExpr)(1)) );

    CHECK_THROWS_WITH( parse_str("(1"), "bad input" );

    CHECK( parse_str("1")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("10")->equals(NEW(NumExpr)(10)) );
    CHECK( parse_str("-3")->equals(NEW(NumExpr)(-3)) );
    CHECK( parse_str("  \n 5  ")->equals(NEW(NumExpr)(5)) );
    CHECK_THROWS_WITH( parse_str("-"), "invalid input" );

    CHECK_THROWS_WITH( parse_str(" -   5  "), "invalid input" );

    CHECK( parse_str("x")->equals(NEW(VarExpr)("x")) );
    CHECK( parse_str("xyz")->equals(NEW(VarExpr)("xyz")) );
    CHECK( parse_str("xYz")->equals(NEW(VarExpr)("xYz")) );
    CHECK_THROWS_WITH( parse_str("x_z"), "invalid input" );

    CHECK( parse_str("x + y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );

    CHECK( parse_str("x * y")->equals(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );

    CHECK( parse_str("z * x + y")
          ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("z"), NEW(VarExpr)("x")),
                           NEW(VarExpr)("y"))) );

    CHECK( parse_str("z * (x + y)")
          ->equals(NEW(MultExpr)(NEW(VarExpr)("z"),
                             NEW(AddExpr)(NEW(VarExpr)("x"),
                                         NEW(VarExpr)("y")))) );

    // Test parsing boolean values
    CHECK( parse_str("_true")->equals(NEW(BoolExpr)(true)) );
    CHECK( parse_str("_false")->equals(NEW(BoolExpr)(false)) );

    // Test parsing equality expressions
    CHECK( parse_str("1 == 2")->equals(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))) );

    // Test parsing if expressions
    CHECK( parse_str("_if _true _then 1 _else 2")
          ->equals(NEW(IfExpr)(NEW(BoolExpr)(true), NEW(NumExpr)(1), NEW(NumExpr)(2))) );
}

// ====================== Additional Tests for Pretty Print ======================
TEST_CASE("Pretty print tests for conditionals") {
    // Test pretty printing of nested if expressions
    PTR(IfExpr) nestedIf = NEW(IfExpr)(
        NEW(BoolExpr)(true),
        NEW(IfExpr)(
            NEW(BoolExpr)(false),
            NEW(NumExpr)(1),
            NEW(NumExpr)(2)
        ),
        NEW(NumExpr)(3)
    );
    CHECK(nestedIf->to_pretty_string() ==
        "_if _true\n"
        "_then _if _false\n"
        "      _then 1\n"
        "      _else 2\n"
        "_else 3");

    // Test pretty printing of if expressions with arithmetic
    PTR(IfExpr) ifWithArithmetic = NEW(IfExpr)(
        NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)),
        NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4)),
        NEW(MultExpr)(NEW(NumExpr)(5), NEW(NumExpr)(6))
    );
    CHECK(ifWithArithmetic->to_pretty_string() ==
        "_if 1 == 2\n"
        "_then 3 + 4\n"
        "_else 5 * 6");

    // Test pretty printing of if expressions with let expressions
    PTR(IfExpr) ifWithLet = NEW(IfExpr)(
        NEW(BoolExpr)(true),
        NEW(LetExpr)("x",
            NEW(NumExpr)(5),
            NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
        ),
        NEW(NumExpr)(10)
    );
    CHECK(ifWithLet->to_pretty_string() ==
        "_if _true\n"
        "_then _let x = 5\n"
        "      _in  x + 1\n"
        "_else 10");
}

// ====================== FunExpr Tests ======================
TEST_CASE("FunExpr basic tests") {
    PTR(FunExpr) fun1 = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    PTR(FunExpr) fun2 = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    PTR(FunExpr) fun3 = NEW(FunExpr)("y", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

    // Test equals()
    CHECK(fun1->equals(fun2) == true);
    CHECK(fun1->equals(fun3) == false);

    // Test interp() - should return a FunVal
    PTR(Val) result = fun1->interp();
    CHECK(CAST(FunVal)(result) != nullptr);
    CHECK(result->to_string() == "[function]");

    // Test subst() - should not substitute shadowed variables
    PTR(Expr) subst1 = fun1->subst("x", NEW(NumExpr)(5));
    CHECK(subst1->equals(fun1)); // x is shadowed by formal arg

    PTR(Expr) subst2 = fun1->subst("y", NEW(NumExpr)(5));
    PTR(Expr) expected = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    CHECK(subst2->equals(expected));

    // Test printExp()
    CHECK(fun1->to_string() == "(_fun (x) (x+1))");

    // Test pretty_print()
    // CHECK(fun1->to_pretty_string() == "_fun (x)\n  x + 1");
}

// ====================== CallExpr Tests ======================
TEST_CASE("CallExpr basic tests") {
    // Simple function application
    PTR(CallExpr) call1 = NEW(CallExpr)(
        NEW(FunExpr)("x",
            NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
        ),
        NEW(NumExpr)(5)
    );

    // Test equals()
    CHECK(call1->equals(
        NEW(CallExpr)(
            NEW(FunExpr)("x",
                NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
            ),
            NEW(NumExpr)(5)
        )
    ) == true);

    // Test interp()
    PTR(Val) result1 = call1->interp();
    CHECK(result1->to_string() == "6"); // (λx.x+1)(5) = 6

    // Test nested function calls
    PTR(CallExpr) nestedCall = NEW(CallExpr)(
        NEW(CallExpr)(
            NEW(FunExpr)("f",
                NEW(FunExpr)("x",
                    NEW(AddExpr)(
                        NEW(CallExpr)(NEW(VarExpr)("f"), NEW(VarExpr)("x")),
                        NEW(NumExpr)(1)
                    )
                )
            ),
            NEW(FunExpr)("y",
                NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))
            )
        ),
        NEW(NumExpr)(3)
    );

    PTR(Val) result2 = nestedCall->interp();
    CHECK(result2->to_string() == "6"); // Should evaluate to 6

    // Test printExp()
    CHECK(call1->to_string() == "(_fun (x) (x+1))(5)");

    // Test pretty_print()
//    CHECK(call1->to_pretty_string() == "(_fun (x)\n  x + 1)(5)");
}

// ====================== FunVal Tests ======================
TEST_CASE("FunVal basic tests") {
    PTR(FunVal) funVal1 = NEW(FunVal)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    PTR(FunVal) funVal2 = NEW(FunVal)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
    PTR(FunVal) funVal3 = NEW(FunVal)("y", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

    // Test equals()
    CHECK(funVal1->equals(funVal2) == true);
    CHECK(funVal1->equals(funVal3) == false);
    CHECK(funVal1->equals(NEW(NumVal)(1)) == false);

    // Test to_string()
    CHECK(funVal1->to_string() == "[function]");

    // Test call()
    PTR(Val) result1 = funVal1->call(NEW(NumVal)(5));
    CHECK(result1->to_string() == "6"); // (λx.x+1)(5) = 6

    // Test invalid operations
    CHECK_THROWS_WITH(funVal1->add_to(NEW(NumVal)(1)), "Cannot add functions");
    CHECK_THROWS_WITH(funVal1->mult_with(NEW(NumVal)(1)), "Cannot multiply functions");
    CHECK_THROWS_WITH(funVal1->is_true(), "Cannot use function as boolean");
}

// ====================== Parser Tests for Functions ======================
TEST_CASE("parse functions") {
    std::string input2 = "_fun (x) x + 1";
    try {
        std::stringstream ss(input2);
        PTR(Expr) parsed = parse(ss);
        PTR(Expr) expected = NEW(FunExpr)("x",
            NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
        CHECK(parsed->equals(expected));
    } catch (const std::exception& e) {
        std::cerr << "Parse failed: " << e.what() << "\n";
        FAIL("Parsing failed");
    }

    // Test parsing function calls
    CHECK( parse_str("f(x)")->equals(
        NEW(CallExpr)(NEW(VarExpr)("f"), NEW(VarExpr)("x"))
    ));

    // Test parsing nested function calls
    CHECK( parse_str("f(g)(x)")->equals(
        NEW(CallExpr)(
            NEW(CallExpr)(NEW(VarExpr)("f"), NEW(VarExpr)("g")),
            NEW(VarExpr)("x")
        )
    ));

    // Test parsing function with body containing another function
    CHECK( parse_str("_fun (x) _fun (y) x + y")->equals(
        NEW(FunExpr)("x",
            NEW(FunExpr)("y",
                NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))
            )
        )
    ));

    // Test parsing function application with complex arguments
    CHECK( parse_str("f(x + 1)")->equals(
        NEW(CallExpr)(
            NEW(VarExpr)("f"),
            NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
        )
    ));

    SECTION("Simple function") {
        std::string input1 = "_fun (x) x + 1";
        try {
            PTR(Expr) parsed = parse_str(input1);
            PTR(Expr) expected = NEW(FunExpr)("x",
                NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
            );
            CHECK(parsed->equals(expected));
        } catch (const std::exception& e) {
            FAIL("Exception thrown: " << e.what());
        }
    }
}

// ====================== Factorial Example Test ======================
//TEST_CASE("Factorial example") {
//    std::string factorial =
//        "_let factrl = _fun (factrl)\n"
//        "                _fun (x)\n"
//        "                  _if x == 1\n"
//        "                  _then 1\n"
//        "                  _else x * factrl(factrl)(x + -1)\n"
//        "_in factrl(factrl)(10)";
//
//    Expr* expr = parse_str(factorial);
//    Val* result = expr->interp();
//    CHECK(result->to_string() == "3628800");
//}