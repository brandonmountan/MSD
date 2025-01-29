#include <iostream>
#include <iomanip> // For formatting output

extern "C" {
    void sayHello();                  // Function to print "hello"
    int myPuts(const char* s, int len); // Function to write a string to stdout
    long myGTOD();                    // Function to get current time (seconds since epoch)
}

int main() {
    // Call sayHello
    sayHello();

    // Call myPuts with a custom message
    const char* message = "This is myPuts!";
    myPuts(message, 17); // 17 is the length of the string (excluding null terminator)

    // Call myGTOD to get the current time
    long secondsSinceEpoch = myGTOD();
    std::cout << "Seconds since epoch: " << secondsSinceEpoch << std::endl;

    return 0;
}