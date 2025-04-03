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
#include <vector>
#include <thread>
#include "ConcurrentQueue.h"

bool testQueue(int num_producers, int num_consumers, int num_ints) {
    std::vector<std::thread> threads;
    threads.reserve(num_producers + num_consumers); // Reserve space for all threads
    ConcurrentQueue<int> queue; // Statically created queue

    // Producer threads: Each enqueues `num_ints` integers
    for (int i = 0; i < num_producers; ++i) {
        threads.emplace_back([&queue, num_ints]() {
            for (int j = 0; j < num_ints; ++j) {
                queue.enqueue(j); // Enqueue values (could be any int, using j for simplicity)
            }
        });
    }

    // Consumer threads: Each dequeues `num_ints` integers
    for (int i = 0; i < num_consumers; ++i) {
        threads.emplace_back([&queue, num_ints]() {
            int val;
            for (int j = 0; j < num_ints; ++j) {
                while (!queue.dequeue(&val)) {
                    // If queue is empty, yield and retry (busy-wait)
                    std::this_thread::yield();
                }
            }
        });
    }

    // Wait for all threads to finish
    for (auto& t : threads) {
        t.join();
    }

    // Check final queue size
    int expected_size = (num_producers - num_consumers) * num_ints;
    return (queue.size() == expected_size);
}

int main(int argc, char** argv) {
    if (argc != 4) {
        std::cerr << "Usage: " << argv[0] << " <num_producers> <num_consumers> <num_ints>\n";
        return 1;
    }

    int num_producers = std::stoi(argv[1]);
    int num_consumers = std::stoi(argv[2]);
    int num_ints = std::stoi(argv[3]);

    bool success = testQueue(num_producers, num_consumers, num_ints);
    if (success) {
        std::cout << "Test passed! Queue size matches expected.\n";
    } else {
        std::cout << "Test failed! Queue size is incorrect.\n";
    }

    return 0;
}