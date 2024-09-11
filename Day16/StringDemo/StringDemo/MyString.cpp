//
//  MyString.cpp
//  StringDemo
//
//  Created by Brandon Mountan on 9/11/24.
//

#include "MyString.hpp"
#include <cstring>

MyString::MyString(){
    data = new char[1];
    data[0] = '\0';
}

MyString::MyString(const char* str){
    int strSize = strlen(str) + 1;
    data = new char[strSize];
    for (int i = 0; i < strSize; i++){
        data[i] = str.data[i];
    }
}

MyString::MyString(const MyString& rhs){
    //fill in 'this' with a copy of this
    int rhsSize = rhs.size() + 1;
    data = new char[rhsSize];
    for (int i = 0; i < rhsSize; i++){
        data[i] = rhs.data[i];
    }
}

MyString& MyString::operator+=(const MyString& rhs){
    //rhs is it's own copy because it's passed by value
    char* tmp = data;
    data = rhs.data;
    rhs.data = tmp;
    
    return *this;
    //the destructor will delete[] my old data
}


MyString::~MyString(){
    delete [] data;
}

int MyString::size() const{
    
    
}


//char operator[](int index) const { return data[index];}
////size is implicit
//char& operator[](int index) { return data[index];}
////null-terminated string (0-terminated)
////size is implicit, ends where I find a '\0' == 0
//char* data;

