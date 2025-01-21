//
// Created by Brandon Mountan on 1/21/25.
//
#include "catch.h"
#include "expr.h"
#include <memory>

TEST_CASE("NumExpr equals") {
    CHECK((std::make_shared<NumExpr>(5))->equals(std::make_shared<NumExpr>(5)) == true);
    CHECK((std::make_shared<NumExpr>(5))->equals(std::make_shared<NumExpr>(6)) == false);
}

TEST_CASE("AddExpr equals") {
    auto add1 = std::make_shared<AddExpr>(std::make_shared<NumExpr>(2), std::make_shared<NumExpr>(3));
    auto add2 = std::make_shared<AddExpr>(std::make_shared<NumExpr>(2), std::make_shared<NumExpr>(3));
    auto add3 = std::make_shared<AddExpr>(std::make_shared<NumExpr>(3), std::make_shared<NumExpr>(2));

    CHECK(add1->equals(add2) == true);
    CHECK(add1->equals(add3) == false);
}

TEST_CASE("MultExpr equals") {
    auto mult1 = std::make_shared<MultExpr>(std::make_shared<NumExpr>(2), std::make_shared<NumExpr>(3));
    auto mult2 = std::make_shared<MultExpr>(std::make_shared<NumExpr>(2), std::make_shared<NumExpr>(3));
    auto mult3 = std::make_shared<MultExpr>(std::make_shared<NumExpr>(3), std::make_shared<NumExpr>(2));

    CHECK(mult1->equals(mult2) == true);
    CHECK(mult1->equals(mult3) == false);
}

TEST_CASE("VarExpr equals") {
    CHECK((std::make_shared<VarExpr>("x"))->equals(std::make_shared<VarExpr>("x")) == true);
    CHECK((std::make_shared<VarExpr>("x"))->equals(std::make_shared<VarExpr>("y")) == false);
    CHECK((std::make_shared<NumExpr>(1))->equals(std::make_shared<VarExpr>("x")) == false);
}
