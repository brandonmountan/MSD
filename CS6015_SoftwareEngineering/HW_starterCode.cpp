#include <iostream>
#include <time.h>

static std::string random_expr_string();
static std::string random_variable();

int main(int argc, char **argv) {
    srand (clock());
    for (int i = 0; i < 10; i++) {
        std::string s = "";
        s=(rand() % 26) + 'a';
        std::string in = random_expr_string();
        std::cout << "Add/Num expr: "<< in << "\n";
        std::cout << "Variable: "<< s <<std::endl;
    }

return 0;
}

std::string random_expr_string() {
    if ((rand() % 10) < 6)
        return std::to_string(rand());
    else
        return random_expr_string() + "+" + random_expr_string();
}
