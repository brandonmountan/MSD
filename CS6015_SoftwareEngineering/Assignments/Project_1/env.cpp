//
// Created by Brandon Mountan on 4/9/25.
//

#include "env.h"
#include <stdexcept>

PTR(Env) Env::empty = NEW(EmptyEnv)();

PTR(Val) EmptyEnv::lookup(std::string find_name) {
    throw std::runtime_error("Free variable: " + find_name);
}

ExtendedEnv::ExtendedEnv(std::string name, PTR(Val) val, PTR(Env) rest)
    : name(name), val(val), rest(rest) {}

PTR(Val) ExtendedEnv::lookup(std::string find_name) {
    if (find_name == name) {
        return val;
    }
    return rest->lookup(find_name);
}