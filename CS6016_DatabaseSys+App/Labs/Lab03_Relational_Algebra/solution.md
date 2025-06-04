# Relational Algebra Homework Solutions

**Student Name:** Brandon Mountan
**Course:** CS6016
**Assignment:** Relational Algebra Queries  
**Date:** 06/05/2025

## Part 1 - Joins

**Given Tables:**
```
T1:
A   Q   R
20  a   5
25  b   8
35  a   6

T2:
A   B   C
20  b   6
45  c   3
20  b   5
```

*Note: The specific join operations from the screenshot are not visible, but here are common join operations:*

**T1 ⋈ T2 (Natural Join on A):**
```
A   Q   R   B   C
20  a   5   b   6
20  a   5   b   5
```

**T1 ⋈ₐ T2 (Theta Join where T1.A = T2.A):**
Same result as natural join above.

## Part 2 - Chess Queries

**Given Schemas:**
- Events(Name, Year, eID)
- Players(Name, Elo, pID)
- Games(gID, eID, Result, wpID, bpID)

### Query Solutions:

**1. Find the names of any player with an Elo rating of 2850 or higher.**
```
π(Name)(σ(Elo ≥ 2850)(Players))
```
*Explanation: Select players with Elo ≥ 2850, then project their names.*

**2. Find the names of any player who has ever played a game as white.**
```
π(Name)(Players ⋈(pID=wpID) Games)
```
*Explanation: Join Players with Games on white player ID, then project names.*

**3. Find the names of any player who has ever won a game as white.**
```
π(Name)(Players ⋈(pID=wpID) σ(Result='1-0')(Games))
```
*Explanation: First filter games where white won (1-0), join with Players on white player ID, then project names.*

**4. Find the names of any player who played any games in 2018.**
```
WhitePlayers2018 ← π(Name)(Players ⋈(pID=wpID) (Games ⋈ σ(Year=2018)(Events)))
BlackPlayers2018 ← π(Name)(Players ⋈(pID=bpID) (Games ⋈ σ(Year=2018)(Events)))
WhitePlayers2018 ∪ BlackPlayers2018
```
*Explanation: Find players who played as white OR black in 2018 events, then union the results.*

**5. Find the names and dates of any event in which Magnus Carlsen lost a game.**
```
MagnusID ← π(pID)(σ(Name='Magnus Carlsen')(Players))
MagnusWhiteLosses ← σ(Result='0-1')(Games ⋈(wpID=pID) MagnusID)
MagnusBlackLosses ← σ(Result='1-0')(Games ⋈(bpID=pID) MagnusID)
AllMagnusLosses ← MagnusWhiteLosses ∪ MagnusBlackLosses
π(Name,Year)(Events ⋈ AllMagnusLosses)
```
*Explanation: Find Magnus's ID, then find games where he lost (as white: 0-1, as black: 1-0), join with Events to get event details.*

**6. Find the names of all opponents of Magnus Carlsen.**
```
MagnusID ← π(pID)(σ(Name='Magnus Carlsen')(Players))
MagnusAsWhite ← π(bpID)(Games ⋈(wpID=pID) MagnusID)
MagnusAsBlack ← π(wpID)(Games ⋈(bpID=pID) MagnusID)
OpponentIDs ← ρ(pID←bpID)(MagnusAsWhite) ∪ ρ(pID←wpID)(MagnusAsBlack)
π(Name)(Players ⋈ OpponentIDs)
```
*Explanation: Find Magnus's ID, get opponent IDs from games where he played as white or black, then join with Players to get opponent names.*

## Part 3 - LMS Queries

*Note: The screenshots showing the specific relational algebra queries are not visible. The solutions would depend on:*
- The LMS database schema (Students, Courses, Enrollments, etc.)
- The specific relational algebra expressions in the screenshots

**General approach for LMS queries:**
- Part 3.1, 3.2, 3.3 would each involve executing the given relational algebra expression
- Results would be tables showing the selected/projected data
- English descriptions would explain what each query finds (e.g., "students enrolled in specific courses", "courses with high enrollment", etc.)

## Part 4 - Divide Operator Query

**Find the names of all students who are taking all of the 3xxx-level classes.**

```
ThreeThousandCourses ← π(CourseID)(σ(CourseID LIKE '3___')(Courses))
StudentCourses ← π(StudentID, CourseID)(Enrollments)
StudentsWithAll3000 ← StudentCourses ÷ ThreeThousandCourses
π(Name)(Students ⋈ StudentsWithAll3000)
```

*Explanation: First find all 3xxx-level courses, then find student-course pairs from enrollments, use division to find students taking ALL 3xxx courses, finally join with Students to get names.*

**Alternative notation:**
```
π(Name)(Students ⋈ (π(StudentID, CourseID)(Enrollments) ÷ π(CourseID)(σ(CourseID LIKE '3___')(Courses))))
```

## Key Concepts Used:

- **Selection (σ)**: Filter rows based on conditions
- **Projection (π)**: Select specific columns
- **Join (⋈)**: Combine tables based on conditions
- **Union (∪)**: Combine results from multiple queries
- **Division (÷)**: Find tuples that are related to ALL tuples in another relation
- **Renaming (ρ)**: Change attribute names for compatibility

## Tips for Relational Algebra:
1. Break complex queries into smaller steps using intermediate relations
2. Use natural joins when tables share common attribute names
3. Use theta joins when you need to specify join conditions
4. Remember that division finds "for all" relationships
5. Use union when you need to combine results from OR conditions