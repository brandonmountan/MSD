For this assignment, you will be writing a thread-safe queue in C++. This will be a straightforward C++ implementation of concepts discussed in class.

Part 1: A Serial Queue (10 pts)
This is a warmup based on your data structures class. You will write a non-concurrent (serial) queue in C++. The queue will be implemented as a singly-linked list that maintains both head and tail pointers. Fill in the function definitions in the file SerialQueue.hpp Download SerialQueue.hpp. The constructor and destructor have been filled in for you. Some points:

1. enqueue( const T & x ) adds a new node at the tail of the linked list.

2. dequeue( T * ret ) removes a node from the head of the linked list, and returns the data at the new head in the variable ret. If the queue is empty, dequeue returns false. If an element was dequeued successfully, dequeue returns true.

3. Draw a diagram of what the Queue looks like after it has been constructed.  Draw another picture of what it will look like after a node is added.

When you're done filling in the enqueue and dequeue, write a SerialQueueTests.cpp (which should define main()), include SerialQueue.hpp, and test your code.  Be sure to test creation of your Queue, as well as enqueuing and dequeuing.  Basic tests would include manually adding one or two values to a queue and immediately removing them.  Dynamic testing should include creating a function that takes a Queue and a number of iterations, and then enqueues (and/or dequeues) that many "random" elements.  Part of this test would include enqueuing 100 (as specified) integers, and the verifying that when you dequeue them, you get the correct values back (if you enqueued 1 first, the first dequeue should return 1.)

Constraints:

Remember, this is C++ code. Do not use malloc and free. You are allowed to use new and delete. You are not allowed to use the STL's built-in queue or any queue libraries!



Part 2: Thread-Safe Locking Queue (15 pts)
Now, you will make the same queue thread-safe. Copy the contents of your SerialQueue.hpp into a new file called ConcurrentQueue.hpp.  Rename the class SerialQueue to ConcurrentQueue. Then, modify the queue to be thread-safe using concepts from our class.  The constraints from Part 1 still apply. Some points:

1. You will need to modify enqueue and dequeue to use locks. For this, you will have to identify potential race conditions, and lock and unlock a mutex around them. The lecture on locked data structures should help.

2. You can assume that new and delete are thread-safe.

3. The C++11 threading library has several options for mutexes and locks. I recommend using std::mutex in conjunction with std::unique_lock, but you're free to just use std::mutex also.

4. std::unique_lock is a scoped lock. It will automatically unlock when it goes out of scope. Remember, local variables declared in a function go out of scope when the function returns. You can also arbitrarily define scopes using curly braces anywhere in your code like so:

// Some code here that does some stuff

{ // Begins a new (sub) scope

std::unique_lock<std::mutex> my_lock( mutex );

// Do other stuff here that is now locked - ie, the critical section

} // my_lock goes out of scope and automatically unlocks.


Part 3: Testing ConcurrentQueue (5 pts)
It's always hard to test multithreaded code. We won't be doing exhaustive tests. The only thing we'll be doing is concurrently enqueue as many ints from as many threads as the user wants. We'll also concurrently dequeue as many ints from as many threads as the user wants.

Read all 3 of the following points before you start:

1. Have your program take in from the command line (using argv) the numbers of producers (threads that enqueue ints), the number of consumers (threads that dequeue ints), and the number of ints that producers and consumers enqueue and dequeue. An example of command line execution of your program would be:

./ConcurrentQueueTest 4 3 10

This will create 4 producer threads and 3 consumer threads. Each thread will enqueue 10 integers and dequeue 10 integers respectively.

2. To do the above, create a new file called ConcurrentQueueTest.cpp, and (in addition to main), write a function called testQueue() with the following signature:

bool testQueue( int num_producers, int num_consumers, int num_ints ) { ... }
You must fill in the body of this function by doing:

a. Create an std::vector of std::threads.

b. reserve space in this vector for all producer and consumer threads

c. Create a ConcurrentQueue object statically.

d. Create num_producer producer threads that enqueue num_ints ints into the ConcurrentQueue.

e. Create num_consumer consumer threads that dequeue num_ints ints from the ConcurrentQueue.

f. Wait for all threads to join (ie, finish).

g. Return true if the number of elements in the queue matches (num_producers - num_consumers)*num_ints, false otherwise. This basically tests if we did the right number of inserts and deletes into the queue.



3. In int main( int argc, char **argv ), use argv to read in num_producers, num_consumers, and num_ints from the command line. Call testQueue with these parameters and check the return value to make sure your code is working correctly. If your code hangs or freezes, you're probably not locking correctly.

Upload your SerialQueue.hpp, SerialQueueTests.hpp, ConcurrentQueue.hpp, ConcurrentQueueTests.cpp, and diagram to Canvas.