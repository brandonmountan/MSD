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
    int strSize = (int)strlen(str) + 1;
    data = new char[strSize];
    for (int i = 0; i < strSize; i++){
        data[i] = str[i];
    }
}

//copy constructor
MyString::MyString(const MyString& rhs){
    //fill in 'this' with a copy of rhs
    int rhsSize = rhs.size() + 1;
    data = new char[rhsSize];
    for (int i = 0; i < rhsSize; i++){
        data[i] = rhs.data[i];
    }
}

MyString& MyString::operator=(MyString rhs){
    //rhs is it's own copy because it's passed by value
    char* tmp = data;
    data = rhs.data;
    rhs.data = tmp;
    
    return *this;
    //the destructor will delete[] my old data
}

//destructor
//MyString::~MyString(){
//    delete [] data;
//}

MyString& MyString::operator+=(const MyString& rhs){
    //make a new array long enough for this + rhs
    int lSize = size();
    int rSize = rhs.size();
    char* newData = new char[lSize + rSize + 1];
    //copy lhs and rhs values in
    for (int i = 0; i < lSize; i++){
        newData[i] = data[i];
    }
    for (int i = 0; i < rSize; i++){
        newData[lSize + i] = rhs.data[i];
    }
    //add a '\0' byte
    newData[lSize+rSize] = '\0';
    //delete old array
    delete [] data;
    newData = data;
    
    return *this;
};

int MyString::size() const{
    return (int)strlen(data);
}

MyString operator+(const MyString& lhs, const MyString& rhs){
    MyString ret = lhs;
    ret += rhs;
    return ret;
};
