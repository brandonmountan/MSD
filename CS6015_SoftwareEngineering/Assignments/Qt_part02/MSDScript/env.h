//
// Created by Brandon Mountan on 4/9/25.
//

#ifndef ENV_H
#define ENV_H

#include <string>
#include "pointer.h"
#include "val.h"

CLASS(Env) {
    public:
    virtual ~Env() = default;
    virtual PTR(Val) lookup(std::string find_name) = 0;

    static PTR(Env) empty;
};

class EmptyEnv : public Env {
public:
    PTR(Val) lookup(std::string find_name) override;
};

class ExtendedEnv : public Env {
    std::string name;
    PTR(Val) val;
    PTR(Env) rest;
public:
    ExtendedEnv(std::string name, PTR(Val) val, PTR(Env) rest);
    PTR(Val) lookup(std::string find_name) override;
};



#endif //ENV_H
