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

#include "ConcurrentQueue.h"
#include <iostream>
#include <vector>
#include <cstdlib>
#include <pthread.h>

// Define thread_args struct at global scope
struct thread_args {
    ConcurrentQueue<int>* queue;
    int num_ints;
};

void* producer_thread(void* arg) {
    thread_args* args = static_cast<thread_args*>(arg);
    for (int i = 0; i < args->num_ints; ++i) {
        args->queue->enqueue(i);
    }
    delete args;
    return NULL;
}

void* consumer_thread(void* arg) {
    thread_args* args = static_cast<thread_args*>(arg);
    int val;
    for (int i = 0; i < args->num_ints; ++i) {
        while (!args->queue->dequeue(&val)) {
            sched_yield();
        }
    }
    delete args;
    return NULL;
}

bool testQueue(int num_producers, int num_consumers, int num_ints) {
    std::vector<pthread_t> threads(num_producers + num_consumers);
    ConcurrentQueue<int> queue;

    // Create producer threads
    for (int i = 0; i < num_producers; ++i) {
        thread_args* args = new thread_args;
        args->queue = &queue;
        args->num_ints = num_ints;
        pthread_create(&threads[i], NULL, producer_thread, args);
    }

    // Create consumer threads
    for (int i = 0; i < num_consumers; ++i) {
        thread_args* args = new thread_args;
        args->queue = &queue;
        args->num_ints = num_ints;
        pthread_create(&threads[num_producers + i], NULL, consumer_thread, args);
    }

    // Wait for all threads to finish
    for (size_t i = 0; i < threads.size(); ++i) {
        pthread_join(threads[i], NULL);
    }

    int expected_size = (num_producers - num_consumers) * num_ints;
    return queue.size() == expected_size;
}

int main(int argc, char** argv) {
    if (argc != 4) {
        std::cerr << "Usage: " << argv[0]
                  << " <num_producers> <num_consumers> <num_ints>\n";
        return 1;
    }

    int num_producers = atoi(argv[1]);
    int num_consumers = atoi(argv[2]);
    int num_ints = atoi(argv[3]);

    bool result = testQueue(num_producers, num_consumers, num_ints);

    if (result) {
        std::cout << "Test passed! Queue size is correct.\n";
        return 0;
    } else {
        std::cout << "Test failed! Queue size is incorrect.\n";
        return 1;
    }
}