#include "catch.h"
#include "expr.h"
#include "parse.hpp"
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

TEST_CASE("Pretty print tests") {
    // Test pretty printing with proper spacing and minimal parentheses
    CHECK((new MultExpr(new NumExpr(1), new AddExpr(new NumExpr(2), new NumExpr(3))))->to_string() == "(1*(2+3))");
    CHECK((new MultExpr(new MultExpr(new NumExpr(8), new NumExpr(1)), new VarExpr("y")))->to_string() == "((8*1)*y)");
    CHECK((new MultExpr(new AddExpr(new NumExpr(3), new NumExpr(5)), new MultExpr(new NumExpr(6), new NumExpr(1))))->to_string() == "((3+5)*(6*1))");
    CHECK((new MultExpr(new MultExpr(new NumExpr(7), new NumExpr(7)), new AddExpr(new NumExpr(9), new NumExpr(2))))->to_string() == "((7*7)*(9+2))");

    // Test pretty printing with spaces and minimal parentheses
    CHECK((new MultExpr(new NumExpr(1), new AddExpr(new NumExpr(2), new NumExpr(3))))->to_pretty_string() == "1 * (2 + 3)");
    CHECK((new MultExpr(new MultExpr(new NumExpr(8), new NumExpr(1)), new VarExpr("y")))->to_pretty_string() == "(8 * 1) * y");
    CHECK((new MultExpr(new AddExpr(new NumExpr(3), new NumExpr(5)), new MultExpr(new NumExpr(6), new NumExpr(1))))->to_pretty_string() == "(3 + 5) * 6 * 1");
    CHECK((new MultExpr(new MultExpr(new NumExpr(7), new NumExpr(7)), new AddExpr(new NumExpr(9), new NumExpr(2))))->to_pretty_string() == "(7 * 7) * (9 + 2)");
}

TEST_CASE("LetExpr pretty_print") {
    Expr* expr = new LetExpr("x", new NumExpr(5), new AddExpr(new LetExpr("y", new NumExpr(3), new AddExpr(new VarExpr("y"), new NumExpr(2))), new VarExpr("x")));
    CHECK(expr->to_pretty_string() == "_let x = 5\n_in  (_let y = 3\n      _in  y + 2) + x");
}

TEST_CASE("LetExpr basic tests") {
    LetExpr* let1 = new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));
    LetExpr* let2 = new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));
    LetExpr* let3 = new LetExpr("y", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));

    // Test equals()
    CHECK(let1->equals(let2) == true);
    CHECK(let1->equals(let3) == false);

    // Test interp()
    CHECK(let1->interp() == 6);  // (5 + 1) = 6

    // Test has_variable()
    CHECK(let1->has_variable() == true);  // Body has variable 'x'
    CHECK((new LetExpr("x", new NumExpr(5), new NumExpr(10)))->has_variable() == false);
}

TEST_CASE("LetExpr substitution with unbound variable") {
    LetExpr* let1 = new LetExpr("x", new VarExpr("y"), new AddExpr(new VarExpr("x"), new NumExpr(1)));

    // Substitute y (unbound variable) - should affect rhs
    Expr* substituted = let1->subst("y", new NumExpr(5));

    // Check that the substituted expression evaluates correctly
    CHECK(substituted->interp() == 6);  // let x = 5 in (x + 1)
}

TEST_CASE("LetExpr pretty print - basic") {
    LetExpr* let1 = new LetExpr("x", new NumExpr(5), new AddExpr(new VarExpr("x"), new NumExpr(1)));
    CHECK(let1->to_pretty_string() == "_let x = 5\n_in  x + 1");
}

TEST_CASE("LetExpr pretty print - nested") {
    LetExpr* nestedLet = new LetExpr("x", new NumExpr(2),
        new LetExpr("y", new NumExpr(3),
            new AddExpr(new VarExpr("x"), new VarExpr("y"))));

    CHECK(nestedLet->to_pretty_string() ==
        "_let x = 2\n"
        "_in  _let y = 3\n"
        "     _in  x + y");
}

TEST_CASE("LetExpr edge cases") {
    // Unused variable
    LetExpr* unusedVar = new LetExpr("x", new NumExpr(5), new NumExpr(10));
    CHECK(unusedVar->interp() == 10);
    CHECK(unusedVar->to_pretty_string() == "_let x = 5\n_in  10");

    // Let rhs with variable
    LetExpr* rhsVar = new LetExpr("x", new VarExpr("y"),
        new AddExpr(new VarExpr("x"), new NumExpr(1)));
    CHECK(rhsVar->has_variable() == true);
    CHECK_THROWS_WITH(rhsVar->interp(), "Variable has no value");

    // Let in let
    LetExpr* letInLet = new LetExpr("x", new NumExpr(5),
        new LetExpr("x", new NumExpr(6),
            new VarExpr("x")));
    CHECK(letInLet->interp() == 6);  // Inner shadowing
    CHECK(letInLet->to_pretty_string() ==
        "_let x = 5\n"
        "_in  _let x = 6\n"
        "     _in  x");
}

TEST_CASE("LetExpr in complex expressions") {
    // Let as part of multiplication
    MultExpr* multWithLet = new MultExpr(
        new LetExpr("x", new NumExpr(5),
            new AddExpr(new VarExpr("x"), new NumExpr(1))),
        new NumExpr(2));

    CHECK(multWithLet->interp() == 12);  // (5+1)*2 = 12
    CHECK(multWithLet->to_pretty_string() ==
        "(_let x = 5\n"
        " _in  x + 1) * 2");

    // Let in let with arithmetic
    Expr* complexLet = new LetExpr("a", new NumExpr(3),
        new AddExpr(
            new LetExpr("b", new NumExpr(4),
                new MultExpr(new VarExpr("a"), new VarExpr("b"))),
            new NumExpr(5)));

    CHECK(complexLet->interp() == 17);  // (3*4)+5 = 17
    CHECK(complexLet->to_pretty_string() ==
        "_let a = 3\n"
        "_in  (_let b = 4\n"
        "      _in  a * b) + 5");
}

/* Some ideas for parse tests, probably not enough. Also, you may need
   to adjust these tests, and there may even be mistakes here. */

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

    // This was some temporary debugging code:
    //  std::istringstream in("-");
    //  parse_num(in)->print(std::cout); std::cout << "\n";

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

}
