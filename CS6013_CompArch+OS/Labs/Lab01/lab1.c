/*********************************************************************
 *
 * Brandon Mountan
 * CS6013
 * 01/06/25
 * Lab 01 Warmup Lab
 *
 */

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h> // For strlen()

/*********************************************************************
 *
 * The C functions in this lab use patterns and functionality often found in
 * operating system code. Your job is to implement them.  Additionally, write some test
 * cases for each function (stick them in functions [called xyzTests(), etc or similar])
 * Call your abcTests(), etc functions in main().
 *
 * Write your own tests for each function you are testing, then share/combine
 * tests with a classmate.  Try to come up with tests that will break people's code!
 * Easy tests don't catch many bugs! [Note this is one specific case where you may
 * share code directly with another student.  The test function(s) from other students
 * must be clearly marked (add '_initials' to the function name) and commented with the
 * other student's name.
 *
 * Note: you may NOT use any global variables in your solution.
 * 
 * Errata:
 *   - You can use global vars in your testing functions (if necessary).
 *   - Don't worry about testing the free_list(), or the draw_me() functions.
 *
 * You must compile in C mode (not C++).  If you compile from the commandline
 * run clang, not clang++. Submit your solution files on Canvas.
 *
 *********************************************************************/

/*********************************************************************
 *
 * byte_sort()
 *
 * specification: byte_sort() treats its argument as a sequence of
 * 8 bytes, and returns a new unsigned long integer containing the
 * same bytes, sorted numerically, with the smaller-valued bytes in
 * the lower-order byte positions of the return value
 * 
 * EXAMPLE: byte_sort (0x0403deadbeef0201) returns 0xefdebead04030201
 * Ah, the joys of using bitwise operators!
 *
 * Hint: you may want to write helper functions for these two functions
 *
 *********************************************************************/

long* unsortedLongToArray( unsigned long arg, int size ){
  unsigned long shiftedArg;
  unsigned long maskedArg;
  long* array = (long*)malloc( size * sizeof( long ) );
  for ( int i = 0; i < size; i++ ){
    if (size == 8) {
    shiftedArg = arg >> ( 8 * i );
    maskedArg = shiftedArg & 0x00000000000000FF;
    array[i] = maskedArg;
    } else if (size == 16) {
      shiftedArg = arg >> ( 4 * i );
      maskedArg = shiftedArg & 0x000000000000000F;
      array[i] = maskedArg;
    }
  }
  return array;
}

void sortArray( long* array, int size ) {
  for ( int i = 0; i < size; i++ ){
    int minIndex = i;
    for ( int j = i + 1; j < size; j++ ){
      if ( array[ j ] < array[ minIndex ] ){
        minIndex = j;
      }
    }
    unsigned long temp = array[ minIndex ];
    array[ minIndex ] = array[ i ];
    array[ i ] = temp;
  }
}

unsigned long arrayToLong( long* array, int size ){
  unsigned long shiftedLong;
  unsigned long finalLong = 0;
  for ( int i = 0; i < size; i++ ){
    if (size == 8) {
     shiftedLong = array[ i ] << ( 8 * i );
     finalLong = finalLong + shiftedLong;
     } else if (size == 16) {
       shiftedLong = array[ i ] << ( 4 * i );
       finalLong = finalLong + shiftedLong;
     }
  }
  return finalLong;
}

unsigned long byte_sort( unsigned long arg )
{
  long* array = unsortedLongToArray( arg, 8 );
  sortArray( array, 8 );
  unsigned long answer = arrayToLong( array, 8 );
  free( array );
  return answer;
}

/*********************************************************************
 *
 * nibble_sort()
 *
 * specification: nibble_sort() treats its argument as a sequence of 16 4-bit
 * numbers, and returns a new unsigned long integer containing the same nibbles,
 * sorted numerically, with smaller-valued nibbles towards the "small end" of
 * the unsigned long value that you return
 *
 * the fact that nibbles and hex digits correspond should make it easy to
 * verify that your code is working correctly
 * 
 * EXAMPLE: nibble_sort (0x0403deadbeef0201) returns 0xfeeeddba43210000
 *
 *********************************************************************/

unsigned long nibble_sort (unsigned long arg)
{
  long* array = unsortedLongToArray( arg, 16 );
  sortArray( array, 16 );
  unsigned long answer = arrayToLong( array, 16 );
  free( array );
  return answer;
}

/*********************************************************************/

typedef struct elt {
  char val;
  struct elt *link;
} Elt;

/*********************************************************************/

/* Forward declaration of "free_list()"... This allows you to call   */
/* free_list() in name_list() [if you'd like].                       */
void free_list( Elt* head ); // [No code goes here!]

/*********************************************************************
 *
 * name_list()
 *
 * specification: allocate and return a pointer to a linked list of
 * struct elts
 *
 * - the first element in the list should contain in its "val" field the first
 *   letter of your first name; the second element the second letter, etc.;
 *
 * - the last element of the linked list should contain in its "val" field
 *   the last letter of your first name and its "link" field should be a null
 *   pointer
 *
 * - each element must be dynamically allocated using a malloc() call
 * 
 * - you must use the "name" variable (change it to be your name).
 *
 * Note, since we're using C, not C++ you can't use new/delete!
 * The analog to delete is the free() function
 *
 * - if any call to malloc() fails, your function must return NULL and must also
 *   free any heap memory that has been allocated so far; that is, it must not
 *   leak memory when allocation fails
 *
 * Implement print_list and free_list which should do what you expect.
 * Printing or freeing a nullptr should do nothing.
 *
 * Note: free_list() might be useful for error handling for name_list()... 
 *
 *********************************************************************/


Elt *name_list( char * name )
{
  if (name == NULL || strlen(name) == 0) {
    return NULL; // Handle invalid or empty name input
  }

  int size = strlen(name);
  Elt *head = NULL, *current = NULL;

  for (int i = 0; i < size; i++) {
    Elt *newElt = (Elt *)malloc(sizeof(Elt));
    if (newElt == NULL) {
      // Handle memory allocation failure
      free_list(head); // Free any allocated nodes
      return NULL;
    }
    newElt->val = name[i];
    newElt->link = NULL;

    if (head == NULL) {
      // Initialize the head
      head = newElt;
    } else {
      // Link the new element to the current list
      current->link = newElt;
    }
    current = newElt;
  }

  return head;
}

/*********************************************************************/

void print_list( Elt* head )
{
  Elt *current = head;
  while (current != NULL) {
    printf("%c -> ", current->val);
    current = current->link;
  }
  printf("NULL\n");
}

/*********************************************************************/

void free_list( Elt* head )
{
  while (head != NULL) {
    Elt *temp = head;
    head = head->link;
    free(temp);
  }
}

/*********************************************************************
 *
 * draw_me()
 *
 * This function creates a file called 'me.txt' which contains an ASCII-art
 * picture of you (it does not need to be very big).
 * 
 * Use the C stdlib functions: fopen, fclose, fprintf, etc which live in stdio.h
 * - Don't use C++ iostreams
 *
 *********************************************************************/

void draw_me()
{
  FILE *file = fopen("me.txt", "w");
  if (file == NULL) {
    perror("Error opening file");
    return;
  }

  // Write ASCII art for a face
  fprintf(file, "   _____   \n");
  fprintf(file, "  /     \\  \n");
  fprintf(file, " |  o o  | \n");
  fprintf(file, " |   ^    | \n");
  fprintf(file, " |  '-'   | \n");
  fprintf(file, "  \\_____/  \n");

  fclose(file);
  printf("Face ASCII art created in 'me.txt'.\n");
}

/*********************************************************************
 *
 * Test Code - Place your test functions in this section:
 */

// bool testByteSort() { ... }
// ...
// ...

/*********************************************************************
 *
 * main()
 *
 * The main driver program.  You can place your main() method in this
 * file to make compilation easier, or have it in a separate file.
 *
 *
 *********************************************************************/

int main()
{
   unsigned long test = byte_sort( 0x0403deadbeef0201 );
   printf("The unsigned long number using byte_sort is: %lu\n", test); //17284462070200467969
   unsigned long test2 = nibble_sort( 0x0403deadbeef0201 );
   printf("The unsigned long number using nibble_sort is: %lu\n", test2); //18369863722150723584

  // Test name_list and print_list
  char name[] = "Brandon";
  Elt *head = name_list(name);

  if (head == NULL) {
    printf("Failed to create linked list.\n");
    return 1;
  }

  printf("Linked list representation of name:\n");
  print_list(head);

  // Free the list
  free_list(head);

  // Test draw_me
  draw_me();

  return 0;
}
