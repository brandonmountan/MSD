# Database Systems Homework - Schema Design and Keys

## Part 1 - English to Schema

### Problem 1: Grocery Store Inventory
**Requirement:** A grocery store needs to track an inventory of products for sale. It has zero or more of each type of product for sale, and needs to track the quantity and price for each product. A product has a name and a "stock keeping unit" (SKU).

**Solution:**
```
Product [__SKU (string)__, name (string), quantity (integer), price (real)]
```

### Problem 2: Modified Grocery Store (Aisle Tracking)
**Requirement:** Now we don't care about tracking quantity, but we do want to track which aisle(s) the product is to be displayed on. Sometimes a product is displayed on more than one aisle in special display racks, but the product cannot have multiple display cases per aisle.

**Solution:**
```
Product [__SKU (string)__, name (string), price (real)]
Aisle [__aisleID (integer)__, aisleNumber (string)]
ProductAisle [__SKU (string), aisleID (integer)__]
```

### Problem 3: Car Dealership
**Requirement:** A car has a make, model, year, color, and VIN. A salesperson has a name and a social security number, and is responsible for trying to sell zero or more cars. More than one salesperson can be assigned to any given car, but a car does not necessarily have any salespeople assigned to it.

**Solution:**
```
Car [__VIN (string)__, make (string), model (string), year (integer), color (string)]
Salesperson [__SSN (string)__, name (string)]
CarAssignment [__VIN (string), SSN (string)__]
```

## Part 2 - SQL Table Declarations

```sql
CREATE TABLE Patrons (
   CardNum (integer) PRIMARY KEY,
   Name (string)
);

CREATE TABLE Phones (
   CardNum (integer),
   Phone (string),
   PRIMARY KEY (CardNum, Phone),
   FOREIGN KEY (CardNum) REFERENCES Patrons(CardNum)
);

CREATE TABLE Titles (
   ISBN (string) PRIMARY KEY,
   Title (string),
   Author (string)
);

CREATE TABLE Inventory (
   Serial (integer),
   ISBN (string),
   PRIMARY KEY (Serial, ISBN),
   FOREIGN KEY (ISBN) REFERENCES Titles(ISBN)
);

CREATE TABLE CheckedOut (
   CardNum (integer),
   Serial (integer),
   PRIMARY KEY (CardNum, Serial),
   FOREIGN KEY (CardNum) REFERENCES Patrons(CardNum),
   FOREIGN KEY (Serial) REFERENCES Inventory(Serial)
);
```

## Part 3 - Fill in Tables

### Car Table
| VIN | make | model | year | color |
|-----|------|-------|------|-------|
| 1GCCS148388123456 | Toyota | Tacoma | 2008 | Red |
| 2GCCS148199987654 | Toyota | Tacoma | 1999 | Green |
| 5YJ3E1EA8JF123789 | Tesla | Model 3 | 2018 | White |
| JF1VA1A68G9456123 | Subaru | WRX | 2016 | Blue |
| 1FTPF14594NA78901 | Ford | F150 | 2004 | Red |

### Salesperson Table
| SSN | name |
|-----|------|
| 123-45-6789 | Arnold |
| 987-65-4321 | Hannah |
| 555-12-3456 | Steve |

### CarAssignment Table
| VIN | SSN |
|-----|-----|
| 1GCCS148388123456 | 123-45-6789 |
| 2GCCS148199987654 | 123-45-6789 |
| 1GCCS148388123456 | 987-65-4321 |
| 1FTPF14594NA78901 | 987-65-4321 |
| 5YJ3E1EA8JF123789 | 555-12-3456 |

## Part 4 - Keys and Superkeys

Given instance:
```
A1  A2  A3
x   4.0 q
y   4.0 p
z   3.1 p
z   4.0 p
```

| Attribute Sets | Superkey? | Proper Subsets | Key? |
|----------------|-----------|----------------|------|
| {A1} | No | {} | No |
| {A2} | No | {} | No |
| {A3} | No | {} | No |
| {A1, A2} | Yes | {A1}, {A2} | Yes |
| {A1, A3} | No | {A1}, {A3} | No |
| {A2, A3} | No | {A2}, {A3} | No |
| {A1, A2, A3} | Yes | {A1}, {A2}, {A3}, {A1,A2}, {A1,A3}, {A2,A3} | No |

## Part 5 - Abstract Reasoning

### Statement 1: If {x} is a superkey, then any set containing x is also a superkey.
**Answer: True**

**Explanation:** If {x} can uniquely identify all tuples, then adding more attributes to the set will not remove this uniqueness property. The larger set will still be able to uniquely identify all tuples.

### Statement 2: If {x} is a key, then any set containing x is also a key.
**Answer: False**

**Explanation:** A key is a minimal superkey. If {x} is a key and we add more attributes to create a larger set, that larger set is still a superkey but not a key because it has a proper subset {x} that is also a superkey.

### Statement 3: If {x} is a key, then {x} is also a superkey.
**Answer: True**

**Explanation:** By definition, a key is a minimal superkey. Therefore, every key is also a superkey.

### Statement 4: If {x, y, z} is a superkey, then one of {x}, {y}, or {z} must also be a superkey.
**Answer: False**

**Explanation:** It's possible that {x, y, z} is a superkey but none of its individual attributes are superkeys. The uniqueness might only emerge from the combination of all three attributes together.

### Statement 5: If an entire schema consists of the set {x, y, z}, and if none of the proper subsets of {x, y, z} are keys, then {x, y, z} must be a key.
**Answer: True**

**Explanation:** In any relation, there must be at least one key (since we can always use the set of all attributes as a superkey). If no proper subset of {x, y, z} is a key, and {x, y, z} represents all attributes in the schema, then {x, y, z} must be the minimal superkey, making it a key.