/*
  Author: Daniel Kopta and ??
  July 2017
  
  CS 6010 Fall 2019
  Vector util library definitions

  Complete the functions defined in this file.

  Some basic tests for your library are provided in VectorTest.cpp.
  Compile the tests together with your library using the following command:
  clang++ -std=c++11 VectorTest.cpp VectorUtil.cpp

  Most of the provided tests will fail until you have provided correct
  implementations for the VectorUtil library functions.

  You will need to provide more thorough tests.
*/

#include "VectorUtil.h"

//std::string boolToString( bool trueFalse ){
//    if (trueFalse == true){
//        return "true";
//    } else {
//        return "false";
//    }
//}

/*
 * Determines whether or not a vector contains a certain item.
 *
 * Parameters:
 *   input -- The vector to search
 *   lookFor -- The item to look for
 *
 * Returns:
 *   Whether or not the item is contained in the vector
 */
bool Contains( vector<int> input, int lookFor )
{
  // TODO: Fill in implementation. Do not always return false.
    for (int i = 0; i < input.size(); i++){
        if (input[i] == lookFor){
            return true;
        }
    }
    return false;
}

/*
 * Determines the minimum value in a vector.
 *
 * Assumes the vector is non-empty.
 *
 * Parameters:
 *   input -- A vector of integers
 *
 * Returns:
 *   The smallest item in the vector
 */
int FindMin( vector<int> input )
{
  // TODO: Fill in implementation. Do not always return 0.
    
    // is input.at(0) < input.at(1)? if true keep input.at(0) and if not assign input.at(1) to compare
    
    int minValue = input[0];
    
    for (int i = 0; i < input.size(); i++){
        if (minValue > input[i]){
            minValue = input[i];
        }
    }
  return minValue;
}

/*
 * Determines the maximum value in a vector.
 *
 * Assumes the vector is non-empty
 *
 * Parameters:
 *   input -- A vector of integers
 *
 * Returns:
 *   The largest item in the vector
 */
int FindMax( vector<int> input )
{
  // TODO: Fill in implementation. Do not always return 0.
    
    // is input.at(0) > input.at(1)? if true keep input.at(0) and if not assign input.at(1) to compare
    
    int maxValue = input[0];
    for (int i = 1; i < input.size(); i++){
        if (maxValue < input[i]){
            maxValue = input[i];
        }
    }
    return maxValue;
}

/*
 * Determines the average of all values in a vector
 *
 * Assumes the vector is non-empty
 *
 * Parameters:
 *   input -- A vector of integers
 *
 * Returns:
 *   The (integer) average of all values in the vector
 */
int Average( vector<int> input )
{
  // TODO: Fill in implementation. Do not always return 0.
    
    int sum = 0;
    for (int i = 0; i < input.size(); i++)
        sum += input[i];
    return sum/input.size() ;
}


/*
 * Determines whether or not the items in a vector are in non-descending order
 *
 * Non-descending order is similar to ascending order, except that it allows for
 * duplicate items to appear next to each other.
 * i.e., no item appearing at a lower index than another item is greater than that
 * other item.
 *
 * Examples:
 *  {1, 2, 2, 15, 70} is sorted
 *  {2, 3, 0} is not sorted
 *
 * Parameters:
 *   input -- A vector of integers
 *
 * Returns:
 *   True if the vector is sorted in non-descending order, false otherwise
 *   An empty vector is considered sorted, since there are no items out of order
 *   A single-item vector is considered sorted, since there are no items out of order
 */
bool IsSorted( vector<int> input )
{
  // TODO: Fill in implementation. Do not always return false.
    
    // is input.at(0) < input.at(1) < input.at(2) ...
    // is input[0] < input [1]? if no, return false
    // if yes, continue to is input[1] < input[2] and so on
    if (input.empty()) return true;
    for (int i = 0; i < input.size(); i++)
        if (input[i] < input[i-1]){
            return true;
        }
            return false;
}
