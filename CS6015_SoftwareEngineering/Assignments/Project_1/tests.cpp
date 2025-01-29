#include "catch.h"
#include "expr.h"
#include <stdexcept>

TEST_CASE("NumExpr tests") {
    NumExpr* num1 = new NumExpr(5);
    NumExpr* num2 = new NumExpr(5);
    NumExpr* num3 = new NumExpr(6);

    // Test equals()
    CHECK(num1->equals(num2) == true);
    CHECK(num1->equals(num3) == false);

    // Test interp()
    CHECK(num1->interp() == 5);

    // Test has_variable()
    CHECK(num1->has_variable() == false);

    // Test subst()
    CHECK(num1->subst("x", new NumExpr(10))->equals(num1));
}

TEST_CASE("AddExpr tests") {
    AddExpr* add1 = new AddExpr(new NumExpr(2), new NumExpr(3));
    AddExpr* add2 = new AddExpr(new NumExpr(2), new NumExpr(3));
    AddExpr* add3 = new AddExpr(new NumExpr(3), new NumExpr(2));
    AddExpr* addWithVar = new AddExpr(new VarExpr("x"), new NumExpr(5));

    // Test equals()
    CHECK(add1->equals(add2) == true);
    CHECK(add1->equals(add3) == false);

    // Test interp()
    CHECK(add1->interp() == 5);  // 2 + 3 = 5

    // Test has_variable()
    CHECK(add1->has_variable() == false);
    CHECK(addWithVar->has_variable() == true);

    // Test subst()
    Expr* replaced = addWithVar->subst("x", new NumExpr(10));  // Replace "x" with 10
    CHECK(replaced->equals(new AddExpr(new NumExpr(10), new NumExpr(5))));

    // Nested substitution test
    CHECK((new AddExpr(new VarExpr("x"), new NumExpr(7)))
          ->subst("x", new VarExpr("y"))
          ->equals(new AddExpr(new VarExpr("y"), new NumExpr(7))));
}

TEST_CASE("MultExpr tests") {
    MultExpr* mult1 = new MultExpr(new NumExpr(2), new NumExpr(3));
    MultExpr* mult2 = new MultExpr(new NumExpr(2), new NumExpr(3));
    MultExpr* mult3 = new MultExpr(new NumExpr(3), new NumExpr(2));
    MultExpr* multWithVar = new MultExpr(new VarExpr("x"), new NumExpr(5));

    // Test equals()
    CHECK(mult1->equals(mult2) == true);
    CHECK(mult1->equals(mult3) == false);

    // Test interp()
    CHECK(mult1->interp() == 6);  // 2 * 3 = 6

    // Test has_variable()
    CHECK(mult1->has_variable() == false);
    CHECK(multWithVar->has_variable() == true);

    // Test subst()
    Expr* replaced = multWithVar->subst("x", new NumExpr(10));  // Replace "x" with 10
    CHECK(replaced->equals(new MultExpr(new NumExpr(10), new NumExpr(5))));
}

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
    CHECK(var1->has_variable() == true);

    // Test subst()
    Expr* replacement = var1->subst("x", new NumExpr(10));
    CHECK(replacement->equals(new NumExpr(10)));

    Expr* unchanged = var1->subst("y", new NumExpr(10));
    CHECK(unchanged->equals(var1));
}

TEST_CASE("Complex nested expressions") {
    // Nested AddExpr and MultExpr
    Expr* expr = new AddExpr(
        new MultExpr(new NumExpr(2), new VarExpr("x")),
        new NumExpr(5)
    );

    // Test has_variable()
    CHECK(expr->has_variable() == true);

    // Test subst()
    Expr* substituted = expr->subst("x", new NumExpr(10));
    CHECK(substituted->equals(new AddExpr(
        new MultExpr(new NumExpr(2), new NumExpr(10)),
        new NumExpr(5)
    )));

    // Test interp() - Check exception for variables
    CHECK_THROWS_WITH(expr->interp(), "Variable has no value");

    // Substituted expression interp
    substituted = expr->subst("x", new NumExpr(3));
    CHECK(substituted->interp() == 11);  // (2 * 3) + 5 = 11
}
