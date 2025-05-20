//////////////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
//
// Date: 04/17/2025
//
// Class: CS 6013 - Systems I
//
//////////////////////////////////////////////////////////////////////////////////

#include "shelpers.hpp"
#include <iostream>
#include <string>
#include <vector>
#include <unistd.h>
#include <sys/wait.h>
#include <cstring>
#include <stdlib.h>

using namespace std;

// Function to execute a built-in command
// Returns true if a built-in command was executed, false otherwise
bool executeBuiltIn(const Command& cmd) {
    if (cmd.execName == "cd") {
        // cd is a built-in command
        const char* dir = nullptr;

        if (cmd.argv.size() == 1 || (cmd.argv.size() == 2 && cmd.argv[1] == nullptr)) {
            // No arguments to cd, go to home directory
            dir = getenv("HOME");
            if (!dir) {
                cerr << "Error: HOME environment variable not set" << endl;
                return true;
            }
        } else {
            // Use the directory specified
            dir = cmd.argv[1];
        }

        if (chdir(dir) != 0) {
            perror("cd failed");
        }
        return true;
    } else if (cmd.execName == "exit") {
        // exit is a built-in command
        exit(0);
    }

    // Not a built-in command
    return false;
}

// Function to execute a command
void executeCommand(const vector<Command>& commands) {
    if (commands.empty()) {
        return; // Error occurred during parsing
    }

    // Check if the command is a built-in
    if (executeBuiltIn(commands[0])) {
        // Clean up any open file descriptors
        for (const Command& cmd : commands) {
            if (cmd.inputFd != STDIN_FILENO) {
                close(cmd.inputFd);
            }
            if (cmd.outputFd != STDOUT_FILENO) {
                close(cmd.outputFd);
            }
        }
        return;
    }

    // Track child processes for waiting
    vector<pid_t> childPids;

    // Launch all commands in the pipeline
    for (const Command& cmd : commands) {
        pid_t pid = fork();

        if (pid < 0) {
            // Fork failed
            perror("fork failed");
            break;
        } else if (pid == 0) {
            // Child process

            // Set up input redirection
            if (cmd.inputFd != STDIN_FILENO) {
                if (dup2(cmd.inputFd, STDIN_FILENO) < 0) {
                    perror("dup2 for stdin failed");
                    exit(1);
                }
                close(cmd.inputFd);
            }

            // Set up output redirection
            if (cmd.outputFd != STDOUT_FILENO) {
                if (dup2(cmd.outputFd, STDOUT_FILENO) < 0) {
                    perror("dup2 for stdout failed");
                    exit(1);
                }
                close(cmd.outputFd);
            }

            // Close any other file descriptors that might be open
            for (const Command& otherCmd : commands) {
                if (otherCmd.inputFd != STDIN_FILENO && otherCmd.inputFd != cmd.inputFd) {
                    close(otherCmd.inputFd);
                }
                if (otherCmd.outputFd != STDOUT_FILENO && otherCmd.outputFd != cmd.outputFd) {
                    close(otherCmd.outputFd);
                }
            }

            // Execute the command
            execvp(cmd.execName.c_str(), const_cast<char* const*>(cmd.argv.data()));

            // If execvp returns, it must have failed
            perror("execvp failed");
            exit(1);
        } else {
            // Parent process
            childPids.push_back(pid);
        }
    }

    // Close all file descriptors in the parent process
    for (const Command& cmd : commands) {
        if (cmd.inputFd != STDIN_FILENO) {
            close(cmd.inputFd);
        }
        if (cmd.outputFd != STDOUT_FILENO) {
            close(cmd.outputFd);
        }
    }

    // Wait for all child processes to finish (unless they're backgrounded)
    for (size_t i = 0; i < childPids.size(); i++) {
        // If it's the last command and it's backgrounded, don't wait
        if (i == childPids.size() - 1 && commands[i].background) {
            cout << "Running in background, PID: " << childPids[i] << endl;
            continue;
        }

        int status;
        if (waitpid(childPids[i], &status, 0) < 0) {
            perror("waitpid failed");
        }
    }
}

int main() {
    string line;

    // Display prompt and get input
    while (true) {
        cout << "myshell$ ";

        // Check for background processes that have completed
        int status;
        pid_t pid;
        while ((pid = waitpid(-1, &status, WNOHANG)) > 0) {
            cout << "Background process " << pid << " completed" << endl;
        }

        // Get user input
        if (!getline(cin, line)) {
            // EOF (Ctrl+D)
            cout << "exit" << endl;
            break;
        }

        if (line.empty()) {
            continue;
        }

        // Tokenize the input
        vector<string> tokens = tokenize(line);

        // Parse the tokens into commands
        vector<Command> commands = getCommands(tokens);

        // Execute the commands
        executeCommand(commands);

        // Clean up any dynamically allocated memory in argv
        for (const Command& cmd : commands) {
            for (const char* arg : cmd.argv) {
                if (arg != nullptr) {
                    free((void*)arg);
                }
            }
        }
    }

    return 0;
}