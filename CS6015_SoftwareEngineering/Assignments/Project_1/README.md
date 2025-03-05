# MSDscript Project

## Project Phase 1: MSDscript Command Line

In this phase, you create your project repository and start out with a simple, multi-file C++ program that looks at command-line arguments.

### Requirements
- Implement a function `use_arguments` to handle command-line arguments.
- If `--help` is provided, print help text and exit.
- If `--test` is provided, print `Tests passed` (but only once, otherwise print an error and exit).
- If any other argument is provided, print an error and exit.
- If no arguments trigger an error, return normally.
- Implement the function declaration in `cmdline.h` and the definition in `cmdline.cpp`.
- Create a `Makefile` that compiles `main.cpp` and `cmdline.cpp` into an executable named `msdscript`.

## Project Phase 2: Expression Classes

### Requirements
- Implement expression classes (`NumExpr`, `AddExpr`, `MultExpr`, `VarExpr`).
- Implement the `equals` method for all expression classes.
- Modify the command-line handler to run tests if `--test` is passed.
- Integrate `catch.h` for unit testing.
- Add tests for `equals`, ensuring correctness across all expression types.
- Ensure GitHub repo is updated, and submit code and GitHub link on Canvas.

## Project Phase 3: Interpret

### Requirements

#### 1. Implement `interp`
- Returns an integer value for an expression.
    - `NumExpr`: Returns the number itself.
    - `AddExpr`: Returns the sum of subexpression values.
    - `MultExpr`: Returns the product of subexpression values.
    - `VarExpr`: Throws `std::runtime_error` since variables have no values.
- Example tests:
  ```cpp
  CHECK( (new MultExpr(new NumExpr(3), new NumExpr(2)))->interp() == 6 );
  CHECK( (new AddExpr(new AddExpr(new NumExpr(10), new NumExpr(15)), new AddExpr(new NumExpr(20), new NumExpr(20))))->interp() == 65 );
  CHECK_THROWS_WITH( (new VarExpr("x"))->interp(), "no value for variable" );
  ```

#### 2. Implement `has_variable`
- Returns `true` if the expression contains a variable, `false` otherwise.
- Example tests:
  ```cpp
  CHECK( (new AddExpr(new VarExpr("x"), new NumExpr(1)))->has_variable() == true );
  CHECK( (new MultExpr(new NumExpr(2), new NumExpr(1)))->has_variable() == false );
  ```

#### 3. Implement `subst`
- Replaces occurrences of a given variable in the expression with another expression.
- Should return a new object without modifying the original.
- Example tests:
  ```cpp
  CHECK( (new AddExpr(new VarExpr("x"), new NumExpr(7)))->subst("x", new VarExpr("y"))->equals(new AddExpr(new VarExpr("y"), new NumExpr(7))) );
  CHECK( (new VarExpr("x"))->subst("x", new AddExpr(new VarExpr("y"), new NumExpr(7)))->equals(new AddExpr(new VarExpr("y"), new NumExpr(7))) );
  ```

### Notes
- Do not modify existing objects; always return a new one.
- Ensure comprehensive testing.
- Update the GitHub repo and submit code along with the repository link on Canvas.

