////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
// Date: 04/04/2025
//
// CS 6013
//
// A thread-safe FIFO queue implementation using a singly-linked list
// with head/tail pointers and mutex locks for synchronization.
//
////////////////////////////////////////////////////////////////////////

#pragma once
#include <cstddef>  // for nullptr
#include <mutex>    // for std::mutex and std::unique_lock

template <typename T>
class ConcurrentQueue {
public:
    // Constructor: Initializes an empty queue with a dummy head node.
    // The dummy node simplifies edge cases (empty queue checks).
    ConcurrentQueue() :
        head_(new Node(T(), nullptr)), // Dummy head node
        size_(0)                       // Initial size
    {
        tail_ = head_; // Tail initially points to dummy head
    }

    // Thread-safe enqueue operation.
    // @param x: The element to add (passed by const reference).
    void enqueue(const T& x) {
        // 1. Create the new node OUTSIDE the lock for efficiency.
        //    (Memory allocation can be slow; don't block other threads.)
        Node* newNode = new Node(x, nullptr);

        // 2. Lock the mutex to enter the critical section.
        //    std::unique_lock automatically unlocks when scope ends.
        std::unique_lock<std::mutex> lock(mutex_);

        // 3. Link the new node to the tail.
        tail_->next = newNode;
        tail_ = newNode; // Update tail pointer
        size_++;         // Increment size
    } // Mutex unlocked here (RAII)

    // Thread-safe dequeue operation.
    // @param ret: Pointer to store the dequeued value.
    // @return: True if successful, false if queue was empty.
    bool dequeue(T* ret) {
        // 1. Lock the mutex for the entire operation.
        std::unique_lock<std::mutex> lock(mutex_);

        // 2. Check if queue is empty (only dummy head exists).
        if (head_->next == nullptr) {
            return false;
        }

        // 3. Get the first actual node (after dummy head).
        Node* temp = head_->next;

        // 4. Copy data to the return pointer.
        *ret = temp->data;

        // 5. Update head's next pointer to skip the dequeued node.
        head_->next = temp->next;

        // 6. Special case: If dequeuing the last node, reset tail to dummy head.
        if (temp == tail_) {
            tail_ = head_;
        }

        // 7. Delete the node and decrement size.
        delete temp;
        size_--;

        return true;
    } // Mutex unlocked here

    // Destructor: Cleans up all nodes (including dummy head).
    ~ConcurrentQueue() {
        while (head_ != nullptr) {
            Node* temp = head_->next;
            delete head_;
            head_ = temp;
        }
    }

    // Thread-safe size query.
    // @return: Current number of elements in the queue.
    // Note: 'mutable' allows locking in const methods.
    int size() const {
        std::unique_lock<std::mutex> lock(mutex_);
        return size_;
    }

private:
    // Internal Node structure for the linked list.
    struct Node {
        T data;       // Stores the element
        Node* next;   // Pointer to the next node

        Node(const T& data, Node* next) : data(data), next(next) {}
    };

    Node* head_;               // Dummy head node (always exists)
    Node* tail_;               // Tail pointer (last node)
    int size_;                 // Number of elements (excludes dummy head)
    mutable std::mutex mutex_; // Mutex for thread safety ('mutable' for const methods)
};