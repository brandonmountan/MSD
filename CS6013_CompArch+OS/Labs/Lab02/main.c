/**
 * @file pipe_communication.c
 * @brief A program demonstrating inter-process communication using pipes and signals.
 *
 * This program forks a child process and communicates with it using a pipe.
 * The parent sends messages to the child, and the child acknowledges receipt of each message.
 * The program terminates when the user enters "quit".
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <sys/wait.h>

#define MAX_MSG_SIZE 1024 ///< Maximum size of a message.

int pipefd[2]; ///< Pipe file descriptors for communication between parent and child.
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
        printf("Parent: Received acknowledgment from child.\n");
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
        printf("Child: Received message from parent.\n");
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

    close(pipefd[1]); // Close the write end of the pipe

    char msg[MAX_MSG_SIZE];
    ssize_t bytes_read;

    while (1) {
        // Read the length of the message first
        size_t msg_len;
        bytes_read = read(pipefd[0], &msg_len, sizeof(msg_len));
        if (bytes_read <= 0) {
            perror("Child: Error reading message length");
            break;
        }

        // Read the actual message
        bytes_read = read(pipefd[0], msg, msg_len);
        if (bytes_read <= 0) {
            perror("Child: Error reading message");
            break;
        }

        msg[bytes_read] = '\0'; // Null-terminate the message
        printf("Child: Received message: %s\n", msg);

        // Send acknowledgment to the parent
        kill(getppid(), SIGUSR1);

        // Check if the message is "quit"
        if (strcmp(msg, "quit") == 0) {
            printf("Child: Exiting...\n");
            break;
        }
    }

    close(pipefd[0]); // Close the read end of the pipe
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

    close(pipefd[0]); // Close the read end of the pipe

    char msg[MAX_MSG_SIZE];
    size_t msg_len;

    // Send the initial message from argv[1]
    if (argc > 1) {
        msg_len = strlen(argv[1]) + 1;
        printf("Parent: Sending initial message: %s\n", argv[1]); // Debugging statement
        if (write(pipefd[1], &msg_len, sizeof(msg_len)) == -1) {
            perror("Parent: Error writing message length");
            exit(1);
        }
        if (write(pipefd[1], argv[1], msg_len) == -1) {
            perror("Parent: Error writing message");
            exit(1);
        }
        printf("Parent: Sent initial message: %s\n", argv[1]);

        // Wait for acknowledgment from the child
        pause();
    }

    // Continuously read messages from the user and send them to the child
    while (1) {
        printf("Parent: Enter a message (or 'quit' to exit): ");
        if (fgets(msg, MAX_MSG_SIZE, stdin) == NULL) {
            perror("Parent: Error reading input");
            break;
        }

        // Remove newline character from the input
        msg[strcspn(msg, "\n")] = '\0';

        // Send the message length first
        msg_len = strlen(msg) + 1;
        if (write(pipefd[1], &msg_len, sizeof(msg_len)) == -1) {
            perror("Parent: Error writing message length");
            break;
        }

        // Send the actual message
        if (write(pipefd[1], msg, msg_len) == -1) {
            perror("Parent: Error writing message");
            break;
        }
        printf("Parent: Sent message: %s\n", msg);

        // Wait for acknowledgment from the child
        pause();

        // Check if the message is "quit"
        if (strcmp(msg, "quit") == 0) {
            printf("Parent: Exiting...\n");
            break;
        }
    }

    close(pipefd[1]); // Close the write end of the pipe

    // Wait for the child process to exit
    waitpid(child_pid, NULL, 0);
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
    if (pipe(pipefd) == -1) {
        perror("Error creating pipe");
        exit(1);
    }

    // Fork the process
    child_pid = fork();
    if (child_pid == -1) {
        perror("Error forking process");
        exit(1);
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