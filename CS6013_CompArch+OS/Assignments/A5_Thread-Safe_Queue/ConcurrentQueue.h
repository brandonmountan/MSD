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

#pragma once
#include <mutex>
#include <cstddef>  // For nullptr

template <typename T>
class ConcurrentQueue {

public:
    ConcurrentQueue() :
        head_(new Node(T(), nullptr)),
        size_(0)
    {
        tail_ = head_;
    }

    void enqueue(const T& x) {
        Node* newNode = new Node(x, nullptr);  // Fixed syntax

        {
            std::unique_lock<std::mutex> lock(mutex_);
            tail_->next = newNode;
            tail_ = newNode;
            size_++;
        }
    }

    bool dequeue(T* ret) {
        std::unique_lock<std::mutex> lock(mutex_);

        if (head_->next == nullptr) {
            return false;
        }

        Node* temp = head_->next;
        *ret = temp->data;
        head_->next = temp->next;

        if (temp == tail_) {
            tail_ = head_;
        }

        delete temp;
        size_--;
        return true;
    }

    ~ConcurrentQueue() {
        while (head_ != nullptr) {
            Node* temp = head_->next;
            delete head_;
            head_ = temp;
        }
    }

    int size() const {
        std::unique_lock<std::mutex> lock(mutex_);
        return size_;
    }

private:
    struct Node {
        T data;
        Node* next;
        Node(const T& d, Node* n) : data(d), next(n) {}  // Explicit constructor
    };

    Node* head_;
    Node* tail_;
    int size_;
    mutable std::mutex mutex_;
};