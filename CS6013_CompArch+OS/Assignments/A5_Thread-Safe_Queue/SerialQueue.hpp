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
#include <cstddef> // for nullptr

template <typename T>
class SerialQueue {

public:
   SerialQueue() :
      head_( new Node(T(), NULL) ), size_( 0 )
   {
      tail_ = head_;
   }

   void enqueue( const T & x ) {
      // Create a new node with the data
      Node* newNode = new Node(x, NULL);

      // Add it to the tail of the queue
      tail_->next = newNode;
      tail_ = newNode;

      // Increment size
      size_++;
   }

   bool dequeue( T * ret ) {
      // If queue is empty (only dummy head exists)
      if (head_->next == NULL) {
         return false;
      }

      // Get the first actual node (after dummy head)
      Node* temp = head_->next;

      // Return the data through ret pointer
      *ret = temp->data;

      // Remove the node from the queue
      head_->next = temp->next;

      // If this was the last node, update tail
      if (temp == tail_) {
         tail_ = head_;
      }

      // Delete the node and decrement size
      delete temp;
      size_--;

      return true;
   }

   ~SerialQueue() {
      while( head_ != NULL ) {
         Node* temp = head_->next;
         delete head_;
         head_ = temp;
      }
   }

   int size() const { return size_; }

private:
   struct Node {
      T data;
      Node* next;
      Node(const T& data, Node* next) : data(data), next(next) {}
   };

   Node * head_;
   Node * tail_;
   int    size_;
};