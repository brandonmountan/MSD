////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
// Date: 04/04/2025
//
// CS 6013
//
// Outline for SerialQueue class.  Fill in the missing data, comments, etc.
//
////////////////////////////////////////////////////////////////////////


#include <iostream>
#include <cassert>
#include "SerialQueue.hpp"

// Basic manual tests
void testBasicOperations() {
    SerialQueue<int> queue;

    // Test 1: Enqueue and dequeue single element
    queue.enqueue(42);
    int val;
    assert(queue.dequeue(&val) == true);
    assert(val == 42);
    assert(queue.size() == 0);

    // Test 2: Dequeue from empty queue
    assert(queue.dequeue(&val) == false);

    // Test 3: Enqueue multiple elements and dequeue in order (FIFO)
    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);
    assert(queue.size() == 3);

    assert(queue.dequeue(&val) == true && val == 1);
    assert(queue.dequeue(&val) == true && val == 2);
    assert(queue.dequeue(&val) == true && val == 3);
    assert(queue.size() == 0);

    std::cout << "Basic tests passed!\n";
}

// Dynamic test: Enqueue N random elements and verify FIFO order
void testDynamicOperations(int num_elements) {
    SerialQueue<int> queue;

    // Enqueue elements
    for (int i = 0; i < num_elements; ++i) {
        queue.enqueue(i);
    }
    assert(queue.size() == num_elements);

    // Dequeue and verify order
    int val;
    for (int i = 0; i < num_elements; ++i) {
        assert(queue.dequeue(&val) == true);
        assert(val == i); // FIFO order must match insertion order
    }
    assert(queue.size() == 0);

    std::cout << "Dynamic test with " << num_elements << " elements passed!\n";
}

int main() {
    // Run basic tests
    testBasicOperations();

    // Run dynamic test with 100 elements
    testDynamicOperations(100);

    return 0;
}