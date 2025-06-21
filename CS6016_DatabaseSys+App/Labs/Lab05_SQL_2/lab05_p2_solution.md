# Additional Library Database Queries

**Name:** Brandon Mountan  
**Course:** CS6016  
**Assignment:** Relational Algebra Queries Continued  
**Date:** 06/20/2025

```sql
-- 1. Find the name of the patron who has checked out the most books
SELECT p.Name
FROM Patron p
JOIN CheckedOut c ON p.CardNum = c.CardNum
GROUP BY p.CardNum, p.Name
ORDER BY COUNT(*) DESC
LIMIT 1;

-- 2. Find the Titles of all books that were written by an author whose name starts with 'K'
SELECT Title
FROM Titles
WHERE Author LIKE 'K%';

-- 3. Find the Authors who have written more than one book
SELECT Author
FROM Titles
GROUP BY Author
HAVING COUNT(*) > 1;

-- 4. Find the Authors for which the library has more than one book in inventory
-- (includes multiple copies of the same book)
SELECT t.Author
FROM Titles t
JOIN Inventory i ON t.ISBN = i.ISBN
GROUP BY t.Author
HAVING COUNT(*) > 1;

-- 5. Customer loyalty program: names, book count, and loyalty level of each Patron
SELECT p.Name,
       COALESCE(COUNT(c.Serial), 0) AS BooksCheckedOut,
       CASE 
           WHEN COUNT(c.Serial) > 2 THEN 'Platinum'
           WHEN COUNT(c.Serial) = 2 THEN 'Gold'
           WHEN COUNT(c.Serial) = 1 THEN 'Silver'
           ELSE 'Bronze'
       END AS LoyaltyLevel
FROM Patron p
LEFT JOIN CheckedOut c ON p.CardNum = c.CardNum
GROUP BY p.CardNum, p.Name
ORDER BY BooksCheckedOut DESC;
```
