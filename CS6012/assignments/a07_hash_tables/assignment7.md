# Assignment 7: Hash Tables


We have been asked to construct a hash table of strings. We know that hash tables are extremely valuable in performing insertions and searches in constant time, on average. 

The way in which we resolve collisions in a hash table is critical to attaining a running time of O(1) in the average case. Such collisions are unavoidable because we have many more possible items than positions in the table. However, the number of collisions strongly depends on the effectiveness of the hash function at distributing items evenly throughout the table. Thus, we are interested in evaluating the performance of several hashing functions to determine which strategy best guarantees the O(1) average-case running time. (Recall that a good hash function not only attempts even distribution, but is also very simple and fast, as it must be invoked many times.)


## Requirements

Be sure your code is in a package named to `assignment07`.

### Set interface
The [Set.java interface](Set.java) has been provided for you. It contains all of the operations that your hash table classes must implement.  Do not use the Java Library set interface!!  It has a bunch of extra methods that you won't want to implement!

### ChainingHashTable class

Add a `ChainingHashTable` class which implements the `Set` interface. The class must represent a hash table of `String` objects and must use separate chaining to resolve collisions. You do not need to implement rehashing for this hash table, since it will never be full, however, if you don't implement rehashing, performance may suffer, depending on the initial size of the table.

To get you started, the header for your ChainingHashTable class is given here:

```
     public class ChainingHashTable implements Set<String>
```

ChainingHashTable must also contain a constructor:

```
     public ChainingHashTable(int capacity, HashFunctor functor)
```

To allow your ChainingHashTable class to be used with any hash function, notice that the constructor accepts a function object containing the `int hash(String item)` method, and that method should be used whenever an item's hash code is needed. The `HashFunctor` interface is given below.

Since ChainingHashTable will require an array of `LinkedList<String>`, which is a generic type, Java will give a warning when initializing it. We must specifically cast a non-generic array to a generic one. Use the following as a guideline for declaring your generic array:

Declare the array in your class:

```
     private LinkedList<String>[] storage;
```

Initialize the array in your constructor:

```
     storage = (LinkedList<String>[]) new LinkedList[capacity];
```

The above line will give a warning, but it is safe to add:

```
     @SuppressWarnings("unchecked") to your constructor.
```

### HashFunctorinterface

The [HashFunctor.java interface](HashFunctor.java) has been provided for you and serves as a guide for how to define a functor that contains a hashing function for String items (i.e., the hash method).

For example, to define a really bad hash function, one might write the following class:

```
     public class ReallyBadHashFunctor implements HashFunctor { 
       public int hash(String item) { 
         return 0; 
       } 
     }
```

You must provide three functors, each containing different hashing functions for String items:

* Create one hashing function that you think is bad (although not as bad as the one above). You should expect that it will result in many collisions. Put the hashing function in the class BadHashFunctor.
* Create one hashing function that you think is mediocre. You should expect that it will result in fewer collisions than the bad one. Put the hashing function in the class MediocreHashFunctor.
* Create one hashing function that you think is good. You should expect that it will result in few or no collisions. Put the hashing function in the class GoodHashFunctor. Do not use String's built-in hashCode method, you must implement your own - although you can implement the same algorithm that it uses (you just have to write it yourself). Consult the web for String hash function ideas.

Here are some links to get you started:

* http://www.cse.yorku.ca/~oz/hash.html
* https://stackoverflow.com/questions/7666509/hash-function-for-string
* https://stackoverflow.com/questions/2624192/good-hash-function-for-strings
* http://cseweb.ucsd.edu/~kube/cls/100/Lectures/lec16/lec16-12.html

Each of your functor classes `BadHashFunctor`, `MediocreHashFunctor`, and `GoodHashFunctor` must implement the `HashFunctor` interface and must be in the `assignment07` package.

### Finishing up

Create your own tests and commit them with your program to your github repo. When preliminary coding is complete and your program compiles without error or warning, test the program thoroughly and systematically. Write your analysis document.

Your code should be well-commented (Javadoc comments are required) and formatted such that it is clear and easy to read. You must have at least: comments for every method you write, describing what the method does, what the arguments are (and what they mean), and what the return value is, as well as any special cases that the method handles or can run in to. You should also have comments on any line or block of code that is not self-explanatory.

Submit your ChainingHashTable.java, BadHashFunctor.java, MediocreHashFunctor.java, and BadHashFunctor.java files to gradescope and commit all your code to github.

### Bonus Part

This part is optional, but you will get bonus points if you implement it successfully. Implement quadratic probe hash table. To the `assignment07` package add a `QuadProbeHashTable` class  which implements the Set interface. The class must represent a hash table of String objects and must use quadratic probing to resolve collisions.

To get you started, the header for your QuadProbeHashTable class is given here:

```
     public class QuadProbeHashTable implements Set<String>
```

Your QuadProbeHashTable class must contain the following constructor:
```
     /** Constructs a hash table of the given capacity that uses the hashing function
       * specified by the given functor.
       */
     public QuadProbeHashTable(int capacity, HashFunctor functor)
```

Recall that the table size should be a prime number and that the table should be resized and rehashed when $\lambda$ exceeds 0.5. (Hint: If the capacity given in the constructor is not prime, use the next largest prime number as the table size.) I recommend using the BigInteger class to generate prime numbers

## Analysis Doc

1. Explain the hashing function you used for BadHashFunctor. Be sure to discuss why you expected it to perform badly (i.e., result in many collisions).
2. Explain the hashing function you used for MediocreHashFunctor. Be sure to discuss why you expected it to perform moderately (i.e., result in some
collisions).
3. Explain the hashing function you used for GoodHashFunctor. Be sure to discuss why you expected it to perform well (i.e., result in few or no collisions).
4. Design and conduct an experiment to assess the quality and efficiency of each of your three hash functions. Briefly explain the design of your experiment. Plot the results of your experiment. Since the organization of your plot(s) is not specified here, the labels and titles of your plot(s), as well as, your interpretation of the plots is important. A recommendation for this experiment is to create two plots: one that shows the number of collisions incurred by each hash function for a variety of hash table sizes, and one that shows the actual running time required by various operations using each hash function for a variety of hash table sizes.
5. What is the cost of each of your three hash functions (in Big-O notation)? Note that the problem size (N) for your hash functions is the length of the String, and has nothing to do with the hash table itself. Did each of your hash functions perform as you expected (i.e., do they result in the expected number of collisions)?

Commit your PDF document to github.
