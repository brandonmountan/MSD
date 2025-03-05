Overview

In this assignment, you'll practice using system calls for allocating and deallocating virtual memory from the OS to implement the library functions malloc() and free(). In other words, you are writing replacements for  malloc() and free().

Your allocator will be fairly "dumb", will probably be about 10x slower than the standard library malloc, and will waste lots of memory, but it should work as a drop-in replacement!

When a user calls  malloc(),  allocate memory using the map() system call, which deals in units of pages. You'll forward calls to free() to munmap().  Unfortunately, free() doesn't take the allocation size as a parameter, so you'll have to store the original allocation sizes in a hash table that fits into some memory that we'll map().

Approach

For this assignment you'll write a Class that has 2 public methods...

void* allocate(   size_t bytesToAllocate )
void  deallocate( void*  ptr )
...that are near drop-in replacements for  malloc() and free().

In addition, my implementation contained the following additional methods:

Constructor/Destructor
hash table insert, delete, grow
The allocate function should essentially:

round up the allocation size to the next multiple of the page size
allocate memory with mmap()
insert the returned pointer and the allocation size in the hash table
return the pointer to the new memory to the caller
The deallocate function should do the inverse.

I recommend using a probing-based hash table rather than a bucketed one so that you only need 1 memory allocation to store the table. This requires implementing a lazy delete strategy.

A reasonable hash function for pointers is x >> VirtualAddressOffsetSizeInBits, but you are welcome to use whatever hash function you want.

Note: When creating your hash table, you cannot use malloc() or new to allocate memory! This would defeat the purpose of the assignment, which is to write your own allocator. You'll have to use map() to allocate the hash table also.

Stage 1: Write a Linear Probing Hash Table Class

The hash table will store addresses and the associated size of memory allocated to that address.
Hash the address to find the location to place the address/size into the hash table.
If there is a collision, look for the next available bucket to put the entry into.
When an address is freed up (in the main program), it needs to be removed from the hash table...
Think about what is required to remove an entry from a linear probed hash table.
Public methods the hash table should support: insert(), remove()
Private methods that can make the hash table implementation easier:
find() - returns the address of an entry.
grow() - create a new table that is twice the size of the current table, then re-hash all the current entries into the new table.
Stage 2:Malloc Handling Class

Create a MyMalloc (or some such) class.
The MyMalloc class will contain a hash table to keep track of allocated addresses and their sizes.
It should include the allocate() and deallocate() methods.
Stage 3: Write a main program / test your program.

Create a MyMalloc object.
Overload malloc() and free() to use your MyMalloc object's allocate() and deallocate().
Create a bunch of test routines:
Allocate a large number of small memory objects
Manipulate the data in those objects
Create more objects
Verify the data in the objects stays valid
Allocate a large number of large memory objects
Test them
Deallocate your objects
Verify MyMalloc / hash table are correct
Allocate small and large blocks of memory
Time how long it takes to malloc() and free() memory.  Note, if you have overloaded malloc() and free() (to allocate() and deallocate()), these times will be for your version.  Un-overload them when timing the system malloc() and free().
Come up with more tests...
Some overall pointers from my naïve implementation when allocate() is called:

1. Calls mmap() to allocate memory.

2. Tries to store the ptr in the hash table.

3. If the hash table needs to be grown, this results in a second call to mmap() inside a  growHashTable() function.

4. All entries (except lazy deletes) are copied over into the new larger hash table.

    - What is a "lazy delete" from a hash table? [Answer this question in comments in your code.]

5. The new hash table is simply swapped with the original one and the original one is deallocated or simply allowed to go out of scope in the growHashTable() function. Hint: swaps can be done with std::swap().

You don't HAVE to do things this exact same way, but some variation on this will be needed. A replacement to malloc cannot call malloc or new in it. Ditto for free() or delete().

Remember that C/C++ use new/malloc to allocate memory on the heap. However, if you just declare a variable in a function, that creates it on the stack. The latter is allowed for the assignment!

Requirements
Include unit testing showing that you can correctly allocate and deallocate memory (allocations shouldn't overlap, your allocator shouldn't crash, etc).

Include some simple microbenchmarks (timings) to compare your allocator vs the built-in malloc.

Include instructions for how to run your tests and benchmarks.

Include a write-up of why your implementation is slow (compared to the system version) and ways it could be made faster (~1 page).  Name this file "conclusion.pdf".

Extra Credit (10 pts)
Our allocator has a ton of internal fragmentation since very few calls to allocate will ask for multiples of the page size. This leads to lots of memory waste since our allocator always returns chunks that are multiples of page size.

For a bonus, develop an improved allocator that reduces this fragmentation. Test and benchmark this allocator to make sure it works correctly and is actually faster than the naive version described above.

