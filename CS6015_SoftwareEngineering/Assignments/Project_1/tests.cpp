//
// Created by Brandon Mountan on 1/21/25.
//
#include "catch.h"
#include "expr.h"

TEST_CASE("NumExpr equals") {
    NumExpr* num1 = new NumExpr(5);
    NumExpr* num2 = new NumExpr(5);
    NumExpr* num3 = new NumExpr(6);

    CHECK(num1->equals(num2) == true); // Should return true
    CHECK(num1->equals(num3) == false); // Should return false
}

TEST_CASE("AddExpr equals") {
    AddExpr* add1 = new AddExpr(new NumExpr(2), new NumExpr(3));
    AddExpr* add2 = new AddExpr(new NumExpr(2), new NumExpr(3));
    AddExpr* add3 = new AddExpr(new NumExpr(3), new NumExpr(2));

    CHECK(add1->equals(add2) == true);  // Should return true
    CHECK(add1->equals(add3) == false); // Should return false
}

TEST_CASE("MultExpr equals") {
    MultExpr* mult1 = new MultExpr(new NumExpr(2), new NumExpr(3));
    MultExpr* mult2 = new MultExpr(new NumExpr(2), new NumExpr(3));
    MultExpr* mult3 = new MultExpr(new NumExpr(3), new NumExpr(2));

    CHECK(mult1->equals(mult2) == true);  // Should return true
    CHECK(mult1->equals(mult3) == false); // Should return false
}

TEST_CASE("VarExpr equals") {
    VarExpr* var1 = new VarExpr("x");
    VarExpr* var2 = new VarExpr("x");
    VarExpr* var3 = new VarExpr("y");
    NumExpr* num = new NumExpr(1);

    CHECK(var1->equals(var2) == true);   // Should return true
    CHECK(var1->equals(var3) == false);  // Should return false
    CHECK(var1->equals(num) == false);   // Should return false since types differ
}
