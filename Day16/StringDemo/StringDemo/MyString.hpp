//
//  MyString.hpp
//  StringDemo
//
//  Created by Brandon Mountan on 9/11/24.
//

#ifndef MyString_hpp
#define MyString_hpp

#include <stdio.h>

class MyString{
    
public:
    //constructors
    MyString(); //empty string (data --> '\0' on the heap)
    MyString(const char* str);
    
    //rule of 3
    MyString(const MyString& rhs);
    MyString& operator=(MyString rhs);
    ~MyString();
    
    //+, +=
    MyString& operator+=(const MyString& rhs);
    //[]
    //read only version
    char operator[](int index) const { return data[index];}
    //size is implicit
    // for str[i] = 'c'
    char& operator[](int index) { return data[index];}
    
    int size() const;
private:
    //null-terminated string (0-terminated)
    //size is implicit, ends where I find a '\0' == 0
    char* data;
};

MyString operator+(const MyString& lhs, const MyString& rhs);


#endif /* MyString_hpp */

