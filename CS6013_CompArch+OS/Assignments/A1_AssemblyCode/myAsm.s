section .rodata
    ; Constant strings and data                                                                                          
    helloString: db "hello", 0     ; Null-terminated string "hello"                                                      
    newLine: db 10                ; Newline character                                                                    

section .text
    extern puts                   ; Declare external function `puts`                                                     
    global sayHello               ; Export `sayHello` for linker                                                         
    global myPuts                 ; Export `myPuts` for linker                                                           
    global myGTOD                 ; Export `myGTOD` for linker                                                           

; Function: sayHello                                                                                                     
; Purpose: Print "hello" using the C library function `puts`.                                                            
sayHello:
    push rbp                      ; Save base pointer                                                                    
    mov rbp, rsp                  ; Establish stack frame                                                                
    mov rdi, helloString          ; Argument: pointer to "hello" string                                                  
    call puts                     ; Call `puts` to print the string                                                      
    pop rbp                       ; Restore base pointer                                                                 
    ret                           ; Return to caller                                                                     

; Function: myPuts                                                                                                       
; Purpose: Write a string to stdout using the `write` syscall.                                                           
; Input:                                                                                                                 
;   rdi - Pointer to the string                                                                                          
;   rsi - Length of the string                                                                                           
myPuts:
    ; Write the string to stdout                                                                                         
    mov rax, 1                    ; Syscall number for `write`                                                           
    mov rdx, rsi                  ; Length of the string                                                                 
    mov rsi, rdi                  ; Pointer to the string                                                                
    mov rdi, 1                    ; File descriptor: stdout                                                              
    syscall                       ; Perform the syscall                                                                  

    ; Write a newline to stdout                                                                                          
    mov rax, 1                    ; Syscall number for `write`                                                           
    mov rsi, newLine              ; Pointer to newline character                                                         
    mov rdx, 1                    ; Length of newline                                                                    
    mov rdi, 1                    ; File descriptor: stdout                                                              
    syscall                       ; Perform the syscall                                                                  

    ret                           ; Return to caller                                                                     

; Function: myGTOD                                                                                                       
; Purpose: Get the current time of day using the `gettimeofday` syscall.                                                 
; Output:                                                                                                                
;   rax - Seconds since the epoch                                                                                        
;   rdx - Microseconds part of the time                                                                                  
myGTOD:
    sub rsp, 16                   ; Allocate space on the stack                                                          
    mov rdi, rsp                  ; First argument: pointer to struct timeval                                            
    xor rsi, rsi                  ; Second argument: timezone (NULL)                                                     
    mov rax, 96                   ; Syscall number for `gettimeofday`                                                    
    syscall                       ; Perform the syscall                                                                  

    ; Load results into rax and rdx                                                                                      
    mov rax, [rsp]                ; Load seconds from struct timeval                                                     
    mov rdx, [rsp + 8]            ; Load microseconds from struct timeval                                                
    add rsp, 16                   ; Deallocate stack space                                                               

    ret                           ; Return to caller  