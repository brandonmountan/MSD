In this assignment, we'll write our own shell (like bash, zsh, etc).

A shell is a program that starts other programs (which we've learned how to do using the fork()/exec() pattern). Writing a very simple shell (that doesn't have pipes or I/O redirection) is actually quite straightforward. Adding redirection features requires a bit of wrangling (mostly to parse user input!) and manipulating file descriptors (creating them with open(), and pipe(), reassigning them with dup2()).

Write your shell in C++. The POSIX system calls we'll need to use are C functions which can be called transparently from C++ code. Use the provided shelpers as a starting point: shelpers.hpp and shelpers.cpp Download shelpers.cpp.

Build your shell up in the following pieces.

Part 1: Running a single command at a time

Start with a shell that interprets a line of input to be a single user command. A real shell would consider "words in quotations" as a single argument, but we'll ignore that. Use the getCommands method, and assuming you don't use any of the special redirection/backgrounding characters (&, |, <, >), you should get back a vector containing 1 command.

In your main function, use fork(), exec(), and one of the wait() functions to run the command, wait for it to run, then get the next command from stdin.

Notes:

Look at the family of exec() syscalls. I found the execvp one to be best suited for this project
To get a char* from a std::string use its c_str() method
To get a T* from a std::vector<T>, use its data() method (a pointer to the first element, all elements will be contiguous).
The argv parameter to execvp expects the last pointer in the list to be null.
I was unable to call exec without using const_cast. This works like static_cast, but it adds/removes const. This is generally a bad thing to do, however, since exec will overwrite the whole program memory, we really don't care about the const-ness of our argv array.
argv[0] should be the name of the program being run
The provided code should be helpful for doing some of the necessary text processing, and should fill in the command struct correctly without any I/O redirection
Some example of commands that you should be able to perform:

ls
cat someFile
echo hello world
If you wrap this code in a while(getline(...)) loop, it will continue executing commands until you type control-d (which is like the terminal equivalent of "end of file"). You might also want to check to see if the command requested was "exit" and exit your program.


Part 2: Including I/O redirection

Next, we'll implement a (simplified) version of I/O redirection -- meaning programs will use a file for stdin or stdout (without their knowledge!). Without doing some tricky checks, a program literally cannot tell what the file descriptors for stdin (usually the console keyboard), and stdout (usually the console window) are connected to!

We'll need to add a couple of things to make this work:

You'll need to fill in the appropriate parts of the getCommands functions (read the comments)

Next, you'll need to change things when you fork/exec. The file stdin and stdout file descriptors for the child process will need to be modified AFTER you fork, but BEFORE You exec! You'll want to use the dup2() system call to implement this, which basically replaces one file descriptor with another (you'll overwrite fd 0 with a descriptor hooked up to a file you're reading from, for example).

At this point you should be able to run commands like:

echo hello world > someFile
head < someFile

Part 3: Adding pipes

Chaining multiple commands with pipes is super useful: cat myFile | head -n 100 | tail -n10 #gives me lines 90-100 of myFile

The principle is the same as for part 2, only instead of replacing stdin/stdout with file descriptors of open files, we'll replace them with open pipes.

This will also require some additions to the getCommands function.

Note: only the first command in a pipeline can have input file redirection (all the others get it from a pipe), and only the last can have output file redirection (all the rest write to a pipe).

Note: You'll also need to close any open file descriptors that your child process isn't using! If you don't, your shell might hang because some programs won't exit until stdin is closed!

You should be able to run commands like:

cat myFile | head
cat myFile | grep something | tail #(get the last 10 occurrences of something in myFile)

Part 4: shell builtins

Some things you do in a shell are actually part of the shell because they CANNOT exist as separate programs. We'll stick with the simplest, and probably most important:

cd: cd with no parameters should go to the user's home directory (the $HOME environment variable). otherwise, it should take 1 parameter (the directory to move to). Note this "built-in" should mostly consist of 1 system call (chdir())

Think about why cd must be implemented as a built-in. (Try writing it as a separate program and look at what happens, if you're unsure).


Part 5: bells and whistles

Pick and implement at least one of the following (or suggest other ideas to me)

Implement background commands. If a command runs in the background, the shell should not wait for it to complete before letting the user enter another command. After the user types a new command and presses enter, the shell should check to see if any backgrounded commands have completed and an informative message saying so. The user indicates that a command should be backgrounded by adding a & after the command: ./startMyWebServer& #start the webserver, but give me my shell back!
Add support for environment variables. Add shell builtins for setting and using environment variables (the usual syntax would be something like MYVAR=something and echo $MYVAR). Look at the getenv and setenv family of functions to help with this.
Add tab completion. You'll probably want to use the readline library. Here's a tutorial: https://robots.thoughtbot.com/tab-completion-in-gnu-readlineLinks to an external site.. I'd suggest auto-completing commands (the first word the user is typing) or filenames (for any other argument).

General tips

Start early! There's a lot here!

Test in small pieces (always)!
There's lots of tricky bits to futz with that your classmates have probably dealt with, or will have to deal with, so talk to each other + TA!
man pages are a good resource (if boring and sometimes hard to read).
EVERY SYSTEM CALL RETURN VALUE SHOULD BE CHECKED! For this assignment, if something goes wrong: log it with perror() and clean up (close open file descriptors, etc).