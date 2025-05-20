Brandon Mountan
CS6016 Database Systems
05/22/25

Part 1 - English to Schema

Problem 1 - Done for us

Problem 2 - A grocery store needs to track an inventory of products for sale

We need to track products with their SKU, name, quantity, and price.

Product [__SKU (string)__, name (string), quantity (integer), price (real)]

SKU is the unique identifier for each product type. Quantity is how much product is in stock.

Problem 3 - Consider grocery store database with products on multiple aisles.

Product can be on multiple aisles but can't have multiple display cases per aisle. So we need two tables.

Product [__SKU (string)__, name (string), price (real)]
ProductLocation [__SKU (string)__, __aisle (integer)__, displayCase (string)]

ProductLocation table uses composite key of SKU and aisle which makes it so a product can't have multiple display cases on the same aisle. Many-to-many relationship

Problem 4 - A car dealership with cars and salespeople

Car [__VIN (string)__, make (string), model (string), year (integer), color (string)]
Salesperson [__SSN (string)__, name (string)]
SalesAssignment [__VIN (string)__, __SSN (string)__]

Car table uses VIN as the primary key so each car is uniquely identified. The Salesperson table uses the SSN as the primary key. The SalesAssignment table has a composite key of VIN and SSN so we know which salespeople are assignment to which cars. Many-to-many relationship.


Part 2 - SQL Table Declarations

CREATE TABLE Patrons (
       CardNum (integer) PRIMARY KEY,
       Name (string)
)

CREATE TABLE Phones (
       CardNum (integer),
       Phone (string),
       PRIMARY KEY (CardNum, Phone),
       FOREIGN KEY (CardNum) REFERENCES Patrons(CardNum)
)

CREATE TABLE Titles (
       ISBN (string) PRIMARY KEY,
       Title (string),
       Author (string)
)

CREATE TABLE Inventory (
       Serial (integer),
       ISBN (string),
       PRIMARY KEY (Serial, ISBN),
       FOREIGN KEY (ISBN) REFERENCES Titles (ISBN)
)

CREATE TABLE CheckedOut (
       CardNum (integer),
       Serial (integer),
       PRIMARY KEY (CardNum, Serial),
       FOREIGN KEY (CardNum) REFERENCES Patrons(CardNum),
       FOREIGN KEY (Serial) REFERENCES Inventory(Serial)
)


Part 3 - Fill in Tables

Car Table

| VIN                | make   | model   | year | color |
|--------------------|--------|---------|------|-------|
| 1HGCM82633A123456  | Toyota | Tacoma  | 2008 | Red   |
| JT3HN86R0X0123457  | Toyota | Tacoma  | 1999 | Green |
| 5YJ3E1EA4JF123458  | Tesla  | Model 3 | 2018 | White |
| JF1VA1A69G9123459  | Subaru | WRX     | 2016 | Blue  |
| 1FTFW1ET3EK123460  | Ford   | F150    | 2004 | Red   |

Salesperson Table

SSN           | name
--------------+-------
123-45-6789   | Arnold
987-65-4321   | Hannah
456-78-9012   | Steve

SalesAssignment Table

VIN                     | SSN
------------------------+-------------
1HGCM82633A123456       | 123-45-6789  (Arnold - Toyota)
1HGCM82633A123456       | 987-65-4321  (Hannah - Red car)
JT3HN86R0X0123457       | 123-45-6789  (Arnold - Toyota)
5YJ3E1EA4JF123458       | 456-78-9012  (Steve - Tesla)
1FTFW1ET3EK123460       | 987-65-4321  (Hannah - Red car)


Part 4 - Keys and Superkeys

Attribute Sets | Superkey? | Proper Subsets                                | Key?
---------------|-----------|----------------------------------------------|------
{A1}           | No        | {}                                           | No
{A2}           | No        | {}                                           | No
{A3}           | No        | {}                                           | No
{A1, A2}       | Yes       | {A1}, {A2}                                   | Yes
{A1, A3}       | No        | {A1}, {A3}                                   | No
{A2, A3}       | No        | {A2}, {A3}                                   | No
{A1, A2, A3}   | Yes       | {A1}, {A2}, {A3}, {A1, A2}, {A1, A3}, {A2, A3} | No


Part 5 - Abstract Reasoning

1. If {x} is a superkey, then any set containing x is also a superkey.
   - TRUE. A superkey uniquely identifies each tuple in a relation. Since {x} already uniquely identifies each tuple, if you add more attributes, it won't matter because it will still be unique, so any superset of {x} will also be a superkey

2. If {x} is a key, then any set containing x is also a key.
   - FALSE. A key must be both a superkey which uniquely identifies tuples AND minimal which means no smaller subset can be a superkey. Any set containing x fails the minimality requirement.

3. If {x} is a key, then {x} is also a superkey
   - TRUE. A key is a minimal superkey so every key is a superkey

4. If {x, y, z} is a superkey, then one of {x}, {y}, or {z} must also be a superkey
   - FALSE. It is possible that only the combination of attributes could be unique and the none of the individual attributes could uniquely identify tuples

5. If an entire schema consists of the set {x, y, z}, and if none of the proper subsets of {x, y, z} are keys, then {x, y, z} must be a key.
   - TRUE. {x, y, z} must be a superkey because every relation has at least one superkey and since no subset is a key, {x, y, z} must be minimal so by definition it must be a key

