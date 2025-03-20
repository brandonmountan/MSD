NOTE: Before you get started with this assignment, it is highly recommended that you work through (at least the beginnings of) the NASM tutorialLinks to an external site..

CADE Lab Machines

Create your CADE account here.Links to an external site.
If you need to come in from off campus (or via a VPN), the CADE ssh login will ask you for a code.  To get this code, you need an "Authenticator" app on your phone.  To se up the code in that app, you need to follow these instructions: https://www.cade.utah.edu/cade-2fa-setup/Links to an external site.
We will be using the CADE Lab Linux computers for this assignment.  There quite a few of these machines that you can log into remotely.  Their names are:

lab1-X (where X is any number from 1-40)
lab2-Y (where Y is any number from 1-35)

To access them, use this command:

ssh <userid>@lab1-11.eng.utah.edu

Then use emacs, vi[m], nano, or your favorite text editor to write your assembly code.  You'll need to remember some of the basic shell commands such as mkdir, cd, ls, etc.

Writing Some Assembly

In this assignment, we'll write a little bit of assembly code to understand how user-mode and kernel-mode functions are called at a low level.

For this assignment we'll write 3 assembly functions:

one that prints out a message using the cstdlib function puts( char* )
our own version of puts( char* ) that prints a string to the console. We'll add an extra int parameter that's the string's length to simplify our assembly code.  Our function's declaration will look like: int myPuts( const char* s, int len )
one that gets the time of day using a system call
We'll be compiling everything from the command line so that we have total control over the process.

Our code will be separated into 2 files, a cpp file containing main() that calls our functions, and a .s (common suffix for assembly files) file containing the implementation of our myPuts() function.

Warning: this is NOT a typical assignment. You will be learning some new material as you go along. Be patient, and read carefully.

Part 1 sayHello()
In the first part, we'll write a function in assembly that prints hello to the console,

We'll start by writing the C++ code that calls our (as yet unwritten) assembly function.

In main.cpp, in the main() function, call your sayHello function (which is your at-yet-to-be-written assembly function). Try to compile your C++ code. You should get a compiler error saying sayHello is an undeclared identifier. We need to declare it before using it. Before main, declare the function (do not define it - the definition (ie: implementation) will be in assembly!). Before you compile your program again, think about what kind of error you expect to see.

You should have gotten a linker error saying that the symbol for sayHello was not found. This is because we told the C++ compiler that the function existed (declared it), but we never defined it anywhere. This is because g++ is trying to make a complete executable. We're not ready to do that, so we'll tell g++ to:

1. Compile this .cpp file into an object file, and

2. Have the linker connect up our object files later on.

To accomplish 1) (generating the object file), add the -c flag when you call g++, and you'll get an output file with the same basename as your .cpp file with a .o extension.

Now, let's actually define sayHello in your assembly (.s) file.

Create a new file myAsm.s. In that file, we'll write the implementation of our function in assembly!

We'll add a skeleton for our sayHello function to our asm file:

section .text  ; this says that we're about to write code (as opposed to data)

global sayHello ; this says that "sayHello" is a symbol that the linker needs to know about

sayHello:           ; and here we go...

;; the function's code goes here

ret                 ; just return (for now)
Note: comments start with a ; (semicolon). The implementation of our code goes after sayHello: (the label specifying where the code of our function starts). It is important to know that in assembly we don't explicitly say anything about the parameters/return type of the function. The linker doesn't really know anything about that. If we want our code to work when we call it from C++ we'd better make sure we follow the ABI that it's expecting!

At this point, we are not actually doing anything in our function, but we're going to fit the different pieces together now.

Our assembly file is in assembly language, which is ALMOST machine code, but not quite. Like we would "compile" a C++ file with a compiler, we'll "assemble" our assembly file with an assembler.

The assembler we'll use is called NASM.

Run the assembler with: nasm -felf64 myAsm.s The flag -felf64 tells the nasm program to use the Linux elf output format using the 64-bit version. This should produce a file myAsm.o. As far as the linker is concerned, the object code (.o files) that comes out of the assembler (nasm) or the compiler (g++) works the same.

Finally, we'll link them into a final executable that we can run!  Note, g++ will also work as a linker:

g++ -o mySyscallProgram yourObjectFromClang.o yourObjectFromNasm.o

Unfortunately, you'll get linker errors after running this command because the function you defined in your assembly file doesn't quite match the name that g++ expected based on your declaration. We'll need to fix it in 2 steps.

Name Mangling

[Note: Name mangling is done differently depending on the machine / compiler.  The use of _ mentioned below happens when compiling on some machines (such as Macs), but does not appear to happen on the CADE linux machines.]

All C functions have an _ stuck before their name. This is called name mangling, since the names get changed -- mangled -- by the compiler.

Since sayHello is defined in Assembly and not directly accessible to the compiler, you'll have to mangle the name yourself! Add an underscore before sayHello in the 2 places it shows up in your assembly file (the global declaration and the label with the colon). Re-assemble your assembly file and try linking again. It still doesn't work!

The issue is that we're using C++, not C. In order to make function overloading work (many functions with the same name but different parameters), C++ does super weird name mangling so the actual symbol name that the linker is expecting to see for sayHello is something like __z8sayHellospci or something similar. We can tell the compiler not to do that by wrapping the declaration in your .cpp file in "extern C," which tells the compiler to use C's name mangling rules.

extern "C" {

void sayHello();

}
Recompile your .cpp file and then try to relink your .o files. You should have a functioning executable that does... nothing!

Making sayHello work
To make sayHello print hello when we call it, it needs to do the C++ equivalent of puts( "hello" );

As we mentioned in class, to call a function we use the call asm instruction, and the arguments need to be placed according to the ABI. We're on 64-bit Linux machines with Intel processors, whose calling conventions are described here. Before you freak out about all the details: we're using the puts function which has only 1 parameter, and we're ignoring return values, etc (because our function returns void and we're ignoring error checking).

So, the skeleton of our function is

move our char* (pointing to hello) to the right place as specified by the ABI
call puts
return
Note, to tell nasm that puts is an externally provided function, we must write extern puts at the top of our assembly code file.

How do we get "hello" into our program? NASM lets us specify it using this syntax:

section .rodata  ; this is the read only data ('hello', below, is a constant)

helloString: db "hello",0 ; helloString is the name of our symbol

; db is the directive to the assembler to put data in our object file
; the 0 is the null terminator that puts is expecting.  
; nasm does NOT null terminate our string automatically
Now, anywhere we use helloString, it refers to the address of the 'h' (the first character of hello).

You should now be able to assemble, link, and run your program and it should now successfully print hello when executed!

Making a system call
Next, we'll write our own implementation of the puts call we used in the last step, called myPuts (see top of this assignment for myPuts signature).

Since our process is running in user mode, we're not allowed to touch the console directly. Only the kernel can do I/O. To print our string to the console, we need to make a write system call using a syscall. This syscall will follow the Linux syscall ABI conventions.

To make a write system call under the Linux ABI:

Put the system call number in the rax register. You can find the list of syscall numbers hereLinks to an external site..
Note: the assembler interprets numbers starting with 0x as hex, which is handy.

Put the parameters in the appropriate registers. We'll only be dealing with integer/pointer parameters, so the parameters go in (in order) rdi, rsi, rdx, rcx, etc. You should only need 3 parameters.

execute the syscall instruction. This will cause a trap (user mode is calling a privileged instruction, so the CPU alerts the kernel), and the kernel's exception handler will perform the sycall. It will put the return value (an int saying how many characters were actually printed) in rax.

since we're returning the same value that the syscall returned, we can actually just return! The value is already in rax! The "ret" instruction will jump us back to the return address. (We didn't need to touch anything on the stack so there's no prologue or epilogue for this function). Unlike above, syscall does not push the return address onto the stack, so we don't have to deal with stack alignment.

Follow those 4 steps to execute a write() syscall. Remember that the file descriptor is an int telling the kernel where we want to write our data. To write to the console, we should use the descriptor for stdout.

The parameters to our function are also passed to us in registers. We'll get our first parameter in rdi, rsi, rdx, rcx, etc. Again, we should only need to look at the first 2.

You should only need to use the mov, syscall, and ret instructions to implement this. My implementation is ~8 instructions.

It may be helpful to draw a picture of what's in each register when you start, and how things need to get moved around before you make your syscall.

Test myPuts by calling it from main (you'll need to follow the instructions from part 1 to declare it properly in your .cpp file as well as assemble and link everything together).


A slightly trickier syscall
Once you have your myPuts() implementation working, we're going to rewrite another syscall, gettimeofday(); The interface to gettimeofday is kind of obnoxious, so we're actually going to improve the interface somewhat.

Look at the manpage for gettimeofday (run man gettimeofday, or google "man gettimeofday") to get a sense for how it works.

The signature is int gettimeofday(timeval* tv, timezone* tz); The function takes pointers to structs that will be filled in by the system call. The return value is 0 if the call was successfull and -1 if there was some sort of error (I have no idea how/why this syscall would ever fail).

Assuming we don't care about the time zone, we can define a function with a much nicer signature:

timeval myGTOD(); //much nicer

We're going to implement this function in assembly.

Follow the steps above to call your new function from your C++ code and print out the seconds/usecs that you get back. Note you'll need to #include <sys/time.h> to get the definition of timeval. It looks basically like this (hidden behind some platform dependent types):

struct timeval{

long tv_sec; //seconds since January 1, 1970

long tv_usec;  //microseconds

};
Add a stub (empty) definition for your function to your assembly file. Compile, assemble, and link, then run everything. You should get weird values in the timeval struct you print.

So how are we going to implement our function? In order to get the time of day, we need to call the syscall like we did above. The gettimeofday syscall expects a pointer to a timeval struct... where should we put it? It will actually be very convenient for us to put it on our stack. For the timezone parameter, we'll just pass a null pointer (just put 0 in the register corresponding to that register).

When we return we hit a weird-ish part of the ABI. To return this particular struct, we will stick the seconds field in rax, and the microseconds field in rdx.

So, the overall outline for the function looks like this:

make space on the struct for our timeval struct (you allocate space on the stack by subtracting the number of bytes you want from rsp. In this case, 16)
Stick the parameters in the appropriate place for the gettimeofday syscall
execute a syscall instruction
move the values in our timeval struct (which should be at memory locations [rsp] and [rsp + 8]) into rax and rdx so that we can return.
give back the stack space (by adding to rsp)
then return with ret
This function is slightly longer: mine is 9 instructions (disregarding the prologue/epilogue). It should require 1 add, 1 sub, a few movs, a syscall, and a ret.

You can confirm that your function works by calling gettimeofday using the normal syscall from your C++ code. You should expect the seconds field to be the same (or possibly off by 1 if you're lucky).