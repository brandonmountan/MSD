#include <iostream>
#include "parse.hpp"

Expr *parse_num(std::istream &inn) {
    
    int n = 0;
    
    while (1) {
        int c = inn.get();

        if (isdigit(c))
        {
            n = n*10 + (c - '0');
        }
        else
            break;
    }
    
    return new NumExpr(n);
}
