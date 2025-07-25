# SQL Lab Assignment Solutions

## Part 1 - SQL Command Line

## Part 2 - Creating the Dealership Database

### Dealership Schema (Problem 4 Solution)
```sql
-- Switch to database
USE u6033375;

-- Car table
CREATE TABLE Car (
    VIN VARCHAR(255) PRIMARY KEY,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INT UNSIGNED NOT NULL,
    color VARCHAR(255)
);

-- Salesperson table
CREATE TABLE Salesperson (
    SSN VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- SalesAssignment table (many-to-many relationship)
CREATE TABLE SalesAssignment (
    VIN VARCHAR(255) NOT NULL,
    SSN VARCHAR(255) NOT NULL,
    PRIMARY KEY (VIN, SSN),
    FOREIGN KEY (VIN) REFERENCES Car(VIN),
    FOREIGN KEY (SSN) REFERENCES Salesperson(SSN)
);
```

### Populating the Tables with Lab 1 Data

```sql
-- Insert Car data
INSERT INTO Car (VIN, make, model, year, color) VALUES
('1NXBR32E38Z123456', 'Toyota', 'Tacoma', 2008, 'Red'),
('1NXBR32E27Z789012', 'Toyota', 'Tacoma', 1999, 'Green'),
('5YJ3E1EA8JF345678', 'Tesla', 'Model 3', 2018, 'White'),
('JF1VA1A68G9901234', 'Subaru', 'WRX', 2016, 'Blue'),
('1FTFW1ET44FB567890', 'Ford', 'F150', 2004, 'Red');

-- Insert Salesperson data
INSERT INTO Salesperson (SSN, name) VALUES
('123-45-6789', 'Arnold'),
('987-65-4321', 'Hannah'),
('555-12-3456', 'Steve');

-- Insert SalesAssignment data (who is trying to sell which cars)
-- Arnold sells all Toyotas
INSERT INTO SalesAssignment (VIN, SSN) VALUES
('1NXBR32E38Z123456', '123-45-6789'), -- Red Toyota Tacoma 2008
('1NXBR32E27Z789012', '123-45-6789'); -- Green Toyota Tacoma 1999

-- Hannah sells all red cars  
INSERT INTO SalesAssignment (VIN, SSN) VALUES
('1NXBR32E38Z123456', '987-65-4321'), -- Red Toyota Tacoma 2008
('1FTFW1ET44FB567890', '987-65-4321'); -- Red Ford F150 2004

-- Steve sells the Tesla
INSERT INTO SalesAssignment (VIN, SSN) VALUES
('5YJ3E1EA8JF345678', '555-12-3456'); -- White Tesla Model 3 2018
```

## Part 3 - Simple Retrieval Queries (Library Database)

```sql
-- 1. Get the ISBNs of all books by <Author>
SELECT DISTINCT ISBN 
FROM Titles 
WHERE Author = '<Author>';

-- 2. Get Serial numbers (descending order) of all books by <ISBN>
SELECT Serial 
FROM Inventory 
WHERE ISBN = '<ISBN>' 
ORDER BY Serial DESC;

-- 3. Get the Serial numbers and ISBNs of all books checked out by <Patron's name>
SELECT c.Serial, i.ISBN
FROM Patrons p
JOIN CheckedOut c ON p.CardNum = c.CardNum
JOIN Inventory i ON c.Serial = i.Serial
WHERE p.Name = '<Patron\'s name>';

-- 4. Get phone number(s) and Name of anyone with <ISBN> checked out
SELECT DISTINCT ph.Phone, p.Name
FROM Patrons p
JOIN CheckedOut c ON p.CardNum = c.CardNum
JOIN Inventory i ON c.Serial = i.Serial
JOIN Phones ph ON p.CardNum = ph.CardNum
WHERE i.ISBN = '<ISBN>';
```

## Part 4 - Intermediate Retrieval Queries (Library Database)

```sql
-- 1. Find the Authors of the library's oldest <N> books (lowest serial numbers)
SELECT DISTINCT t.Author
FROM Titles t
JOIN Inventory i ON t.ISBN = i.ISBN
ORDER BY i.Serial ASC
LIMIT <N>;

-- 2. Find the name and phone number of the person who has checked out the most recent book
-- (highest serial number that has been checked out)
SELECT p.Name, ph.Phone
FROM Patrons p
JOIN CheckedOut c ON p.CardNum = c.CardNum
JOIN Phones ph ON p.CardNum = ph.CardNum
WHERE c.Serial = (
    SELECT MAX(c2.Serial)
    FROM CheckedOut c2
);

-- 3. Find the phone number(s) and name of anyone who has checked out any book
SELECT DISTINCT ph.Phone, p.Name
FROM Patrons p
JOIN CheckedOut c ON p.CardNum = c.CardNum
JOIN Phones ph ON p.CardNum = ph.CardNum;

-- 4. Find the Authors and Titles of books NOT checked out by anyone (no duplicates)
SELECT DISTINCT t.Author, t.Title
FROM Titles t
JOIN Inventory i ON t.ISBN = i.ISBN
LEFT JOIN CheckedOut c ON i.Serial = c.Serial
WHERE c.Serial IS NULL;
```

## Part 5 - Chess Queries

```sql
-- 1. Find the names and IDs of any player with the 10 highest Elo ratings
SELECT Name, pID
FROM Players
ORDER BY Elo DESC
LIMIT 10;

-- 2. Find the names and Elo ratings of any player who has ever played a game as black
SELECT DISTINCT p.Name, p.Elo
FROM Players p
JOIN Games g ON p.pID = g.BlackPlayer;

-- 3. Find the names of any player who has ever won a game as white
SELECT DISTINCT p.Name
FROM Players p
JOIN Games g ON p.pID = g.WhitePlayer
WHERE g.Result = 'W';

-- 4. Find the names of any player who played any games between 2014 and 2018 in Budapest HUN
SELECT DISTINCT p.Name
FROM Players p
JOIN Games g ON (p.pID = g.WhitePlayer OR p.pID = g.BlackPlayer)
JOIN Events e ON g.eID = e.eID
WHERE e.Site = 'Budapest HUN'
  AND YEAR(e.Date) BETWEEN 2014 AND 2018;

-- 5. Find the Sites and dates of any event in which Garry Kasparov won a game
SELECT DISTINCT e.Site, e.Date
FROM Events e
JOIN Games g ON e.eID = g.eID
JOIN Players p ON (
    (p.pID = g.WhitePlayer AND g.Result = 'W') OR
    (p.pID = g.BlackPlayer AND g.Result = 'B')
)
WHERE p.Name = 'Kasparov, Garry';

-- 6. Find the names of all opponents of Magnus Carlsen
SELECT DISTINCT p.Name
FROM Players p
WHERE p.pID IN (
    SELECT g.BlackPlayer
    FROM Games g
    JOIN Players carlsen ON g.WhitePlayer = carlsen.pID
    WHERE carlsen.Name = 'Carlsen, Magnus'
    
    UNION
    
    SELECT g.WhitePlayer
    FROM Games g
    JOIN Players carlsen ON g.BlackPlayer = carlsen.pID
    WHERE carlsen.Name = 'Carlsen, Magnus'
)
AND p.Name != 'Carlsen, Magnus';
```
