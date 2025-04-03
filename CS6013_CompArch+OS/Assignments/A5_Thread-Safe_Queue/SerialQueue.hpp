////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
// Date: 04/04/2025
//
// CS 6013
//
// A thread-unsafe (serial) queue implementation using a singly-linked list
// with head and tail pointers. This is a FIFO (First-In-First-Out) data structure.
//
////////////////////////////////////////////////////////////////////////

#pragma once
#include <cstddef> // for nullptr

template <typename T>
class SerialQueue {

public:
    // Constructor: Initializes an empty queue with a dummy head node.
    // The dummy node simplifies edge cases (empty queue checks).
    SerialQueue() :
        head_(new Node(T(), NULL)), // Create dummy head with default T value
        size_(0)                    // Initialize size to 0
    {
        tail_ = head_; // Tail points to dummy head initially
    }

    // Enqueues (adds) an element to the tail of the queue.
    // @param x: The element to be added (passed by const reference).
    void enqueue(const T& x) {
        // 1. Create a new node with the given data and NULL next pointer
        Node* newNode = new Node(x, NULL);

        // 2. Link the new node to the current tail's next pointer
        tail_->next = newNode;

        // 3. Update tail to point to the new node
        tail_ = newNode;

        // 4. Increment the queue size
        size_++;
    }

    // Dequeues (removes) an element from the head of the queue.
    // @param ret: Pointer to store the dequeued value.
    // @return: True if successful, false if queue is empty.
    bool dequeue(T* ret) {
        // 1. Check if queue is empty (only dummy head exists)
        if (head_->next == NULL) {
            return false; // Nothing to dequeue
        }

        // 2. Get the first actual node (after dummy head)
        Node* temp = head_->next;

        // 3. Copy the data to the return pointer
        *ret = temp->data;

        // 4. Update head's next pointer to skip the dequeued node
        head_->next = temp->next;

        // 5. Special case: If dequeuing the last node, reset tail to dummy head
        if (temp == tail_) {
            tail_ = head_;
        }

        // 6. Delete the node and decrement size
        delete temp;
        size_--;

        return true; // Success
    }

    // Destructor: Cleans up all nodes (including dummy head) to prevent memory leaks.
    ~SerialQueue() {
        while (head_ != NULL) {
            Node* temp = head_->next; // Save next node
            delete head_;             // Delete current node
            head_ = temp;             // Move to next node
        }
    }

    // Returns the current number of elements in the queue.
    // @return: Size of the queue.
    int size() const { return size_; }

private:
    // Internal Node structure for linked list implementation.
    struct Node {
        T data;       // Stores the element
        Node* next;   // Pointer to the next node

        // Node constructor
        Node(const T& data, Node* next) : data(data), next(next) {}
    };

    Node* head_; // Pointer to dummy head node (always exists)
    Node* tail_; // Pointer to the last node in the queue
    int size_;   // Tracks the number of elements (excluding dummy head)
};