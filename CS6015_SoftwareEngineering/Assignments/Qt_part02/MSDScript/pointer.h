//
// Created by Brandon Mountan on 4/1/25.
//

#ifndef __msdscript_pointer__
#define __msdscript_pointer__

#include <memory>

// Forward declarations
class Expr;
class Val;
class Env;

#define USE_PLAIN_POINTERS 0
#if USE_PLAIN_POINTERS

# define NEW(T)    new T
# define PTR(T)    T*
# define CAST(T)   dynamic_cast<T*>
# define CLASS(T)  class T
# define THIS      this

#else

# define NEW(T)    std::make_shared<T>
# define PTR(T)    std::shared_ptr<T>
# define CAST(T)   std::dynamic_pointer_cast<T>
# define CLASS(T)  class T : public std::enable_shared_from_this<T>
# define THIS      shared_from_this()

#endif

#endif