# Lab/Assignment 1: Degree Planner

In this assignment/lab, we'll build a simple app for planning an undergrad degree.

We'll build on this app for at least one more assignment, so we'll focus on making it easy to modify/update.

Here's the requirements for this phase of the assignment:

A user must be able to enter/update/edit the list of courses they plan to take (courses have a department string like "CS" and a number like 6018).

There must be a list of degree requirements, each of which much visually indicate whether the requirement is satisfied by the courses in the user's list or not.  If ALL requirements are satisfied, then the app should indicate that somehow.

For now, the type of requirements we must support are specific classes (you must take CS 101) and you must pick one course from a set (you must take PHIL 101 or SOC 101)


## Aside: Functions and Functional Programming are great

As a challenge to you, I recommend embracing "functional" style programming as much as possible.  An example is replacing code like this:

```kotlin
var ret = listOf<Whatever>()
for( x in something){
	ret.add(x.method())
}
```

with the functional equivalent:

```kotlin
val ret = something.map{it.method}
```

Chat with your friends/TA/professor about why the second version might be preferable.

## Finishing up (read before starting)

Since we'll be buildling more features on this, we want to make sure that our code is in a good place after this phase

* Split up big Composables into small pieces
* Be sure that you use "state hoisting" so that you can defined `@Preview @Composable` previews to see these in action without running the app.  I recommend writing a `@Preview` at the same time you write the `@Composable` itself!
* Write unit tests for any "business logic" like checking if requirements are satisfied, etc
* Try to replace loops with functional alternatives when possible

We'll take a look at how well you did here next week during code review