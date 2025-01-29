#include <iostream>
#include <cassert>
using namespace std;

class node {
public:
    int data;
    node *prev;
    node *next;
};

class Doubly_Linked_List {
    node *front;
    node *end;
    public:
    Doubly_Linked_List()
    {
        front = NULL;
        end = NULL;
    }
    
    void add_front(int );
    void add_end(int );
    void forward_traverse();
    void check_rep();
 
};

void Doubly_Linked_List :: add_front(int d) {
    node *temp;
    temp = new node();
    temp->data = d;
    temp->prev = NULL;
    temp->next = front;

    if(front == NULL)
        end = temp;
    else
        front->prev = temp;
        
    front = temp;
    
    // Missing
    assert(front->next==nullptr || front->next->prev==front);
}

void Doubly_Linked_List :: add_end(int d) {
    node *temp;
    temp = new node();
    temp->data = d;
    temp->prev = end;
    temp->next = NULL;

    if(end == NULL)
        front = temp;
    else
        end->next = temp;
    end = temp;
    
    // Missing assert
    assert(end != NULL);

}


void Doubly_Linked_List :: forward_traverse() {
    node *trav;
    trav = front;
    while(trav != NULL) {
        cout<<trav->data<<endl;
        trav = trav->next;
    }
}

void Doubly_Linked_List :: check_rep() {
    node *n;
    n = front;
    while(n != NULL) {
        assert(n->next == nullptr || n->next->prev == n);
        n = n->next;
    }
    cout << "Representation maintained!" << endl;
}



int main() {
    Doubly_Linked_List *LL = new Doubly_Linked_List();
    LL->add_front(100);
    LL->add_front(200);
    
    LL->add_end(300);
    LL->add_end(400);
    
    LL->check_rep();
    
    LL->forward_traverse();
    
}
