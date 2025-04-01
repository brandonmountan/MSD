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


#include "SerialQueue.hpp"
#include <iostream>
#include <cassert>

void basicTests() {
    SerialQueue<int> queue;

    // Test initial state
    assert(queue.size() == 0);

    // Test enqueue and dequeue single element
    queue.enqueue(42);
    assert(queue.size() == 1);

    int val;
    bool success = queue.dequeue(&val);
    assert(success);
    assert(val == 42);
    assert(queue.size() == 0);

    // Test empty dequeue
    success = queue.dequeue(&val);
    assert(!success);

    // Test multiple elements
    for (int i = 0; i < 100; ++i) {
        queue.enqueue(i);
    }
    assert(queue.size() == 100);

    for (int i = 0; i < 100; ++i) {
        success = queue.dequeue(&val);
        assert(success);
        assert(val == i);
    }
    assert(queue.size() == 0);

    std::cout << "Basic tests passed!" << std::endl;
}

void randomTests(int iterations) {
    SerialQueue<int> queue;

    for (int i = 0; i < iterations; ++i) {
        int action = rand() % 2;
        int val = rand() % 1000;

        if (action == 0 || queue.size() == 0) {
            // Enqueue
            queue.enqueue(val);
        } else {
            // Dequeue
            int ret;
            bool success = queue.dequeue(&ret);
            assert(success);
        }
    }

    std::cout << "Random tests completed with " << iterations << " operations." << std::endl;
}

int main() {
    basicTests();
    randomTests(10000);
    return 0;
}