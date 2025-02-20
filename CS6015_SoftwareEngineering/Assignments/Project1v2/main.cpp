#define CATCH_CONFIG_RUNNER
#include "catch.h"

int main(int argc, char* argv[]) {
    for (int i = 1; i < argc; ++i) {
        if (std::string(argv[i]) == "--test") {
            int result = Catch::Session().run();
            if (result != 0) {
                exit(1);
            }
            return 0;
        }
    }


    return 0;
}

// expr *e
// .......