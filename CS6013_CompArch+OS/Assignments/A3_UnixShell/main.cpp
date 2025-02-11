#include <iostream>
#include <vector>
#include <string>
#include <unistd.h>
#include <sys/wait.h>
#include "shelpers.hpp"

int main() {
    std::string input;
    while (true) {
        std::cout << "myshell> "; // Display a prompt
        if (!std::getline(std::cin, input)) { // Read input
            break; // Exit on EOF (Ctrl+D)
        }

        if (input.empty()) continue; // Skip empty input

        // Tokenize the input
        std::vector<std::string> tokens = tokenize(input);

        // Parse tokens into commands
        std::vector<Command> commands = getCommands(tokens);

        if (commands.empty()) continue; // Skip if no command

        // Handle the "exit" command
        if (commands[0].execName == "exit") {
            break;
        }

        // Fork a child process
        pid_t pid = fork();
        if (pid < 0) {
            perror("fork");
            continue;
        }

        if (pid == 0) { // Child process
            // Prepare arguments for execvp
            std::vector<char*> argv;
            for (const auto& arg : commands[0].argv) {
                argv.push_back(const_cast<char*>(arg));
            }
            argv.push_back(nullptr); // Null-terminate the array

            // Execute the command
            execvp(argv[0], argv.data());

            // If execvp fails
            perror("execvp");
            exit(1);
        } else { // Parent process
            // Wait for the child process to finish
            int status;
            waitpid(pid, &status, 0);
        }
    }
    return 0;
}