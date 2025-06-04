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

### Query Solutions:

**1. T1 ⋈(T1.A=T2.A) T2**
```
A   Q   R   B   C
20  a   5   b   6
20  a   5   b   5
```
*Explanation: Join T1 and T2 where A values match. Only T1's row with A=20 matches T2's two rows with A=20.*

**2. T1 ⋈(T1.Q=T2.B) T2**
```
T1.A  Q   R   T2.A  B   C
25    b   8   20    b   6
25    b   8   20    b   5
```
*Explanation: Join where T1.Q equals T2.B. Only T1's row with Q='b' matches T2's rows with B='b'.*

**3. T1 ⋈ T2 (Natural Join)**
```
A   Q   R   B   C
20  a   5   b   6
20  a   5   b   5
```
*Explanation: Natural join on common attribute A. Same result as query 1.*

**4. T1 ⋈(T1.A=T2.A∧T1.R=T2.C) T2**
```
A   Q   R   B   C
20  a   5   b   5
```
*Explanation: Join where BOTH conditions are met: T1.A=T2.A AND T1.R=T2.C. Only one combination satisfies both A=20 and R=C=5.*

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

**Given LMS Database:**
```
Students:
sID  Name      DOB
1    Hermione  1980
2    Harry     1979
3    Ron       1980
4    Malfoy    1982

Enrolled:
sID  cID   Grd
1    3500  A
1    3810  A-
1    5530  A
2    3810  A
2    5530  B
3    3500  C
3    3810  B
4    3500  C

Courses:
cID   Name
3500  SW Practice
3810  Architecture
5530  Databases
```

### Part 3.1
**Query:**
```
ρ(C, π_sid(σ_Grd=C(Enrolled)))
π_Name((π_sid(Enrolled)−C) ⋈ Students)
```

**a) Result Table:**
```
Name
Hermione
Harry
```

**b) English Description:**
This query finds the names of all students who are enrolled in courses but have never received a grade of 'C'. Students Ron and Malfoy both got C grades, so only Hermione and Harry remain.

### Part 3.2
**Query:**
```
ρ(S1, Students)
ρ(S2, Students)
π_S2.Name(σ_S1.Name==Ron∧S1.DOB==S2.DOB∧S2.name!=Ron(S1 × S2))
```

**a) Result Table:**
```
Name
Hermione
```

**b) English Description:**
This query finds the names of all students who have the same date of birth as "Ron" (1980), but are not Ron himself. Only Hermione shares Ron's birth year of 1980.

### Part 3.3
**Query:**
```
π_cName((π_cID,sID(Enroll)/π_sID(Students)) ⋈ Courses)
```

**a) Result Table:**
```
cName
(empty - no results)
```

**b) English Description:**
This query finds the names of courses that ALL students in the database are enrolled in. Since no course has all four students (Hermione, Harry, Ron, and Malfoy) enrolled, the result is empty. Course 3810 comes closest with 3 out of 4 students.

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


