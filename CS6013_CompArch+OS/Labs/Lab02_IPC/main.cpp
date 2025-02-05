#include <iostream>
#include <string>
#include <unistd.h>
#include <cstring>
#include <signal.h>
#include <sys/wait.h>

#define MAX_MSG_SIZE 1024 ///< Maximum size of a message.

int fds[2]; ///< Pipe file descriptors for communication between parent and child.
pid_t child_pid; ///< Process ID of the child process.

/**
 * @brief Signal handler for the parent process.
 *
 * This function handles the SIGUSR1 signal sent by the child process to acknowledge receipt of a message.
 *
 * @param sig The signal number (should be SIGUSR1).
 */
void parent_signal_handler(int sig) {
    if (sig == SIGUSR1) {
        std::cout << "Parent: Received acknowledgment from child.\n";
    }
}

/**
 * @brief Signal handler for the child process.
 *
 * This function handles the SIGUSR1 signal sent by the parent process to indicate a new message.
 *
 * @param sig The signal number (should be SIGUSR1).
 */
void child_signal_handler(int sig) {
    if (sig == SIGUSR1) {
        std::cout << "Child: Received message from parent.\n";
    }
}

/**
 * @brief Function executed by the child process.
 *
 * The child process reads messages from the pipe, prints them, and sends an acknowledgment to the parent.
 * It terminates when it receives the "quit" message.
 */
void child_process() {
    // Set up signal handler for the child
    signal(SIGUSR1, child_signal_handler);

    close(fds[1]); // Close the write end of the pipe

    char msg[MAX_MSG_SIZE];
    ssize_t bytes_read;

    while (true) {
        // Read the length of the message first
        size_t msg_len;
        bytes_read = read(fds[0], &msg_len, sizeof(msg_len));
        if (bytes_read <= 0) {
            perror("Child: Error reading message length");
            break;
        }

        // Read the actual message
        bytes_read = read(fds[0], msg, msg_len);
        if (bytes_read <= 0) {
            perror("Child: Error reading message");
            break;
        }

        msg[bytes_read] = '\0'; // Null-terminate the message
        std::cout << "Child: Received message: " << msg << "\n";

        // Send acknowledgment to the parent
        kill(getppid(), SIGUSR1);

        // Check if the message is "quit"
        if (std::string(msg) == "quit") {
            std::cout << "Child: Exiting...\n";
            break;
        }
    }

    close(fds[0]); // Close the read end of the pipe
    exit(0);
}

/**
 * @brief Function executed by the parent process.
 *
 * The parent process sends messages to the child through the pipe and waits for acknowledgment.
 * It reads user input and sends it to the child until the user enters "quit".
 *
 * @param argc The number of command-line arguments.
 * @param argv The command-line arguments. The first argument is the initial message to send.
 */
void parent_process(int argc, char *argv[]) {
    // Set up signal handler for the parent
    signal(SIGUSR1, parent_signal_handler);

    close(fds[0]); // Close the read end of the pipe

    std::string msg;
    size_t msg_len;

    // Send the initial message from argv[1]
    if (argc > 1) {
        msg = argv[1];
        msg_len = msg.size() + 1; // Include null terminator
        std::cout << "Parent: Sending initial message: " << msg << "\n";
        std::cout << "Parent: Message length: " << msg_len << "\n"; // Debugging
        if (write(fds[1], &msg_len, sizeof(msg_len)) == -1) {
            perror("Parent: Error writing message length");
            exit(1);
        }
        if (write(fds[1], msg.c_str(), msg_len) == -1) {
            perror("Parent: Error writing message");
            exit(1);
        }
        std::cout << "Parent: Sent initial message: " << msg << "\n";

        // Wait for acknowledgment from the child
        pause();
    }

    // Continuously read messages from the user and send them to the child
    while (true) {
        std::cout << "Parent: Enter a message (or 'quit' to exit): ";
        if (!std::getline(std::cin, msg)) {
            perror("Parent: Error reading input");
            break;
        }

        // Send the message length first
        msg_len = msg.size() + 1; // Include null terminator
        if (write(fds[1], &msg_len, sizeof(msg_len)) == -1) {
            perror("Parent: Error writing message length");
            break;
        }

        // Send the actual message
        if (write(fds[1], msg.c_str(), msg_len) == -1) {
            perror("Parent: Error writing message");
            break;
        }
        std::cout << "Parent: Sent message: " << msg << "\n";

        // Wait for acknowledgment from the child
        pause();

        // Check if the message is "quit"
        if (msg == "quit") {
            std::cout << "Parent: Exiting...\n";
            break;
        }
    }

    close(fds[1]); // Close the write end of the pipe

    // Wait for the child process to exit
    waitpid(child_pid, nullptr, 0);
    exit(0);
}

/**
 * @brief Main function.
 *
 * This function creates a pipe, forks a child process, and delegates execution to the parent or child process.
 *
 * @param argc The number of command-line arguments.
 * @param argv The command-line arguments.
 * @return 0 on success, 1 on failure.
 */
int main(int argc, char *argv[]) {
    // Create the pipe
    if (pipe(fds) == -1) {
        perror("Error creating pipe");
        return 1;
    }

    // Fork the process
    child_pid = fork();
    if (child_pid == -1) {
        perror("Error forking process");
        return 1;
    }

    if (child_pid == 0) {
        // Child process
        child_process();
    } else {
        // Parent process
        parent_process(argc, argv);
    }

    return 0;
}