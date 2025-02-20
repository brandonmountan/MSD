#include <iostream>   // For input/output (e.g., std::cout, std::cin)
#include <vector>     // For using dynamic arrays (e.g., std::vector)
#include <string>     // For using strings (e.g., std::string)
#include <unistd.h>   // For system calls like fork(), execvp(), etc.
#include <sys/wait.h> // For waitpid() to wait for child processes
#include "shelpers.hpp" // For helper functions like tokenize() and getCommands()

int main() {
    std::string input; // Variable to store user input

    // Main loop: Keep the shell running until the user exits
    while (true) {
        // Display a prompt to the user
        std::cout << "myshell> ";

        // Read a line of input from the user
        if (!std::getline(std::cin, input)) {
            // If getline fails (e.g., user presses Ctrl+D), exit the loop
            break;
        }

        // If the input is empty, skip to the next iteration
        if (input.empty()) {
            continue;
        }

        // Step 1: Tokenize the input
        // Split the input string into individual tokens (words/symbols)
        std::vector<std::string> tokens = tokenize(input);

        // Step 2: Parse tokens into commands
        // Convert tokens into a list of Command structs (e.g., handle pipes, redirection)
        std::vector<Command> commands = getCommands(tokens);

        // If no valid commands were parsed, skip to the next iteration
        if (commands.empty()) {
            continue;
        }

        // Step 3: Handle the "exit" command
        // If the user typed "exit", break out of the loop to end the shell
        if (commands[0].execName == "exit") {
            break;
        }

        // Step 4: Fork a child process
        // Create a new process to execute the command
        pid_t pid = fork(); // fork() creates a copy of the current process
        if (pid < 0) {
            // If fork() fails, print an error and continue to the next iteration
            perror("fork");
            continue;
        }

        if (pid == 0) {
            // Child process: This code runs in the new process

            // Step 5: Prepare arguments for execvp
            // Convert the command's arguments into a format suitable for execvp
            std::vector<char*> argv; // Vector to hold C-style strings (char*)
            for (const char* arg : commands[0].argv) {
                // Add each argument to the argv vector
                argv.push_back(const_cast<char*>(arg));
            }
            // execvp requires the argument list to end with a nullptr
            argv.push_back(nullptr);

            // Step 6: Execute the command
            // Replace the current process with the new program specified by argv[0]
            execvp(argv[0], argv.data());

            // If execvp fails, it will return, and we print an error
            perror("execvp");
            exit(1); // Exit the child process with an error code
        } else {
            // Parent process: This code runs in the original process

            // Step 7: Wait for the child process to finish
            // waitpid() pauses the parent process until the child process completes
            int status;
            waitpid(pid, &status, 0);

            // Optionally, you can check the status to see how the child process exited
            // For example:
            // if (WIFEXITED(status)) {
            //     std::cout << "Child process exited with status: " << WEXITSTATUS(status) << std::endl;
            // }
        }
    }

    // End of the shell program
    return 0;
}