#include <stdio.h>

int doit( int a, int b, int c ) {
    int result = a + b + c; // result -> rax
    return result;

    // doit:
    // add rdi, rsi     a = a + b
    // add rdi, rdx     a = a + c
    // mov rax, rdi
    // jmp main

    // mov rcx, rbx     save bx as it must be restored
    // mov rbx, rdi
    // add rbx, rsi
    // add rbx, rdx
    // mov rax, rbx
    // mov rbx, rcx     restore bx
    // ret
};
int main(void) {

    doit( 9, 99, 123 );

    // main:
    //  mov rdi, 9
    //  mov rsi, 99
    //  mov rdx, 123
    // ...
    //  mov r9, 3       put 6th pram into appropriate register
    //  push 5
    //  call doit
    // ret

    // dont_doit:


    return 0;
}

// pass data into registers so it's all there and can immediately be used