#include <iostream>
#include "Caps.h"
#include "Clothing.h"
#include "Pants.h"

using namespace std;
void virtualViaPointer(Clothing *C);

int main()
{
	//initializing 3 derived types
	Pants p1("P0001", 10.1, 12, 34, 2, 1.5);
	Caps c1("5000A", 15, 11, 40, 150, 'N');
	Clothing * cptr;

	//testing initialization
	cout << "Pants:" << endl;
	p1.print();
	p1.printbill();
	cout << endl;


	cout << "Cap: " << endl;
	c1.print();
	c1.printbill();
	cout << endl;

	//testing  += overload
	p1 += 99;
	c1 += 99;

	cout << "using virtualviapointer()" << endl;
	cout << endl;

	cout << "Pants:" << endl;
	virtualViaPointer(&p1);// passing a reference of type pants to a pointer of type clothing
	cptr = &p1;//aim the base pointer to pants
	cout << "using baseclass pointer" << endl;
	cptr->print();//print using the base pointer
	cout << endl;


	cout << "Cap: " << endl;
	virtualViaPointer(&c1);
	cptr = &c1;
	cout << "using baseclass pointer" << endl;
	cptr->print();
	cout << endl;

    //test
    return 0;
}

void virtualViaPointer(Clothing *C)
{
	C->print();
	cout << endl;
}

// \n flushes buffer, std::endl does not. each one byte of memory
// const char * argv[] = array of pointers to pointers of characters. char * argv[] can be replaced with string. char * *argv
// c++ test.cpp -o test
// creates test
// test.cpp is source file
// g++ test.cpp -o test
// clang++ test.cpp -o test
// previous two lines also work
// all compile in the terminal
// GNU - c++, g++ and LLVM - clang++
// USE c++ because it is most obvious
// c++ test.o -o test says give me output file called test
// from source code we go to object file. from object file to binary file or executable file
//
