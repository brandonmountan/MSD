#include <iostream>
#include <time.h>

static std::string random_bytes();

int main(int argc, char **argv) {
    srand (clock());
    for (int i = 0; i < 10; i++) {
        std::string rand_str = random_bytes();
        std::cout << "Output: "<< rand_str << "\n";
    }

return 0;
}

std::string random_bytes() {
    std::string word = "";
    for (int i = rand() % 32; i-- > 0; )
        word += rand() % 256;
    return word;
}


