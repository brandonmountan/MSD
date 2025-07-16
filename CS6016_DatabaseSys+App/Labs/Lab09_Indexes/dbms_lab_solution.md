# DBMS Index Design Lab Solutions

## Part 1 - Selecting Indexes

### s1 - Former Employee Records Table
**Table:** eID (int) (primary key), Start Date (Date), End Date (Date)

**Queries:**
1. Find all employees that started after a certain date
2. Find all employees that started on a certain date, and worked until at least another certain date

**Answer:** Index on (Start Date, End Date)

**Reasoning:** A composite index on (Start Date, End Date) efficiently supports both queries. For query 1, it can filter Start Date > X. For query 2, it can first filter Start Date = X (exact match), then within those results filter End Date >= Y (range condition). The ordering of the composite index supports both the equality and range conditions optimally.

### s2 - Student Grades Table  
**Table:** studentID (int) (primary key), className (varchar(10)) (primary key), Grade (char(1))

**Queries:**
1. Get all students with a grade better than 'B' (Grade = 'A')
2. Get all classes where any student earned a grade worse than 'D' (Grade = 'F')

**Answer:** Index on Grade

**Reasoning:** Both queries filter by Grade value. A single index on Grade supports efficient filtering for both Grade = 'A' and Grade = 'F' conditions.

### s3 - Same Grade Database, Different Queries
**Queries:**
1. Get all classes ordered by class name  
2. Get all students who earned an 'A' in a certain class

**Answer:** Index on (className, Grade)

**Reasoning:** Query 1 needs efficient ordering by className. Query 2 needs efficient filtering by className = X AND Grade = 'A'. A composite index on (className, Grade) supports both: it provides ordered access by className for query 1, and efficient filtering for the compound condition in query 2.

### s4 - Chess Database Queries
**Queries:**
1. `select Name from Players where Elo >= 2050;`
2. `select Name, gID from Players join Games where pID = WhitePlayer;`

**Answer:** Index on Players.Elo and Index on Games.WhitePlayer

**Reasoning:** Query 1 requires efficient filtering by Elo (range condition). Query 2 requires efficient join between Players.pID and Games.WhitePlayer. Since Players.pID already has a primary index, we need an index on Games.WhitePlayer to make the join efficient.

### s5 - Library Database Natural Join
**Query:** `select * from Inventory natural join CheckedOut;`

**Answer:** Index on CheckedOut.ItemID (assuming ItemID is the common join column)

**Reasoning:** Natural join requires efficient matching on common columns. Assuming Inventory.ItemID is the primary key (already indexed), we need an index on the corresponding column in CheckedOut to make the join efficient.

### s6 - More Library Queries
**Queries:**
1. `select * from Inventory natural join CheckedOut where CardNum=2;`
2. `select * from Patrons natural join CheckedOut;`

**Answer:** Index on CheckedOut.ItemID, Index on CheckedOut.CardNum, and Index on CheckedOut.PatronID

**Reasoning:** Query 1 needs efficient natural join plus filtering by CardNum. Query 2 needs efficient natural join between Patrons and CheckedOut. We need indexes on all join columns and filter columns that aren't already primary keys.

### s7 - Auto-scaffolded Library LINQ Query
**Query:** LINQ query selecting Titles with their associated Inventory serials

**Answer:** Index on Inventory.TitleID

**Reasoning:** This query requires joining Titles with Inventory. Assuming Titles.TitleID is the primary key, we need an index on Inventory.TitleID for efficient joining.

## Part 2 - B+ Tree Index Structures

### Students Table Analysis
**Record size:** studentID (4 bytes) + className (10 bytes) + Grade (1 byte) = 15 bytes  
**Page size:** 4096 bytes

#### Question 1: Rows in first leaf node before split
**Answer:** 273 rows

**Calculation:** 4096 ÷ 15 = 273.06... → 273 rows (floor)

#### Question 2: Maximum keys in internal node
**Answer:** 292 keys

**Calculation:** Primary key size = studentID (4) + className (10) = 14 bytes  
4096 ÷ 14 = 292.57... → 292 keys (floor)

#### Question 3: Maximum rows with height 1
**Answer:** 79,989 rows

**Calculation:** Height 1 = root + leaf level  
- Root can have max 292 keys → 293 child pointers
- Each leaf holds max 273 records  
- Maximum rows = 293 × 273 = 79,989

#### Question 4: Minimum rows with height 1  
**Answer:** 274 rows

**Calculation:** Height 1 requires minimum 2 leaf nodes  
- Each leaf must be ≥50% full: ceil(273 ÷ 2) = 137 records minimum
- Minimum rows = 2 × 137 = 274

#### Question 5: Secondary index on Grade - entries per leaf node
**Answer:** 819 entries

**Calculation:** Secondary index entry = Grade (1 byte) + record pointer (4 bytes) = 5 bytes  
4096 ÷ 5 = 819.2 → 819 entries (floor)

### Another Table (128-byte records)

#### Question 6: Maximum leaf nodes for 48 rows
**Answer:** 2 leaf nodes

**Calculation:** Records per leaf = 4096 ÷ 128 = 32 records  
Leaf nodes needed = ceil(48 ÷ 32) = ceil(1.5) = 2 nodes

#### Question 7: Minimum leaf nodes for 48 rows
**Answer:** 2 leaf nodes  

**Calculation:** Even with maximum packing (32 records per node), we cannot fit 48 records in a single node. Minimum required = ceil(48 ÷ 32) = 2 nodes.